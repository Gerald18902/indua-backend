package com.induamerica.backend.service;

import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.model.Carga;
import com.induamerica.backend.model.Local;
import com.induamerica.backend.repository.BultoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.argThat;

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

    @Test
    void testActualizarDespachoMasivo_SoloActualizaBultosSinEstadoDespacho() {
        // Funcionalidad: actualizarDespachoMasivo
        // Escenario: Solo se actualizan bultos cuyo estadoDespacho es null

        // Datos simulados
        String codigo1 = "BULTO1";
        String codigo2 = "BULTO2";
        String codigo3 = "BULTO3";

        Bulto b1 = new Bulto();
        b1.setCodigoBulto(codigo1);
        b1.setEstadoDespacho(null);

        Bulto b2 = new Bulto();
        b2.setCodigoBulto(codigo2);
        b2.setEstadoDespacho(Bulto.EstadoDespacho.DETERIORADO); // ya tiene estado

        Bulto b3 = new Bulto();
        b3.setCodigoBulto(codigo3);
        b3.setEstadoDespacho(null);

        when(bultoRepository.findByCodigoBulto(codigo1)).thenReturn(b1);
        when(bultoRepository.findByCodigoBulto(codigo2)).thenReturn(b2);
        when(bultoRepository.findByCodigoBulto(codigo3)).thenReturn(b3);

        // Ejecutar método
        List<String> codigos = Arrays.asList(codigo1, codigo2, codigo3);
        bultoService.actualizarDespachoMasivo(codigos, Bulto.EstadoDespacho.ENTREGADO_EN_BUEN_ESTADO);

        // Verificar resultados esperados
        assertEquals(Bulto.EstadoDespacho.ENTREGADO_EN_BUEN_ESTADO, b1.getEstadoDespacho());
        assertEquals(LocalDate.now(), b1.getFechaDespacho());

        assertEquals(Bulto.EstadoDespacho.DETERIORADO, b2.getEstadoDespacho()); // no cambia
        assertNull(b2.getFechaDespacho());

        assertEquals(Bulto.EstadoDespacho.ENTREGADO_EN_BUEN_ESTADO, b3.getEstadoDespacho());
        assertEquals(LocalDate.now(), b3.getFechaDespacho());

        // Verificar que solo b1 y b3 se guardaron
        verify(bultoRepository, times(1)).save(b1);
        verify(bultoRepository, times(1)).save(b3);
        verify(bultoRepository, never()).save(b2);
    }

    @Test
    void testAsignarFechaTransporte_ActualizaBultosEnAlmacenCorrectamente() {
        // Datos simulados
        String nombreLocal = "TRUJILLO 12";
        String codigoCarga = "OTM0000043626";
        LocalDate nuevaFecha = LocalDate.of(2025, 6, 12);

        // Mocks de Local y Carga
        Local localMock = mock(Local.class);
        when(localMock.getNombre()).thenReturn(nombreLocal);

        Carga cargaMock = mock(Carga.class);
        when(cargaMock.getCodigoCarga()).thenReturn(codigoCarga);

        // Bulto 1 (sí se debe actualizar)
        Bulto b1 = new Bulto();
        b1.setCodigoBulto("BANSA0013554629");
        b1.setEstadoTransporte(Bulto.EstadoTransporte.EN_ALMACEN);
        b1.setFechaTransporte(null);
        b1.setLocal(localMock);
        b1.setCarga(cargaMock);

        // Bulto 2 (sí se debe actualizar)
        Bulto b2 = new Bulto();
        b2.setCodigoBulto("BANSA0013554630");
        b2.setEstadoTransporte(Bulto.EstadoTransporte.EN_ALMACEN);
        b2.setFechaTransporte(null);
        b2.setLocal(localMock);
        b2.setCarga(cargaMock);

        // Bulto 3 (NO debe ser actualizado)
        Bulto b3 = new Bulto();
        b3.setCodigoBulto("BANSA0013554631");
        b3.setEstadoTransporte(Bulto.EstadoTransporte.EN_CAMINO); // ya tiene estado distinto
        b3.setFechaTransporte(null);
        b3.setLocal(localMock);
        b3.setCarga(cargaMock);

        List<Bulto> lista = Arrays.asList(b1, b2, b3);

        when(bultoRepository.findAll()).thenReturn(lista);

        // Ejecutar método
        String mensaje = bultoService.asignarFechaTransporte(nombreLocal, codigoCarga, nuevaFecha);

        // Verificaciones
        assertEquals("Fecha de transporte asignada correctamente a 2 bultos.", mensaje);
        assertEquals(Bulto.EstadoTransporte.EN_CAMINO, b1.getEstadoTransporte());
        assertEquals(nuevaFecha, b1.getFechaTransporte());

        assertEquals(Bulto.EstadoTransporte.EN_CAMINO, b2.getEstadoTransporte());
        assertEquals(nuevaFecha, b2.getFechaTransporte());

        assertEquals(Bulto.EstadoTransporte.EN_CAMINO, b3.getEstadoTransporte()); // este no debería haber sido cambiado
        assertNull(b3.getFechaTransporte()); // ni esta asignada

        verify(bultoRepository, times(1)).saveAll(argThat(bultosGuardados -> {
            List<Bulto> list = new java.util.ArrayList<>();
            bultosGuardados.forEach(list::add);
            return list.contains(b1) &&
                    list.contains(b2) &&
                    !list.contains(b3);
        }));
    }

    @Test
    void testAsignarFechaTransporte_SinCoincidencias_NoActualiza() {
        // Datos simulados
        String nombreLocal = "TRUJILLO 12";
        String codigoCarga = "OTM0000043626";
        LocalDate fecha = LocalDate.of(2025, 6, 13);

        // Bultos simulados (ninguno con EN_ALMACEN o con local/carga distintos)
        Bulto b1 = new Bulto();
        b1.setCodigoBulto("BANSA0013554629");
        b1.setEstadoTransporte(Bulto.EstadoTransporte.EN_CAMINO);

        Local localMock = mock(Local.class);
        when(localMock.getNombre()).thenReturn("MALL");
        b1.setLocal(localMock);

        Carga cargaMock = mock(Carga.class);
        b1.setCarga(cargaMock);

        when(bultoRepository.findAll()).thenReturn(List.of(b1));

        // Ejecutar método
        String mensaje = bultoService.asignarFechaTransporte(nombreLocal, codigoCarga, fecha);

        // Validaciones
        assertEquals("Fecha de transporte asignada correctamente a 0 bultos.", mensaje);
        verify(bultoRepository, never()).saveAll(any());
    }

}
