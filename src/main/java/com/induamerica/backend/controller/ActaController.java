package com.induamerica.backend.controller;

import com.induamerica.backend.dto.RegistrarActaRequest;
import com.induamerica.backend.model.Acta;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.repository.ActaRepository;
import com.induamerica.backend.repository.BultoRepository;
import com.induamerica.backend.service.ActaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/actas")
public class ActaController {

    @Autowired
    private ActaRepository actaRepository;

    @Autowired
    private BultoRepository bultoRepository;

    @Autowired
    private ActaService actaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrarActa(@ModelAttribute RegistrarActaRequest request) throws IOException {
        Bulto bulto = bultoRepository.findByCodigoBulto(request.getCodigoBulto());
        if (bulto == null) {
            return ResponseEntity.badRequest().body("El código de bulto no existe.");
        }

        if (actaRepository.existsByCodigoBulto(request.getCodigoBulto())) {
            return ResponseEntity.badRequest().body("Ya existe un acta registrada para este código de bulto.");
        }

        // Actualiza estado despacho del bulto
        switch (request.getTipoMerma()) {
            case DETERIORADO -> {
                bulto.setEstadoDespacho(Bulto.EstadoDespacho.DETERIORADO);
                bulto.setFechaDespacho(request.getFechaIncidencia());
            }
            case DISCREPANCIA -> {
                bulto.setEstadoDespacho(Bulto.EstadoDespacho.DISCREPANCIA);
                bulto.setFechaDespacho(request.getFechaIncidencia());
            }
            case FALTANTE -> {
                bulto.setEstadoDespacho(Bulto.EstadoDespacho.FALTANTE);
                bulto.setFechaDespacho(null);
            }
            default -> {
                bulto.setEstadoDespacho(null);
                bulto.setFechaDespacho(null);
            }
        }
        bultoRepository.save(bulto);

        // Guardar imagen de registro
        String nombreImagen = null;
        if (request.getFotoRegistro() != null && !request.getFotoRegistro().isEmpty()) {
            String original = request.getFotoRegistro().getOriginalFilename();
            String extension = original != null && original.contains(".")
                    ? original.substring(original.lastIndexOf("."))
                    : ".jpg";

            String rutaUploads = System.getProperty("user.dir") + File.separator + "uploads";
            File carpeta = new File(rutaUploads);
            if (!carpeta.exists())
                carpeta.mkdirs();

            nombreImagen = System.currentTimeMillis() + extension;
            File destino = new File(carpeta, nombreImagen);
            request.getFotoRegistro().transferTo(destino);
        }

        // Crear Acta
        Acta acta = new Acta();
        acta.setFechaIncidencia(request.getFechaIncidencia());
        acta.setCodigoBulto(request.getCodigoBulto());
        acta.setNumeroActa(request.getNumeroActa());
        acta.setNombreAuxiliar(request.getNombreAuxiliar());
        acta.setNombre(request.getNombre());
        acta.setTipoMerma(request.getTipoMerma());

        switch (request.getTipoMerma()) {
            case DETERIORADO, DISCREPANCIA -> acta.setEstadoMerma("MERMA SIN SUSTENTO");
            case FALTANTE -> acta.setEstadoMerma("FALTANTE");
            default -> acta.setEstadoMerma("SIN ESTADO");
        }

        acta.setCantidad(request.getCantidad());
        acta.setFotoRegistro(nombreImagen);

        actaRepository.save(acta);

        return ResponseEntity.ok("Acta registrada correctamente");
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
