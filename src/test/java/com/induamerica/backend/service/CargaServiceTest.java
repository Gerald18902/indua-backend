package com.induamerica.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.induamerica.backend.dto.ReporteRecepcionDTO;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.model.Carga;
import com.induamerica.backend.model.Local;
import com.induamerica.backend.repository.BultoRepository;
import com.induamerica.backend.repository.CargaRepository;

@ExtendWith(MockitoExtension.class)
public class CargaServiceTest {
    @Mock
    private CargaRepository cargaRepository;

    @Mock
    private BultoRepository bultoRepository;

    @InjectMocks
    private CargaService cargaService;

    @Test
    void testGenerarReporteRecepcion_conBultos() {
        // Arrange
        Carga carga = new Carga();
        carga.setIdCarga(1L);
        carga.setCodigoCarga("CARGA001");

        Local local = new Local();
        local.setCodigo("LOC001");
        local.setNombre("Sucursal A");

        Bulto b1 = new Bulto();
        b1.setCodigoBulto("BULTO1");
        b1.setEstadoRecepcion(Bulto.EstadoRecepcion.EN_BUEN_ESTADO);
        b1.setLocal(local);
        b1.setCarga(carga);

        Bulto b2 = new Bulto();
        b2.setCodigoBulto("BULTO2");
        b2.setEstadoRecepcion(Bulto.EstadoRecepcion.DETERIORADO);
        b2.setLocal(local);
        b2.setCarga(carga);

        Bulto b3 = new Bulto();
        b3.setCodigoBulto("BULTO3");
        b3.setEstadoRecepcion(Bulto.EstadoRecepcion.FALTANTE);
        b3.setLocal(local);
        b3.setCarga(carga);

        List<Bulto> bultos = Arrays.asList(b1, b2, b3);

        when(bultoRepository.findByCargaIdCarga(1L)).thenReturn(bultos);

        // Act
        ReporteRecepcionDTO reporte = cargaService.generarReporteRecepcion(1L);

        // Assert
        assertEquals(3, reporte.getTotal());
        assertEquals(33.33, Math.round(reporte.getPorcentajeBuenEstado() * 100.0) / 100.0);
        assertEquals(33.33, Math.round(reporte.getPorcentajeDeteriorado() * 100.0) / 100.0);
        assertEquals(33.33, Math.round(reporte.getPorcentajeFaltante() * 100.0) / 100.0);
        assertEquals(2, reporte.getBultosProblema().size());
    }

    @Test
    void testGenerarReporteRecepcion_cargaVacia() {
        // Arrange
        Long idCarga = 2L;

        when(bultoRepository.findByCargaIdCarga(idCarga)).thenReturn(List.of());

        // Act
        ReporteRecepcionDTO reporte = cargaService.generarReporteRecepcion(idCarga);

        // Assert
        assertEquals(0, reporte.getTotal());
        assertEquals(0.0, reporte.getPorcentajeBuenEstado());
        assertEquals(0.0, reporte.getPorcentajeDeteriorado());
        assertEquals(0.0, reporte.getPorcentajeFaltante());
        assertEquals(0, reporte.getBultosProblema().size());
    }

}
