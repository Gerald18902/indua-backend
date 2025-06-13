package com.induamerica.backend.service;

import com.induamerica.backend.dto.BultoDTO;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.repository.BultoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
