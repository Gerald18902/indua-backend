package com.induamerica.backend.service;

import com.induamerica.backend.dto.BultoDTO;
import com.induamerica.backend.dto.BultoTrazaDTO;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.repository.BultoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;

@Service
public class BultoService {

    @Autowired
    private BultoRepository bultoRepository;

    public List<BultoDTO> listarBultosDTO() {
        return bultoRepository.findAll().stream().map(b -> new BultoDTO(
                b.getCodigoBulto(),
                b.getEstadoRecepcion() != null ? b.getEstadoRecepcion().toString() : "-",
                b.getLocal().getCodigo(),
                b.getLocal().getNombre(),
                b.getCarga().getCodigoCarga())).toList();
    }

    /**
     * Actualiza el estado de recepción de un bulto específico
     */
    public void actualizarEstadoRecepcion(String codigoBulto, String nuevoEstado) {
        Bulto bulto = bultoRepository.findByCodigoBulto(codigoBulto);
        if (bulto == null) {
            throw new IllegalArgumentException("Bulto no encontrado: " + codigoBulto);
        }

        Bulto.EstadoRecepcion estadoRecepcion = Bulto.EstadoRecepcion.valueOf(nuevoEstado);
        bulto.setEstadoRecepcion(estadoRecepcion);

        // Lógica adicional: si está en buen estado o deteriorado => EN_ALMACEN
        if (estadoRecepcion == Bulto.EstadoRecepcion.FALTANTE) {
            bulto.setEstadoTransporte(null);
        } else {
            bulto.setEstadoTransporte(Bulto.EstadoTransporte.EN_ALMACEN);
        }

        bultoRepository.save(bulto);
    }

    /**
     * Marca todos los bultos sin estado de recepción como EN_BUEN_ESTADO y
     * EN_ALMACEN
     */
    public void completarCarga(String codigoCarga) {
        List<Bulto> bultos = bultoRepository.findByCargaCodigoCarga(codigoCarga);

        for (Bulto b : bultos) {
            if (b.getEstadoRecepcion() == null) {
                b.setEstadoRecepcion(Bulto.EstadoRecepcion.EN_BUEN_ESTADO);
                b.setEstadoTransporte(Bulto.EstadoTransporte.EN_ALMACEN);
            }
        }

        bultoRepository.saveAll(bultos);
    }

    /**
     * Marca todos los bultos sin estado como FALTANTE
     */
    public void terminarCarga(String codigoCarga) {
        List<Bulto> bultos = bultoRepository.findByCargaCodigoCarga(codigoCarga);

        for (Bulto b : bultos) {
            if (b.getEstadoRecepcion() == null) {
                b.setEstadoRecepcion(Bulto.EstadoRecepcion.FALTANTE);
            }
        }

        bultoRepository.saveAll(bultos);
    }

    public void actualizarDespachoMasivo(List<String> codigosBulto, Bulto.EstadoDespacho nuevoEstado) {
        for (String codigo : codigosBulto) {
            Bulto bulto = bultoRepository.findByCodigoBulto(codigo);
            if (bulto != null && bulto.getEstadoDespacho() == null) {
                bulto.setEstadoDespacho(nuevoEstado);
                bulto.setFechaDespacho(LocalDate.now());
                bultoRepository.save(bulto);
            }
        }
    }

    public String asignarFechaTransporte(String nombreLocal, String codigoCarga, LocalDate nuevaFecha) {
        List<Bulto> bultosFiltrados = bultoRepository.findAll().stream()
                .filter(b -> b.getLocal().getNombre().trim().equalsIgnoreCase(nombreLocal.trim()))
                .filter(b -> b.getCarga().getCodigoCarga().trim().equalsIgnoreCase(codigoCarga.trim()))
                .filter(b -> b.getEstadoTransporte() == Bulto.EstadoTransporte.EN_ALMACEN)
                .toList();

        for (Bulto bulto : bultosFiltrados) {
            bulto.setEstadoTransporte(Bulto.EstadoTransporte.EN_CAMINO);
            bulto.setFechaTransporte(nuevaFecha);
        }

        // ✅ Solo guardar si hay bultos que actualizar
        if (!bultosFiltrados.isEmpty()) {
            bultoRepository.saveAll(bultosFiltrados);
        }

        return "Fecha de transporte asignada correctamente a " + bultosFiltrados.size() + " bultos.";
    }

    public List<BultoTrazaDTO> obtenerTrazabilidad() {
        return bultoRepository.findAll().stream().map(b -> new BultoTrazaDTO(
                b.getCodigoBulto(),
                b.getEstadoRecepcion() != null ? b.getEstadoRecepcion().toString() : "-",
                b.getEstadoTransporte() != null ? b.getEstadoTransporte().toString() : "-",
                b.getFechaTransporte() != null ? b.getFechaTransporte().toString() : "-",
                b.getEstadoDespacho() != null ? b.getEstadoDespacho().toString() : "-",
                b.getFechaDespacho() != null ? b.getFechaDespacho().toString() : "-",
                b.getLocal().getNombre(),
                b.getCarga().getCodigoCarga())).toList();
    }

}
