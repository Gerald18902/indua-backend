package com.induamerica.backend.service;

import com.induamerica.backend.model.Acta;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.repository.ActaRepository;
import com.induamerica.backend.repository.BultoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.induamerica.backend.dto.RegistrarActaRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ActaService {

    @Autowired
    private ActaRepository actaRepository;

    @Autowired
    private BultoRepository bultoRepository;

    public ResponseEntity<?> actualizarActa(Long id, String nuevoEstado, String responsabilidad, MultipartFile foto) {
        Optional<Acta> optionalActa = actaRepository.findById(id);
        if (optionalActa.isEmpty()) {
            return ResponseEntity.badRequest().body("Acta no encontrada.");
        }

        Acta acta = optionalActa.get();
        acta.setEstadoMerma(nuevoEstado);
        acta.setResponsabilidad(responsabilidad);
        acta.setFechaRegularizacion(LocalDate.now());

        if (foto != null && !foto.isEmpty()) {
            try {
                String extension = getExtensionOrDefault(foto.getOriginalFilename(), ".jpg");
                String nombreFoto = "reg_" + System.currentTimeMillis() + extension;

                guardarArchivoEnUploads(foto, nombreFoto);
                acta.setFotoRegularizacion(nombreFoto);

                // Actualiza la fecha de despacho del bulto relacionado
                Bulto bulto = bultoRepository.findByCodigoBulto(acta.getCodigoBulto());
                if (bulto != null) {
                    bulto.setFechaDespacho(LocalDate.now());
                    bultoRepository.save(bulto);
                }

            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Error al guardar la imagen.");
            }
        }

        actaRepository.saveAndFlush(acta);
        return ResponseEntity.ok("Acta actualizada correctamente.");
    }

    public ResponseEntity<?> registrarActa(RegistrarActaRequest request) throws IOException {
        Bulto bulto = bultoRepository.findByCodigoBulto(request.getCodigoBulto());
        if (bulto == null) {
            return ResponseEntity.badRequest().body("El código de bulto no existe.");
        }

        if (actaRepository.existsByCodigoBulto(request.getCodigoBulto())) {
            return ResponseEntity.badRequest().body("Ya existe un acta registrada para este código de bulto.");
        }

        // Actualizar estado de despacho
        switch (request.getTipoMerma()) {
            case DETERIORADO, DISCREPANCIA -> {
                bulto.setEstadoDespacho(Bulto.EstadoDespacho.valueOf(request.getTipoMerma().name()));
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

        // Guardar imagen
        String nombreImagen = null;
        MultipartFile foto = request.getFotoRegistro();
        if (foto != null && !foto.isEmpty()) {
            String original = foto.getOriginalFilename();
            String extension = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : ".jpg";

            String rutaUploads = System.getProperty("user.dir") + File.separator + "uploads";
            File carpeta = new File(rutaUploads);
            if (!carpeta.exists())
                carpeta.mkdirs();

            nombreImagen = System.currentTimeMillis() + extension;
            File destino = new File(carpeta, nombreImagen);
            foto.transferTo(destino);
        }

        // Crear Acta
        Acta acta = new Acta();
        acta.setFechaIncidencia(request.getFechaIncidencia());
        acta.setCodigoBulto(request.getCodigoBulto());
        acta.setNumeroActa(request.getNumeroActa());
        acta.setNombreAuxiliar(request.getNombreAuxiliar());
        acta.setNombre(request.getNombre());
        acta.setTipoMerma(request.getTipoMerma());
        acta.setCantidad(request.getCantidad());
        acta.setFotoRegistro(nombreImagen);

        switch (request.getTipoMerma()) {
            case DETERIORADO, DISCREPANCIA -> acta.setEstadoMerma("MERMA SIN SUSTENTO");
            case FALTANTE -> acta.setEstadoMerma("FALTANTE");
            default -> acta.setEstadoMerma("SIN ESTADO");
        }

        actaRepository.save(acta);

        return ResponseEntity.ok("Acta registrada correctamente");
    }

    private String getExtensionOrDefault(String nombreArchivo, String defecto) {
        if (nombreArchivo != null && nombreArchivo.contains(".")) {
            return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
        }
        return defecto;
    }

    private void guardarArchivoEnUploads(MultipartFile archivo, String nombreDestino) throws IOException {
        String rutaUploads = System.getProperty("user.dir") + File.separator + "uploads";
        File carpeta = new File(rutaUploads);
        if (!carpeta.exists())
            carpeta.mkdirs();

        File destino = new File(carpeta, nombreDestino);
        archivo.transferTo(destino);
    }

    public List<Acta> listarActasConFiltros(String codigoBulto, String tipoMerma, String estadoMerma,
            String fechaIncidencia) {
        return actaRepository.findAll().stream()
                .filter(a -> codigoBulto == null || codigoBulto.isBlank()
                        || a.getCodigoBulto().contains(codigoBulto))
                .filter(a -> tipoMerma == null || tipoMerma.isBlank()
                        || a.getTipoMerma().name().equalsIgnoreCase(tipoMerma))
                .filter(a -> estadoMerma == null || estadoMerma.isBlank()
                        || (a.getEstadoMerma() != null && a.getEstadoMerma().equalsIgnoreCase(estadoMerma)))
                .filter(a -> fechaIncidencia == null || fechaIncidencia.isBlank()
                        || (a.getFechaIncidencia() != null &&
                                a.getFechaIncidencia().toString().equals(fechaIncidencia)))
                .toList();
    }

}
