package com.induamerica.backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.induamerica.backend.model.TipoMerma;

import java.time.LocalDate;

@Getter
@Setter
public class RegistrarActaRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaIncidencia;

    private String codigoBulto;
    private String numeroActa;
    private String nombreAuxiliar;
    private String nombre;
    private TipoMerma tipoMerma;
    private String estadoMerma;
    private Integer cantidad;
    private MultipartFile fotoRegistro;
}
