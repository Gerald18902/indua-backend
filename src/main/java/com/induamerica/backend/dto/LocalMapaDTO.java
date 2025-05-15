package com.induamerica.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalMapaDTO {
    private Long idLocal;
    private String codigo;
    private String nombre;
    private Double latitud;
    private Double longitud;

    public LocalMapaDTO(Long idLocal, String codigo, String nombre, Double latitud, Double longitud) {
        this.idLocal = idLocal;
        this.codigo = codigo;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
