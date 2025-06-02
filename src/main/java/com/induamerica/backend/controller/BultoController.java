package com.induamerica.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.induamerica.backend.dto.ActualizarDespachoRequest;
import com.induamerica.backend.dto.BultoDTO;
import com.induamerica.backend.dto.BultoDespachoDTO;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.repository.BultoRepository;

@RestController
@RequestMapping("/api/bultos")
public class BultoController {

    @Autowired
    private BultoRepository bultoRepository;

    @GetMapping
    public ResponseEntity<List<BultoDTO>> listarBultos() {
        List<Bulto> bultos = bultoRepository.findAll();

        List<BultoDTO> dtoList = bultos.stream().map(b -> new BultoDTO(
                b.getCodigoBulto(),
                b.getEstadoRecepcion() != null ? b.getEstadoRecepcion().toString() : "-",
                b.getLocal().getCodigo(),
                b.getLocal().getNombre(),
                b.getCarga().getCodigoCarga())).toList();

        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/actualizar-estado")
    public ResponseEntity<String> actualizarEstadoRecepcion(@RequestBody Map<String, String> request) {
        try {
            String codigoBulto = request.get("codigoBulto");
            String nuevoEstado = request.get("nuevoEstado");

            Bulto bulto = bultoRepository.findByCodigoBulto(codigoBulto);
            if (bulto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bulto no encontrado");
            }

            Bulto.EstadoRecepcion estadoRecepcion = Bulto.EstadoRecepcion.valueOf(nuevoEstado);
            bulto.setEstadoRecepcion(estadoRecepcion);

            // Lógica para estadoTransporte
            if (estadoRecepcion == Bulto.EstadoRecepcion.FALTANTE) {
                bulto.setEstadoTransporte(null);
            } else {
                bulto.setEstadoTransporte(Bulto.EstadoTransporte.EN_ALMACEN);
            }

            bultoRepository.save(bulto);
            return ResponseEntity.ok("Estado actualizado");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el estado");
        }
    }

    @PutMapping("/completar-carga")
    public ResponseEntity<String> completarCarga(@RequestBody Map<String, String> request) {
        try {
            String codigoCarga = request.get("codigoCarga");

            List<Bulto> bultos = bultoRepository.findByCargaCodigoCarga(codigoCarga);

            for (Bulto b : bultos) {
                if (b.getEstadoRecepcion() == null) {
                    b.setEstadoRecepcion(Bulto.EstadoRecepcion.EN_BUEN_ESTADO);
                    b.setEstadoTransporte(Bulto.EstadoTransporte.EN_ALMACEN);
                }
            }

            bultoRepository.saveAll(bultos);
            return ResponseEntity.ok("Bultos actualizados correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al completar la carga");
        }
    }

    @PutMapping("/terminar-carga")
    public ResponseEntity<String> terminarCarga(@RequestBody Map<String, String> request) {
        try {
            String codigoCarga = request.get("codigoCarga");
            List<Bulto> bultos = bultoRepository.findByCargaCodigoCarga(codigoCarga);

            for (Bulto b : bultos) {
                if (b.getEstadoRecepcion() == null)
                    b.setEstadoRecepcion(Bulto.EstadoRecepcion.FALTANTE);
            }

            bultoRepository.saveAll(bultos);
            return ResponseEntity.ok("Carga terminada y bultos sin estado marcados como faltantes");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al terminar la carga");
        }
    }

    @GetMapping("/en-camino")
    public ResponseEntity<List<BultoDespachoDTO>> listarBultosEnCamino() {
        List<BultoDespachoDTO> bultos = bultoRepository.findBultosEnCamino();
        return ResponseEntity.ok(bultos);
    }

    @PutMapping("/actualizar-despacho-masivo")
    public ResponseEntity<Void> actualizarDespachoMasivo(@RequestBody ActualizarDespachoRequest request) {
        List<String> codigos = request.getCodigosBulto();
        Bulto.EstadoDespacho nuevoEstado = Bulto.EstadoDespacho.valueOf(request.getNuevoEstado());

        for (String codigo : codigos) {
            Bulto bulto = bultoRepository.findByCodigoBulto(codigo);
            if (bulto != null && bulto.getEstadoDespacho() == null) {
                bulto.setEstadoDespacho(nuevoEstado);
                bulto.setFechaDespacho(LocalDate.now()); // ← asignar la fecha actual
                bultoRepository.save(bulto);
            }
        }

        return ResponseEntity.ok().build();
    }

}