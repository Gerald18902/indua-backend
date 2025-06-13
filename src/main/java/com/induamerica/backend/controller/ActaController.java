package com.induamerica.backend.controller;

import com.induamerica.backend.dto.RegistrarActaRequest;
import com.induamerica.backend.repository.ActaRepository;
import com.induamerica.backend.service.ActaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/actas")
public class ActaController {

    @Autowired
    private ActaRepository actaRepository;

    @Autowired
    private ActaService actaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrarActa(@ModelAttribute RegistrarActaRequest request) throws IOException {
        return actaService.registrarActa(request);
    }

    // ACTUALIZACIÓN DE ACTA
    @PostMapping(value = "/{id}/actualizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizarActa(
            @PathVariable Long id,
            @RequestParam("estadoMerma") String nuevoEstado,
            @RequestParam("responsabilidad") String responsabilidad,
            @RequestParam(value = "fotoRegularizacion", required = false) MultipartFile foto) {

        String resultado = actaService.actualizarActa(id, nuevoEstado, responsabilidad, foto);

        if (resultado.equals("Acta no encontrada.") || resultado.contains("Error")) {
            return ResponseEntity.badRequest().body(resultado);
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping
    public ResponseEntity<?> listarActasConFiltros(
            @RequestParam(required = false) String codigoBulto,
            @RequestParam(required = false) String tipoMerma,
            @RequestParam(required = false) String estadoMerma,
            @RequestParam(required = false) String fechaIncidencia) {

        return ResponseEntity.ok(
                actaRepository.findAll().stream()
                        .filter(a -> codigoBulto == null || codigoBulto.isBlank()
                                || a.getCodigoBulto().contains(codigoBulto))
                        .filter(a -> tipoMerma == null || tipoMerma.isBlank()
                                || a.getTipoMerma().name().equalsIgnoreCase(tipoMerma))
                        .filter(a -> estadoMerma == null || estadoMerma.isBlank()
                                || (a.getEstadoMerma() != null && a.getEstadoMerma().equalsIgnoreCase(estadoMerma)))
                        .filter(a -> fechaIncidencia == null || fechaIncidencia.isBlank() ||
                                (a.getFechaIncidencia() != null &&
                                        a.getFechaIncidencia().toString().equals(fechaIncidencia)))
                        .toList());
    }
}
