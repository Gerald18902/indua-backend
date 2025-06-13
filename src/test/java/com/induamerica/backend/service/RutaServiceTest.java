package com.induamerica.backend.service;

import com.induamerica.backend.dto.LocalDTO;
import com.induamerica.backend.dto.ReporteTransporteDTO;
import com.induamerica.backend.dto.RutaPersonalizadaDTO;
import com.induamerica.backend.dto.RutaPersonalizadaRequest;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.model.Carga;
import com.induamerica.backend.model.Local;
import com.induamerica.backend.model.PatronAtencion;
import com.induamerica.backend.model.RutaPersonalizada;
import com.induamerica.backend.model.RutaPersonalizadaLocal;
import com.induamerica.backend.model.UnidadTransporte;
import com.induamerica.backend.repository.BultoRepository;
import com.induamerica.backend.repository.CargaRepository;
import com.induamerica.backend.repository.LocalRepository;
import com.induamerica.backend.repository.RutaPersonalizadaLocalRepository;
import com.induamerica.backend.repository.RutaPersonalizadaRepository;
import com.induamerica.backend.repository.UnidadTransporteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RutaServiceTest {

    @Mock
    private RutaPersonalizadaRepository rutaRepo;

    @Mock
    private CargaRepository cargaRepo;

    @Mock
    private BultoRepository bultoRepo;

    @Mock
    private RutaPersonalizadaLocalRepository rutaLocalRepo;

    @Mock
    private UnidadTransporteRepository unidadRepo;

    @Mock
    private LocalRepository localRepo;

    @InjectMocks
    private RutaService rutaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rutaService = new RutaService(
                rutaRepo,
                cargaRepo,
                bultoRepo,
                rutaLocalRepo,
                unidadRepo,
                localRepo);
    }

    // Funcionalidad 1 - Escenario 1
    @Test
    public void listarRutasPersonalizadas_debeRetornarTodasLasRutas() {
        // Entrada simulada
        Carga carga1 = new Carga();
        carga1.setCodigoCarga("CARGA001");
        carga1.setFechaCarga(LocalDate.of(2025, 6, 15));

        UnidadTransporte unidad1 = new UnidadTransporte();
        unidad1.setIdUnidad(1L);
        unidad1.setPlaca("ABC-123");

        RutaPersonalizada ruta1 = new RutaPersonalizada();
        ruta1.setIdRutaPersonalizada(100L);
        ruta1.setCarga(carga1);
        ruta1.setUnidad(unidad1);
        ruta1.setComentario("Primera ruta");

        Carga carga2 = new Carga();
        carga2.setCodigoCarga("CARGA002");
        carga2.setFechaCarga(LocalDate.of(2025, 6, 16));

        UnidadTransporte unidad2 = new UnidadTransporte();
        unidad2.setIdUnidad(2L);
        unidad2.setPlaca("XYZ-789");

        RutaPersonalizada ruta2 = new RutaPersonalizada();
        ruta2.setIdRutaPersonalizada(101L);
        ruta2.setCarga(carga2);
        ruta2.setUnidad(unidad2);
        ruta2.setComentario("Segunda ruta");

        when(rutaRepo.findAll()).thenReturn(List.of(ruta1, ruta2));

        // Prueba
        List<RutaPersonalizadaDTO> resultado = rutaService.listarRutasPersonalizadas(null, null);

        // Resultado esperado
        assertEquals(2, resultado.size());
        assertEquals("CARGA001", resultado.get(0).getCodigoCarga());
        assertEquals("CARGA002", resultado.get(1).getCodigoCarga());
    }

    // Funcionalidad 1 - Escenario 2
    @Test
    public void listarRutasPersonalizadas_debeIgnorarCargasSinRutas() {
        // Solo retornamos rutas asociadas
        Carga carga1 = new Carga();
        carga1.setCodigoCarga("CARGA001");
        carga1.setFechaCarga(LocalDate.of(2025, 6, 15));

        UnidadTransporte unidad1 = new UnidadTransporte();
        unidad1.setIdUnidad(1L);
        unidad1.setPlaca("ABC-123");

        RutaPersonalizada ruta1 = new RutaPersonalizada();
        ruta1.setIdRutaPersonalizada(100L);
        ruta1.setCarga(carga1);
        ruta1.setUnidad(unidad1);
        ruta1.setComentario("Primera ruta");

        // Simula que hay solo una ruta en el repo
        when(rutaRepo.findAll()).thenReturn(List.of(ruta1));

        // Prueba
        List<RutaPersonalizadaDTO> resultado = rutaService.listarRutasPersonalizadas(null, null);

        // Resultado esperado: solo una ruta listada
        assertEquals(1, resultado.size());
        assertEquals("CARGA001", resultado.get(0).getCodigoCarga());
    }

    @Test
    void obtenerLocalesEnFrecuencia_noAsignados_debeRetornarLocales() {
        // Arrange
        Long idCarga = 1L;
        LocalDate fechaCarga = LocalDate.of(2025, 6, 10);
        Carga carga = new Carga();
        carga.setIdCarga(idCarga);
        carga.setFechaCarga(fechaCarga);

        Local local = new Local();
        local.setIdLocal(10L);
        local.setNombre("Local A");
        local.setCodigo("LOC001");

        PatronAtencion patron = new PatronAtencion();
        patron.setJueves(true); // fechaCarga + 2 = jueves
        local.setPatronAtencion(patron);

        Bulto bulto = new Bulto();
        bulto.setLocal(local);

        Mockito.when(cargaRepo.findById(idCarga)).thenReturn(Optional.of(carga));
        Mockito.when(bultoRepo.findByCarga_IdCarga(idCarga)).thenReturn(List.of(bulto));
        Mockito.when(rutaRepo.findAllByCarga_IdCarga(idCarga)).thenReturn(List.of());
        Mockito.when(rutaLocalRepo.findByRutaPersonalizada_IdRutaPersonalizada(Mockito.anyLong()))
                .thenReturn(List.of());

        // Act
        List<LocalDTO> resultado = rutaService.obtenerLocalesPendientesPorAsignar(idCarga);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Local A", resultado.get(0).getNombre());
    }

    @Test
    public void obtenerLocalesEnFrecuencia_sinLocalesFrecuencia_debeRetornarListaVacia() {
        // Arrange
        Long idCarga = 1L;
        LocalDate fechaCarga = LocalDate.of(2025, 6, 10);
        Carga carga = new Carga();
        carga.setIdCarga(idCarga);
        carga.setFechaCarga(fechaCarga);

        when(cargaRepo.findById(idCarga)).thenReturn(Optional.of(carga));

        // Crear patrón que no atiende el jueves (2025-06-12)
        PatronAtencion patron = new PatronAtencion();
        patron.setLunes(false);
        patron.setMartes(false);
        patron.setMiercoles(false);
        patron.setJueves(false);
        patron.setViernes(false);
        patron.setSabado(false);
        patron.setDomingo(false);

        Local local = new Local();
        local.setIdLocal(1L);
        local.setNombre("Local sin atención");
        local.setCodigo("LOC001");
        local.setPatronAtencion(patron);

        Bulto bulto = new Bulto();
        bulto.setLocal(local);
        when(bultoRepo.findByCarga_IdCarga(idCarga)).thenReturn(List.of(bulto));

        // No hay rutas registradas
        when(rutaRepo.findAllByCarga_IdCarga(idCarga)).thenReturn(List.of());

        // Act
        List<LocalDTO> resultado = rutaService.obtenerLocalesPendientesPorAsignar(idCarga);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void obtenerLocalesEnFrecuencia_todosAsignados_debeRetornarListaVacia() {
        // Arrange
        Long idCarga = 1L;
        LocalDate fechaCarga = LocalDate.of(2025, 6, 10);
        Carga carga = new Carga();
        carga.setIdCarga(idCarga);
        carga.setFechaCarga(fechaCarga);
        when(cargaRepo.findById(idCarga)).thenReturn(Optional.of(carga));

        // Día proyectado: Jueves
        PatronAtencion patron = new PatronAtencion();
        patron.setLunes(false);
        patron.setMartes(false);
        patron.setMiercoles(false);
        patron.setJueves(true);
        patron.setViernes(false);
        patron.setSabado(false);
        patron.setDomingo(false);

        Local local = new Local();
        local.setIdLocal(1L);
        local.setNombre("Local asignado");
        local.setCodigo("LOC001");
        local.setPatronAtencion(patron);

        Bulto bulto = new Bulto();
        bulto.setLocal(local);
        when(bultoRepo.findByCarga_IdCarga(idCarga)).thenReturn(List.of(bulto));

        // La ruta ya incluye el local
        RutaPersonalizada ruta = new RutaPersonalizada();
        ruta.setIdRutaPersonalizada(100L);
        ruta.setCarga(carga);
        when(rutaRepo.findAllByCarga_IdCarga(idCarga)).thenReturn(List.of(ruta));

        RutaPersonalizadaLocal rpl = new RutaPersonalizadaLocal();
        rpl.setLocal(local);
        rpl.setRutaPersonalizada(ruta);
        when(rutaLocalRepo.findByRutaPersonalizada_IdRutaPersonalizada(100L)).thenReturn(List.of(rpl));

        // Act
        List<LocalDTO> resultado = rutaService.obtenerLocalesPendientesPorAsignar(idCarga);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void registrarRuta_conLocalesValidos_debeActualizarBultos() {
        // Arrange
        Long idCarga = 1L;
        Long idUnidad = 1L;
        LocalDate fechaCarga = LocalDate.of(2025, 6, 10);
        LocalDate fechaEsperada = fechaCarga.plusDays(2);

        Carga carga = new Carga();
        carga.setIdCarga(idCarga);
        carga.setFechaCarga(fechaCarga);

        UnidadTransporte unidad = new UnidadTransporte();
        unidad.setIdUnidad(idUnidad);
        unidad.setPlaca("ABC123");

        Local local1 = new Local();
        local1.setIdLocal(100L);
        local1.setNombre("Local 1");

        Bulto bulto1 = new Bulto();
        bulto1.setCodigoBulto("BULTO001");
        bulto1.setEstadoRecepcion(Bulto.EstadoRecepcion.EN_BUEN_ESTADO);
        bulto1.setLocal(local1);
        bulto1.setCarga(carga);

        RutaPersonalizadaRequest request = new RutaPersonalizadaRequest();
        request.setIdCarga(idCarga);
        request.setIdUnidad(idUnidad);
        request.setComentario("Primera ruta");
        RutaPersonalizadaRequest.LocalOrdenado localOrdenado = new RutaPersonalizadaRequest.LocalOrdenado();
        localOrdenado.setIdLocal(local1.getIdLocal());
        localOrdenado.setOrden(1);
        request.setLocalesOrdenados(List.of(localOrdenado));

        when(cargaRepo.findById(idCarga)).thenReturn(Optional.of(carga));
        when(unidadRepo.findById(idUnidad)).thenReturn(Optional.of(unidad));
        when(localRepo.findById(local1.getIdLocal())).thenReturn(Optional.of(local1));
        when(bultoRepo.findByCarga_IdCarga(idCarga)).thenReturn(List.of(bulto1));

        // Act
        Map<String, Integer> resultado = rutaService.registrarRutaPersonalizada(request);

        // Assert
        assertEquals(1, resultado.get("actualizados"));
        assertEquals(0, resultado.get("omitidos"));
        verify(bultoRepo).save(bulto1);
        assertEquals(Bulto.EstadoTransporte.EN_CAMINO, bulto1.getEstadoTransporte());
        assertEquals(fechaEsperada, bulto1.getFechaTransporte());
    }

    @Test
    void registrarRuta_conLocalesConFaltantes_debeOmitirFaltantes() {
        // Arrange
        Long idCarga = 1L;
        Long idUnidad = 1L;
        LocalDate fechaCarga = LocalDate.of(2025, 6, 10);
        LocalDate fechaEsperada = fechaCarga.plusDays(2);

        Carga carga = new Carga();
        carga.setIdCarga(idCarga);
        carga.setFechaCarga(fechaCarga);

        UnidadTransporte unidad = new UnidadTransporte();
        unidad.setIdUnidad(idUnidad);
        unidad.setPlaca("ABC123");

        Local local = new Local();
        local.setIdLocal(100L);
        local.setNombre("Local F");

        Bulto bultoValido = new Bulto();
        bultoValido.setCodigoBulto("BULTO001");
        bultoValido.setEstadoRecepcion(Bulto.EstadoRecepcion.EN_BUEN_ESTADO);
        bultoValido.setLocal(local);
        bultoValido.setCarga(carga);

        Bulto bultoFaltante = new Bulto();
        bultoFaltante.setCodigoBulto("BULTO002");
        bultoFaltante.setEstadoRecepcion(Bulto.EstadoRecepcion.FALTANTE);
        bultoFaltante.setLocal(local);
        bultoFaltante.setCarga(carga);

        RutaPersonalizadaRequest request = new RutaPersonalizadaRequest();
        request.setIdCarga(idCarga);
        request.setIdUnidad(idUnidad);
        request.setComentario("Ruta con faltantes");
        RutaPersonalizadaRequest.LocalOrdenado lo = new RutaPersonalizadaRequest.LocalOrdenado();
        lo.setIdLocal(local.getIdLocal());
        lo.setOrden(1);
        request.setLocalesOrdenados(List.of(lo));

        when(cargaRepo.findById(idCarga)).thenReturn(Optional.of(carga));
        when(unidadRepo.findById(idUnidad)).thenReturn(Optional.of(unidad));
        when(localRepo.findById(local.getIdLocal())).thenReturn(Optional.of(local));
        when(bultoRepo.findByCarga_IdCarga(idCarga)).thenReturn(List.of(bultoValido, bultoFaltante));

        // Act
        Map<String, Integer> resultado = rutaService.registrarRutaPersonalizada(request);

        // Assert
        assertEquals(1, resultado.get("actualizados"));
        assertEquals(1, resultado.get("omitidos"));
        verify(bultoRepo).save(bultoValido);
        assertEquals(Bulto.EstadoTransporte.EN_CAMINO, bultoValido.getEstadoTransporte());
        assertEquals(fechaEsperada, bultoValido.getFechaTransporte());
    }

    @Test
    void obtenerLocalesPorRuta_rutaConLocalesAsignados_debeRetornarLocalesOrdenados() {
        // Arrange
        Long idRuta = 1L;

        Local localA = new Local();
        localA.setIdLocal(1L);
        localA.setCodigo("LOC-A");
        localA.setNombre("Local A");

        Local localB = new Local();
        localB.setIdLocal(2L);
        localB.setCodigo("LOC-B");
        localB.setNombre("Local B");

        Local localC = new Local();
        localC.setIdLocal(3L);
        localC.setCodigo("LOC-C");
        localC.setNombre("Local C");

        RutaPersonalizada ruta = new RutaPersonalizada();
        ruta.setIdRutaPersonalizada(idRuta);

        RutaPersonalizadaLocal rplA = new RutaPersonalizadaLocal();
        rplA.setRutaPersonalizada(ruta);
        rplA.setLocal(localA);
        rplA.setOrden(2);

        RutaPersonalizadaLocal rplB = new RutaPersonalizadaLocal();
        rplB.setRutaPersonalizada(ruta);
        rplB.setLocal(localB);
        rplB.setOrden(1);

        RutaPersonalizadaLocal rplC = new RutaPersonalizadaLocal();
        rplC.setRutaPersonalizada(ruta);
        rplC.setLocal(localC);
        rplC.setOrden(3);

        when(rutaLocalRepo.findByRutaPersonalizada_IdRutaPersonalizadaOrderByOrdenAsc(idRuta))
                .thenReturn(List.of(rplB, rplA, rplC)); // Ya ordenado según el método

        // Act
        List<LocalDTO> resultado = rutaService.obtenerLocalesPorRuta(idRuta);

        // Assert
        assertEquals(3, resultado.size());
        assertEquals("Local B", resultado.get(0).getNombre());
        assertEquals("Local A", resultado.get(1).getNombre());
        assertEquals("Local C", resultado.get(2).getNombre());
    }

    @Test
    void obtenerLocalesPorRuta_rutaSinLocalesAsignados_debeRetornarListaVacia() {
        // Arrange
        Long idRuta = 2L;

        when(rutaLocalRepo.findByRutaPersonalizada_IdRutaPersonalizadaOrderByOrdenAsc(idRuta))
                .thenReturn(List.of());

        // Act
        List<LocalDTO> resultado = rutaService.obtenerLocalesPorRuta(idRuta);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerReporteTransporte_cargaConMultiplesRutas_debeRetornarReporteCompleto() {
        // Arrange
        Long idCarga = 1L;
        Carga carga = new Carga();
        carga.setIdCarga(idCarga);
        carga.setCodigoCarga("CARGA-001");
        carga.setFechaCarga(LocalDate.of(2025, 6, 13));

        when(cargaRepo.findById(idCarga)).thenReturn(Optional.of(carga));

        RutaPersonalizada ruta1 = new RutaPersonalizada();
        ruta1.setIdRutaPersonalizada(100L);
        ruta1.setCarga(carga);
        ruta1.setComentario("Primera ruta");
        UnidadTransporte unidad1 = new UnidadTransporte();
        unidad1.setPlaca("ABC-123");
        ruta1.setUnidad(unidad1);

        RutaPersonalizada ruta2 = new RutaPersonalizada();
        ruta2.setIdRutaPersonalizada(101L);
        ruta2.setCarga(carga);
        ruta2.setComentario("Segunda ruta");
        UnidadTransporte unidad2 = new UnidadTransporte();
        unidad2.setPlaca("XYZ-456");
        ruta2.setUnidad(unidad2);

        when(rutaRepo.findAllByCarga_IdCarga(idCarga)).thenReturn(List.of(ruta1, ruta2));

        // Simular locales para cada ruta
        Local local1 = new Local();
        local1.setIdLocal(1L);
        local1.setCodigo("LOC1");
        local1.setNombre("Local Uno");

        Local local2 = new Local();
        local2.setIdLocal(2L);
        local2.setCodigo("LOC2");
        local2.setNombre("Local Dos");

        when(rutaLocalRepo.findByRutaPersonalizada_IdRutaPersonalizadaOrderByOrdenAsc(100L))
                .thenReturn(List.of(new RutaPersonalizadaLocal() {
                    {
                        setLocal(local1);
                    }
                }));

        when(rutaLocalRepo.findByRutaPersonalizada_IdRutaPersonalizadaOrderByOrdenAsc(101L))
                .thenReturn(List.of(new RutaPersonalizadaLocal() {
                    {
                        setLocal(local2);
                    }
                }));

        // Act
        ReporteTransporteDTO reporte = rutaService.obtenerReporteTransporte(idCarga);

        // Assert
        assertNotNull(reporte);
        assertEquals("CARGA-001", reporte.getCodigoCarga());
        assertEquals("2025-06-13", reporte.getFechaCarga());
        assertEquals(2, reporte.getRutas().size());

        assertEquals("ABC-123", reporte.getRutas().get(0).getPlaca());
        assertEquals("Local Uno", reporte.getRutas().get(0).getLocales().get(0).getNombre());

        assertEquals("XYZ-456", reporte.getRutas().get(1).getPlaca());
        assertEquals("Local Dos", reporte.getRutas().get(1).getLocales().get(0).getNombre());
    }

    @Test
    void reporteCargaSinRutas_debeRetornarDTOConListaVacia() {
        // Arrange
        Long idCarga = 1L;
        Carga carga = new Carga();
        carga.setIdCarga(idCarga);
        carga.setCodigoCarga("CARGA001");
        carga.setFechaCarga(LocalDate.of(2025, 6, 15));

        when(cargaRepo.findById(idCarga)).thenReturn(Optional.of(carga));
        when(rutaRepo.findAllByCarga_IdCarga(idCarga)).thenReturn(List.of());

        // Act
        ReporteTransporteDTO reporte = rutaService.obtenerReporteTransporte(idCarga);

        // Assert
        assertEquals("CARGA001", reporte.getCodigoCarga());
        assertEquals("2025-06-15", reporte.getFechaCarga());
        assertTrue(reporte.getRutas().isEmpty(), "La lista de rutas debe estar vacía");
    }
}
