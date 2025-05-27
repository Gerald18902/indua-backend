// Controlador para Actas
package com.induamerica.backend.controller;

import com.induamerica.backend.dto.RegistrarActaRequest;
import com.induamerica.backend.model.Acta;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.repository.ActaRepository;
import com.induamerica.backend.repository.BultoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/actas")
public class ActaController {

    @Autowired
    private ActaRepository actaRepository;

    @Autowired
    private BultoRepository bultoRepository;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrarActa(@ModelAttribute RegistrarActaRequest request) throws IOException {
        // Validar existencia del bulto
        Bulto bulto = bultoRepository.findByCodigoBulto(request.getCodigoBulto());
        if (bulto == null) {
            return ResponseEntity.badRequest().body("El código de bulto no existe.");
        }

        // Validar que no exista acta duplicada
        if (actaRepository.existsByCodigoBulto(request.getCodigoBulto())) {
            return ResponseEntity.badRequest().body("Ya existe un acta registrada para este código de bulto.");
        }

        // Actualizar estado del bulto
        Bulto bulto1 = bultoRepository.findByCodigoBulto(request.getCodigoBulto());
        if (bulto1 != null) {
            switch (request.getTipoMerma()) {
                case DETERIORADO -> {
                    bulto1.setEstadoDespacho(Bulto.EstadoDespacho.DETERIORADO);
                    bulto1.setFechaDespacho(request.getFechaIncidencia()); // ✅ asignar fecha
                }
                case DISCREPANCIA -> {
                    bulto1.setEstadoDespacho(Bulto.EstadoDespacho.DISCREPANCIA);
                    bulto1.setFechaDespacho(request.getFechaIncidencia()); // ✅ asignar fecha
                }
                case FALTANTE -> {
                    bulto1.setEstadoDespacho(Bulto.EstadoDespacho.FALTANTE);
                    bulto1.setFechaDespacho(null); // ✅ mantener nulo
                }
                default -> {
                    bulto1.setEstadoDespacho(null);
                    bulto1.setFechaDespacho(null);
                }
            }
            bultoRepository.save(bulto1);
        }

        // Procesar imagen
        String nombreImagen = null;
        if (request.getFotoRegistro() != null && !request.getFotoRegistro().isEmpty()) {
            nombreImagen = request.getFotoRegistro().getOriginalFilename();
        }

        Acta acta = new Acta();
        acta.setFechaIncidencia(request.getFechaIncidencia());
        acta.setCodigoBulto(request.getCodigoBulto());
        acta.setNumeroActa(request.getNumeroActa());
        acta.setNombreAuxiliar(request.getNombreAuxiliar());
        acta.setNombre(request.getNombre());
        acta.setTipoMerma(request.getTipoMerma());
        acta.setEstadoMerma(request.getEstadoMerma());
        acta.setCantidad(request.getCantidad());
        acta.setFotoRegistro(nombreImagen);

        actaRepository.save(acta);

        return ResponseEntity.ok("Acta registrada correctamente");
    }
}
