package com.induamerica.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalDTO {
    private Long idLocal;
    private String codigo;
    private String nombre;

    public LocalDTO(Long idLocal, String codigo, String nombre) {
        this.idLocal = idLocal;
        this.codigo = codigo;
        this.nombre = nombre;
    }
}