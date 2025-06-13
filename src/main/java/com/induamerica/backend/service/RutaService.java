package com.induamerica.backend.service;

import com.induamerica.backend.dto.LocalDTO;
import com.induamerica.backend.dto.LocalMapaDTO;
import com.induamerica.backend.dto.RutaPersonalizadaDTO;
import com.induamerica.backend.dto.RutaPersonalizadaRequest;
import com.induamerica.backend.dto.ReporteTransporteDTO;
import com.induamerica.backend.dto.RutaMapaDTO;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.model.Bulto.EstadoRecepcion;
import com.induamerica.backend.model.Bulto.EstadoTransporte;
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RutaService {

        private final RutaPersonalizadaRepository rutaRepo;
        private final CargaRepository cargaRepo;
        private final BultoRepository bultoRepo;
        private final RutaPersonalizadaLocalRepository rutaLocalRepo;
        private final UnidadTransporteRepository unidadRepo;
        private final LocalRepository localRepo;

        public RutaService(
                        RutaPersonalizadaRepository rutaRepo,
                        CargaRepository cargaRepo,
                        BultoRepository bultoRepo,
                        RutaPersonalizadaLocalRepository rutaLocalRepo,
                        UnidadTransporteRepository unidadRepo,
                        LocalRepository localRepo) {
                this.rutaRepo = rutaRepo;
                this.cargaRepo = cargaRepo;
                this.bultoRepo = bultoRepo;
                this.rutaLocalRepo = rutaLocalRepo;
                this.unidadRepo = unidadRepo;
                this.localRepo = localRepo;
        }

        public List<RutaPersonalizadaDTO> listarRutasPersonalizadas(LocalDate fecha, String codigoCarga) {
                List<RutaPersonalizada> rutas;

                if (fecha != null && codigoCarga != null) {
                        rutas = rutaRepo.findAll().stream()
                                        .filter(r -> r.getCarga().getFechaCarga().equals(fecha) &&
                                                        r.getCarga().getCodigoCarga().toLowerCase()
                                                                        .contains(codigoCarga.toLowerCase()))
                                        .collect(Collectors.toList());
                } else if (fecha != null) {
                        rutas = rutaRepo.findByCarga_FechaCarga(fecha);
                } else if (codigoCarga != null) {
                        rutas = rutaRepo.findByCarga_CodigoCargaContainingIgnoreCase(codigoCarga);
                } else {
                        rutas = rutaRepo.findAll();
                }

                return rutas.stream().map(r -> new RutaPersonalizadaDTO(
                                r.getIdRutaPersonalizada(),
                                r.getCarga().getCodigoCarga(),
                                r.getCarga().getFechaCarga(),
                                r.getUnidad().getPlaca(),
                                r.getUnidad().getIdUnidad(),
                                r.getComentario())).collect(Collectors.toList());
        }

        public List<Carga> obtenerCargasDisponiblesParaRuta() {
                List<Carga> cargas = cargaRepo.findAll();

                return cargas.stream()
                                .filter(carga -> {
                                        // 1. Verifica que no tenga rutas registradas
                                        boolean tieneRuta = rutaRepo.findAllByCarga_IdCarga(carga.getIdCarga())
                                                        .size() > 0;
                                        if (tieneRuta)
                                                return false;

                                        // 2. Verifica que todos los bultos tengan estadoRecepcion definido
                                        List<Bulto> bultos = bultoRepo.findByCarga_IdCarga(carga.getIdCarga());
                                        boolean todosRecepcionados = bultos.stream()
                                                        .allMatch(b -> b.getEstadoRecepcion() != null);

                                        return todosRecepcionados;
                                })
                                .toList();
        }

        public List<UnidadTransporte> listarUnidades() {
                return unidadRepo.findAll();
        }

        public List<LocalDTO> obtenerLocalesPendientesPorAsignar(Long idCarga) {
                Optional<Carga> cargaOpt = cargaRepo.findById(idCarga);
                if (cargaOpt.isEmpty())
                        return List.of();

                LocalDate fechaObjetivo = cargaOpt.get().getFechaCarga().plusDays(2);
                DayOfWeek diaSemana = fechaObjetivo.getDayOfWeek();

                // 1. Todos los bultos de la carga
                List<Bulto> bultos = bultoRepo.findByCarga_IdCarga(idCarga);

                // 2. Locales únicos en frecuencia
                Set<Local> localesEnFrecuencia = bultos.stream()
                                .map(Bulto::getLocal)
                                .filter(local -> {
                                        PatronAtencion patron = local.getPatronAtencion();
                                        return switch (diaSemana) {
                                                case MONDAY -> patron.isLunes();
                                                case TUESDAY -> patron.isMartes();
                                                case WEDNESDAY -> patron.isMiercoles();
                                                case THURSDAY -> patron.isJueves();
                                                case FRIDAY -> patron.isViernes();
                                                case SATURDAY -> patron.isSabado();
                                                case SUNDAY -> patron.isDomingo();
                                        };
                                })
                                .collect(Collectors.toSet());

                // 3. Locales ya asignados en alguna ruta de esta carga
                List<RutaPersonalizada> rutas = rutaRepo.findAllByCarga_IdCarga(idCarga);
                Set<Long> localesAsignados = rutas.stream()
                                .flatMap(r -> rutaLocalRepo
                                                .findByRutaPersonalizada_IdRutaPersonalizada(r.getIdRutaPersonalizada())
                                                .stream())
                                .map(rpl -> rpl.getLocal().getIdLocal())
                                .collect(Collectors.toSet());

                // 4. Filtrar los no asignados
                return localesEnFrecuencia.stream()
                                .filter(local -> !localesAsignados.contains(local.getIdLocal()))
                                .map(local -> new LocalDTO(
                                                local.getIdLocal(),
                                                local.getCodigo(),
                                                local.getNombre()))
                                .sorted(Comparator.comparing(LocalDTO::getNombre))
                                .collect(Collectors.toList());
        }

        @Transactional
        public Map<String, Integer> registrarRutaPersonalizada(RutaPersonalizadaRequest request) {
                Carga carga = cargaRepo.findById(request.getIdCarga())
                                .orElseThrow(() -> new RuntimeException("Carga no encontrada"));
                UnidadTransporte unidad = unidadRepo.findById(request.getIdUnidad())
                                .orElseThrow(() -> new RuntimeException("Unidad no encontrada"));

                RutaPersonalizada ruta = new RutaPersonalizada();
                ruta.setCarga(carga);
                ruta.setUnidad(unidad);
                ruta.setComentario(request.getComentario());
                ruta = rutaRepo.save(ruta); // persistir primero para obtener ID

                // Guardar locales ordenados
                for (RutaPersonalizadaRequest.LocalOrdenado lo : request.getLocalesOrdenados()) {
                        RutaPersonalizadaLocal rpl = new RutaPersonalizadaLocal();
                        rpl.setRutaPersonalizada(ruta);
                        Local local = localRepo.findById(lo.getIdLocal())
                                        .orElseThrow(() -> new RuntimeException("Local no encontrado"));
                        rpl.setLocal(local);
                        rpl.setOrden(lo.getOrden());
                        rutaLocalRepo.save(rpl);
                }

                // Actualizar bultos
                LocalDate fechaTransporte = carga.getFechaCarga().plusDays(2);

                List<Bulto> bultos = bultoRepo.findByCarga_IdCarga(carga.getIdCarga());
                int actualizados = 0;
                int omitidos = 0;

                for (Bulto b : bultos) {
                        boolean estaEnRuta = request.getLocalesOrdenados().stream()
                                        .anyMatch(lo -> lo.getIdLocal().equals(b.getLocal().getIdLocal()));

                        if (estaEnRuta) {
                                if (b.getEstadoRecepcion() == EstadoRecepcion.FALTANTE) {
                                        omitidos++;
                                        continue;
                                }

                                b.setEstadoTransporte(EstadoTransporte.EN_CAMINO);
                                b.setFechaTransporte(fechaTransporte);
                                bultoRepo.save(b);
                                actualizados++;
                        }
                }

                return Map.of(
                                "actualizados", actualizados,
                                "omitidos", omitidos);
        }

        public List<LocalDTO> obtenerLocalesPorRuta(Long idRuta) {
                List<RutaPersonalizadaLocal> registros = rutaLocalRepo
                                .findByRutaPersonalizada_IdRutaPersonalizadaOrderByOrdenAsc(idRuta);

                return registros.stream()
                                .map(r -> new LocalDTO(
                                                r.getLocal().getIdLocal(),
                                                r.getLocal().getCodigo(),
                                                r.getLocal().getNombre()))
                                .collect(Collectors.toList());
        }

        public ReporteTransporteDTO obtenerReporteTransporte(Long idCarga) {
                Optional<Carga> cargaOpt = cargaRepo.findById(idCarga);
                if (cargaOpt.isEmpty()) {
                        throw new RuntimeException("Carga no encontrada con ID: " + idCarga);
                }

                Carga carga = cargaOpt.get();

                List<ReporteTransporteDTO.Ruta> rutas = rutaRepo.findAllByCarga_IdCarga(idCarga)
                                .stream()
                                .map(ruta -> {
                                        List<LocalDTO> locales = rutaLocalRepo
                                                        .findByRutaPersonalizada_IdRutaPersonalizadaOrderByOrdenAsc(
                                                                        ruta.getIdRutaPersonalizada())
                                                        .stream()
                                                        .map(rpl -> new LocalDTO(
                                                                        rpl.getLocal().getIdLocal(),
                                                                        rpl.getLocal().getCodigo(),
                                                                        rpl.getLocal().getNombre()))
                                                        .toList();

                                        return new ReporteTransporteDTO.Ruta(
                                                        ruta.getUnidad().getPlaca(),
                                                        ruta.getComentario(),
                                                        locales);
                                })
                                .toList();

                return new ReporteTransporteDTO(
                                carga.getCodigoCarga(),
                                carga.getFechaCarga().toString(),
                                rutas);
        }

        public List<Carga> obtenerCargasConRuta() {
                return rutaRepo.findAll().stream()
                                .map(RutaPersonalizada::getCarga)
                                .distinct()
                                .toList();
        }

        public RutaMapaDTO obtenerMapaPorRuta(Long idRuta) {
                RutaPersonalizada ruta = rutaRepo.findById(idRuta)
                                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

                List<LocalMapaDTO> locales = rutaLocalRepo
                                .findByRutaPersonalizada_IdRutaPersonalizadaOrderByOrdenAsc(idRuta)
                                .stream()
                                .map(rpl -> {
                                        Local l = rpl.getLocal();
                                        return new LocalMapaDTO(
                                                        l.getIdLocal(),
                                                        l.getCodigo(),
                                                        l.getNombre(),
                                                        l.getLatitud(),
                                                        l.getLongitud());
                                })
                                .toList();

                return new RutaMapaDTO(
                                ruta.getUnidad().getPlaca(),
                                ruta.getComentario(),
                                locales);
        }
}
