package com.induamerica.backend.service;

import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.repository.BultoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BultoServiceTest {

    @Mock
    private BultoRepository bultoRepository;

    @InjectMocks
    private BultoService bultoService;

    @Test
    void actualizarEstadoRecepcion_bultoExistente_estadoBuenEstado() {
        // Funcionalidad: actualizarEstadoRecepcion
        // Escenario 1: Bulto existe y se actualiza a EN_BUEN_ESTADO

        // Entrada simulada
        String codigoBulto = "BANSA0013554629";
        String nuevoEstado = "EN_BUEN_ESTADO";

        Bulto bulto = new Bulto();
        bulto.setCodigoBulto(codigoBulto);

        when(bultoRepository.findByCodigoBulto(codigoBulto)).thenReturn(bulto);

        // Prueba
        bultoService.actualizarEstadoRecepcion(codigoBulto, nuevoEstado);

        // Resultado esperado
        assertEquals(Bulto.EstadoRecepcion.EN_BUEN_ESTADO, bulto.getEstadoRecepcion());
        assertEquals(Bulto.EstadoTransporte.EN_ALMACEN, bulto.getEstadoTransporte());
        verify(bultoRepository).save(bulto);
    }

    @Test
    void actualizarEstadoRecepcion_bultoNoExiste_lanzaExcepcion() {
        // Funcionalidad: actualizarEstadoRecepcion
        // Escenario 2: Bulto no existe en base de datos

        // Entrada simulada
        String codigoBulto = "B999";
        String nuevoEstado = "FALTANTE";

        // Simulación
        when(bultoRepository.findByCodigoBulto(codigoBulto)).thenReturn(null);

        // Prueba + Resultado esperado
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bultoService.actualizarEstadoRecepcion(codigoBulto, nuevoEstado);
        });

        assertEquals("Bulto no encontrado: B999", exception.getMessage());
        verify(bultoRepository, never()).save(any());
    }

    @Test
    void actualizarEstadoRecepcion_estadoFaltante_dejaTransporteEnNull() {
        // Funcionalidad: actualizarEstadoRecepcion
        // Escenario 3: estado = FALTANTE → estadoTransporte debe quedar null

        // Entrada simulada
        String codigoBulto = "BANSA0013554629";
        String nuevoEstado = "FALTANTE";

        Bulto bulto = new Bulto();
        bulto.setCodigoBulto(codigoBulto);

        when(bultoRepository.findByCodigoBulto(codigoBulto)).thenReturn(bulto);

        // Prueba
        bultoService.actualizarEstadoRecepcion(codigoBulto, nuevoEstado);

        // Resultado esperado
        assertEquals(Bulto.EstadoRecepcion.FALTANTE, bulto.getEstadoRecepcion());
        assertNull(bulto.getEstadoTransporte());

        verify(bultoRepository).save(bulto);
    }

    @Test
    void actualizarEstadoRecepcion_estadoBuenEstado_seteaTransporteEnAlmacen() {
        // Funcionalidad: actualizarEstadoRecepcion
        // Escenario 4: estado = EN_BUEN_ESTADO → estadoTransporte debe ser EN_ALMACEN

        // Entrada simulada
        String codigoBulto = "BANSA0013554630";
        String nuevoEstado = "EN_BUEN_ESTADO";

        Bulto bulto = new Bulto();
        bulto.setCodigoBulto(codigoBulto);

        when(bultoRepository.findByCodigoBulto(codigoBulto)).thenReturn(bulto);

        // Prueba
        bultoService.actualizarEstadoRecepcion(codigoBulto, nuevoEstado);

        // Resultado esperado
        assertEquals(Bulto.EstadoRecepcion.EN_BUEN_ESTADO, bulto.getEstadoRecepcion());
        assertEquals(Bulto.EstadoTransporte.EN_ALMACEN, bulto.getEstadoTransporte());

        verify(bultoRepository).save(bulto);
    }

    @Test
    public void testCompletarCarga_ActualizaSoloBultosSinEstado() {
        // Datos simulados
        String codigoCarga = "OTM0000032098";

        Bulto bulto1 = new Bulto();
        bulto1.setCodigoBulto("BANSA0013554629");
        bulto1.setEstadoRecepcion(null);

        Bulto bulto2 = new Bulto();
        bulto2.setCodigoBulto("BANSA0013554630");
        bulto2.setEstadoRecepcion(Bulto.EstadoRecepcion.DETERIORADO);

        Bulto bulto3 = new Bulto();
        bulto3.setCodigoBulto("BANSA0013554631");
        bulto3.setEstadoRecepcion(null);

        List<Bulto> lista = Arrays.asList(bulto1, bulto2, bulto3);

        // Simular comportamiento del repositorio
        when(bultoRepository.findByCargaCodigoCarga(codigoCarga)).thenReturn(lista);

        // Ejecutar método
        bultoService.completarCarga(codigoCarga);

        // Verificar que solo los bultos sin estado fueron actualizados
        assertEquals(Bulto.EstadoRecepcion.EN_BUEN_ESTADO, bulto1.getEstadoRecepcion());
        assertEquals(Bulto.EstadoTransporte.EN_ALMACEN, bulto1.getEstadoTransporte());

        assertEquals(Bulto.EstadoRecepcion.DETERIORADO, bulto2.getEstadoRecepcion());
        assertNull(bulto2.getEstadoTransporte());

        assertEquals(Bulto.EstadoRecepcion.EN_BUEN_ESTADO, bulto3.getEstadoRecepcion());
        assertEquals(Bulto.EstadoTransporte.EN_ALMACEN, bulto3.getEstadoTransporte());

        // Verificar que se llamó al método saveAll con la lista modificada
        verify(bultoRepository, times(1)).saveAll(lista);
    }

    @Test
    public void testCompletarCarga_BultosYaActualizados_NoModificaEstados() {
        // Datos simulados
        String codigoCarga = "OTM0000032098";

        Bulto bulto1 = new Bulto();
        bulto1.setCodigoBulto("BANSA0013554629");
        bulto1.setEstadoRecepcion(Bulto.EstadoRecepcion.EN_BUEN_ESTADO);

        Bulto bulto2 = new Bulto();
        bulto2.setCodigoBulto("BANSA0013554630");
        bulto2.setEstadoRecepcion(Bulto.EstadoRecepcion.DETERIORADO);

        List<Bulto> lista = Arrays.asList(bulto1, bulto2);

        when(bultoRepository.findByCargaCodigoCarga(codigoCarga)).thenReturn(lista);

        // Ejecutar método
        bultoService.completarCarga(codigoCarga);

        // Verificar que los estados no fueron cambiados
        assertEquals(Bulto.EstadoRecepcion.EN_BUEN_ESTADO, bulto1.getEstadoRecepcion());
        assertEquals(Bulto.EstadoRecepcion.DETERIORADO, bulto2.getEstadoRecepcion());

        // Verificar que se llama igual a saveAll (puede ser necesario si queremos
        // garantizar persistencia)
        verify(bultoRepository, times(1)).saveAll(lista);
    }

    @Test
    public void testTerminarCarga_AlgunosBultosSinEstado_MarcadosComoFaltantes() {
        // Datos simulados
        String codigoCarga = "OTM0000032098";

        Bulto bulto1 = new Bulto();
        bulto1.setCodigoBulto("BANSA0013554629");
        bulto1.setEstadoRecepcion(null); // debe actualizarse

        Bulto bulto2 = new Bulto();
        bulto2.setCodigoBulto("BANSA0013554630");
        bulto2.setEstadoRecepcion(Bulto.EstadoRecepcion.EN_BUEN_ESTADO); // se mantiene

        List<Bulto> lista = Arrays.asList(bulto1, bulto2);

        when(bultoRepository.findByCargaCodigoCarga(codigoCarga)).thenReturn(lista);

        // Ejecutar método
        bultoService.terminarCarga(codigoCarga);

        // Verificaciones
        assertEquals(Bulto.EstadoRecepcion.FALTANTE, bulto1.getEstadoRecepcion());
        assertEquals(Bulto.EstadoRecepcion.EN_BUEN_ESTADO, bulto2.getEstadoRecepcion());

        verify(bultoRepository, times(1)).saveAll(lista);
    }

    @Test
    public void testTerminarCarga_TodosBultosConEstado_NoSeModifican() {
        // Datos simulados
        String codigoCarga = "CARGA456";

        Bulto bulto1 = new Bulto();
        bulto1.setCodigoBulto("BULTO1");
        bulto1.setEstadoRecepcion(Bulto.EstadoRecepcion.DETERIORADO);

        Bulto bulto2 = new Bulto();
        bulto2.setCodigoBulto("BULTO2");
        bulto2.setEstadoRecepcion(Bulto.EstadoRecepcion.EN_BUEN_ESTADO);

        List<Bulto> lista = Arrays.asList(bulto1, bulto2);

        when(bultoRepository.findByCargaCodigoCarga(codigoCarga)).thenReturn(lista);

        // Ejecutar método
        bultoService.terminarCarga(codigoCarga);

        // Verificaciones
        assertEquals(Bulto.EstadoRecepcion.DETERIORADO, bulto1.getEstadoRecepcion());
        assertEquals(Bulto.EstadoRecepcion.EN_BUEN_ESTADO, bulto2.getEstadoRecepcion());

        verify(bultoRepository, times(1)).saveAll(lista); // el método igual guarda (como en tu implementación actual)
    }

}
