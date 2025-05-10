package com.induamerica.backend.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RutaPersonalizadaDTO {
    private Long idRuta;
    private String codigoCarga;
    private LocalDate fechaCarga;
    private String placaUnidad;
    private Long idUnidad;
    private String comentario;

    public RutaPersonalizadaDTO(Long idRuta, String codigoCarga, LocalDate fechaCarga, String placaUnidad, Long idUnidad, String comentario) {
        this.idRuta = idRuta;
        this.codigoCarga = codigoCarga;
        this.fechaCarga = fechaCarga;
        this.placaUnidad = placaUnidad;
        this.idUnidad = idUnidad;
        this.comentario = comentario;
    }
}
