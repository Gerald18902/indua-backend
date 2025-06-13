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
import com.induamerica.backend.dto.BultoTrazaDTO;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.repository.BultoRepository;
import com.induamerica.backend.service.BultoService;
import com.induamerica.backend.dto.AsignarFechaTransporteRequest;

@RestController
@RequestMapping("/api/bultos")
public class BultoController {

    @Autowired
    private BultoService bultoService;

    // borrar luego
    @Autowired
    private BultoRepository bultoRepository;

    @GetMapping
    public ResponseEntity<List<BultoDTO>> listarBultos() {
        List<BultoDTO> dtoList = bultoService.listarBultosDTO(); // ✅ delegar al servicio
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/actualizar-estado")
    public ResponseEntity<String> actualizarEstadoRecepcion(@RequestBody Map<String, String> request) {
        try {
            String codigoBulto = request.get("codigoBulto");
            String nuevoEstado = request.get("nuevoEstado");

            bultoService.actualizarEstadoRecepcion(codigoBulto, nuevoEstado);
            return ResponseEntity.ok("Estado actualizado");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al actualizar el estado");
        }
    }

    @PutMapping("/completar-carga")
    public ResponseEntity<String> completarCarga(@RequestBody Map<String, String> request) {
        try {
            String codigoCarga = request.get("codigoCarga");
            bultoService.completarCarga(codigoCarga);
            return ResponseEntity.ok("Bultos actualizados correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al completar la carga");
        }
    }

    @PutMapping("/terminar-carga")
    public ResponseEntity<String> terminarCarga(@RequestBody Map<String, String> request) {
        try {
            String codigoCarga = request.get("codigoCarga");
            bultoService.terminarCarga(codigoCarga);
            return ResponseEntity.ok("Carga terminada y bultos sin estado marcados como faltantes");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al terminar la carga");
        }
    }

    @GetMapping("/en-camino")
    public ResponseEntity<List<BultoDespachoDTO>> listarBultosEnCamino() {
        List<BultoDespachoDTO> bultos = bultoRepository.findBultosEnCamino();
        return ResponseEntity.ok(bultos);
    }

    @PutMapping("/actualizar-despacho-masivo")
    public ResponseEntity<Void> actualizarDespachoMasivo(@RequestBody ActualizarDespachoRequest request) {
        bultoService.actualizarDespachoMasivo(
                request.getCodigosBulto(),
                Bulto.EstadoDespacho.valueOf(request.getNuevoEstado()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/trazabilidad")
    public ResponseEntity<List<BultoTrazaDTO>> obtenerTrazabilidad() {
        return ResponseEntity.ok(bultoService.obtenerTrazabilidad());
    }

    @PutMapping("/asignar-fecha-transporte")
    public ResponseEntity<String> asignarFechaTransporte(@RequestBody AsignarFechaTransporteRequest request) {
        try {
            String mensaje = bultoService.asignarFechaTransporte(
                    request.getNombreLocal(),
                    request.getCodigoCarga(),
                    LocalDate.parse(request.getFechaTransporte()));
            return ResponseEntity.ok(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al asignar la fecha de transporte.");
        }
    }
}