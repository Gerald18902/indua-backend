package com.induamerica.backend.controller;

import com.induamerica.backend.dto.RegistrarActaRequest;
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

                return actaService.actualizarActa(id, nuevoEstado, responsabilidad, foto);
        }

        @GetMapping
        public ResponseEntity<?> listarActasConFiltros(
                        @RequestParam(required = false) String codigoBulto,
                        @RequestParam(required = false) String tipoMerma,
                        @RequestParam(required = false) String estadoMerma,
                        @RequestParam(required = false) String fechaIncidencia) {

                return ResponseEntity.ok(
                                actaService.listarActasConFiltros(codigoBulto, tipoMerma, estadoMerma,
                                                fechaIncidencia));
        }

}