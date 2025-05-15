package com.induamerica.backend.dto;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RutaMapaDTO {
    private String placa;
    private String comentario;
    private List<LocalMapaDTO> locales;

    public RutaMapaDTO(String placa, String comentario, List<LocalMapaDTO> locales) {
        this.placa = placa;
        this.comentario = comentario;
        this.locales = locales;
    }
}
