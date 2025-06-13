package com.induamerica.backend.service;

import com.induamerica.backend.dto.RegistrarActaRequest;
import com.induamerica.backend.model.Acta;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.model.Acta.TipoMerma;
import com.induamerica.backend.model.Bulto.EstadoDespacho;
import com.induamerica.backend.repository.ActaRepository;
import com.induamerica.backend.repository.BultoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class ActaServiceTest {

    @InjectMocks
    private ActaService actaService;

    @Mock
    private ActaRepository actaRepository;

    @Mock
    private BultoRepository bultoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrarActa_BultoNoExiste() throws Exception {
        // Arrange
        RegistrarActaRequest request = new RegistrarActaRequest();
        request.setCodigoBulto("BANSA0013554629");

        when(bultoRepository.findByCodigoBulto("BANSA0013554629")).thenReturn(null);

        // Act
        ResponseEntity<?> response = actaService.registrarActa(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El código de bulto no existe.", response.getBody());
    }

    @Test
    void testRegistrarActa_YaExisteActa() throws Exception {
        // Arrange
        RegistrarActaRequest request = new RegistrarActaRequest();
        request.setCodigoBulto("BANSA0013554629");

        when(bultoRepository.findByCodigoBulto("BANSA0013554629")).thenReturn(new Bulto());
        when(actaRepository.existsByCodigoBulto("BANSA0013554629")).thenReturn(true);

        // Act
        ResponseEntity<?> response = actaService.registrarActa(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Ya existe un acta registrada para este código de bulto.", response.getBody());
    }

    @Test
    void testRegistrarActa_ExitosaParaDeteriorado() throws Exception {
        // Arrange
        RegistrarActaRequest request = new RegistrarActaRequest();
        request.setCodigoBulto("BANSA0013554629");
        request.setTipoMerma(TipoMerma.DETERIORADO);
        request.setFechaIncidencia(LocalDate.of(2025, 6, 12));
        request.setNumeroActa("16");
        request.setNombre("Ninet Suave Cuidado");
        request.setNombreAuxiliar("Ana");
        request.setCantidad(5);

        Bulto bulto = new Bulto();
        bulto.setCodigoBulto("BANSA0013554629");

        when(bultoRepository.findByCodigoBulto("BANSA0013554629")).thenReturn(bulto);
        when(actaRepository.existsByCodigoBulto("BANSA0013554629")).thenReturn(false);

        // Act
        ResponseEntity<?> response = actaService.registrarActa(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Acta registrada correctamente", response.getBody());

        verify(bultoRepository).save(argThat(b -> b.getEstadoDespacho() == EstadoDespacho.DETERIORADO &&
                b.getFechaDespacho().equals(LocalDate.of(2025, 6, 12))));

        verify(actaRepository).save(any(Acta.class));
    }

    @Test
    void testRegistrarActa_ExitosaParaFaltante() throws Exception {
        // Arrange
        RegistrarActaRequest request = new RegistrarActaRequest();
        request.setCodigoBulto("BANSA0013554629");
        request.setTipoMerma(TipoMerma.FALTANTE);
        request.setFechaIncidencia(LocalDate.of(2025, 6, 12));
        request.setNumeroActa("BANSA0013554629");
        request.setNombre("Enfagrow");
        request.setNombreAuxiliar("Pedro Alva");
        request.setCantidad(3);

        Bulto bulto = new Bulto();
        bulto.setCodigoBulto("B999");

        when(bultoRepository.findByCodigoBulto("BANSA0013554629")).thenReturn(bulto);
        when(actaRepository.existsByCodigoBulto("BANSA0013554629")).thenReturn(false);

        // Act
        ResponseEntity<?> response = actaService.registrarActa(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Acta registrada correctamente", response.getBody());

        verify(bultoRepository).save(argThat(b -> b.getEstadoDespacho() == EstadoDespacho.FALTANTE &&
                b.getFechaDespacho() == null));

        verify(actaRepository).save(any(Acta.class));
    }

    @Test
    void testRegistrarActa_ExitosaParaDiscrepancia() throws Exception {
        // Arrange
        RegistrarActaRequest request = new RegistrarActaRequest();
        request.setCodigoBulto("BANSA0013554629");
        request.setTipoMerma(TipoMerma.DISCREPANCIA);
        request.setFechaIncidencia(LocalDate.of(2025, 6, 12));
        request.setNumeroActa("BANSA0013554629");
        request.setNombre("Doloneurobion");
        request.setNombreAuxiliar("Jose Soto");
        request.setCantidad(2);

        Bulto bulto = new Bulto();
        bulto.setCodigoBulto("BANSA0013554629");

        when(bultoRepository.findByCodigoBulto("BANSA0013554629")).thenReturn(bulto);
        when(actaRepository.existsByCodigoBulto("BANSA0013554629")).thenReturn(false);

        // Act
        ResponseEntity<?> response = actaService.registrarActa(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Acta registrada correctamente", response.getBody());

        verify(bultoRepository).save(argThat(b -> b.getEstadoDespacho() == EstadoDespacho.DISCREPANCIA &&
                b.getFechaDespacho().equals(LocalDate.of(2025, 6, 12))));

        verify(actaRepository).save(any(Acta.class));
    }

}
