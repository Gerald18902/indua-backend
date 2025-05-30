package com.induamerica.backend.service;

import com.induamerica.backend.model.Acta;
import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.repository.ActaRepository;
import com.induamerica.backend.repository.BultoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ActaService {

    @Autowired
    private ActaRepository actaRepository;

    @Autowired
    private BultoRepository bultoRepository;

    public String actualizarActa(Long id, String nuevoEstado, String responsabilidad, MultipartFile foto) {
        Optional<Acta> optionalActa = actaRepository.findById(id);
        if (optionalActa.isEmpty()) {
            return "Acta no encontrada.";
        }

        Acta acta = optionalActa.get();
        acta.setEstadoMerma(nuevoEstado);
        acta.setResponsabilidad(responsabilidad);
        acta.setFechaRegularizacion(LocalDate.now());

        // Si hay foto, guardar en /uploads con nombre único
        if (foto != null && !foto.isEmpty()) {
            try {
                String extension = foto.getOriginalFilename().contains(".")
                        ? foto.getOriginalFilename().substring(foto.getOriginalFilename().lastIndexOf("."))
                        : ".jpg";

                String rutaUploads = System.getProperty("user.dir") + File.separator + "uploads";
                File carpeta = new File(rutaUploads);
                if (!carpeta.exists())
                    carpeta.mkdirs();

                String nombreFoto = "reg_" + System.currentTimeMillis() + extension;
                File destino = new File(carpeta, nombreFoto);
                foto.transferTo(destino);

                acta.setFotoRegularizacion(nombreFoto);

                Bulto bulto = bultoRepository.findByCodigoBulto(acta.getCodigoBulto());
                if (bulto != null) {
                    bulto.setFechaDespacho(LocalDate.now());
                    bultoRepository.save(bulto);
                }

            } catch (IOException e) {
                return "Error al guardar la imagen.";
            }
        }

        actaRepository.save(acta);
        return "Acta actualizada correctamente.";
    }
}
