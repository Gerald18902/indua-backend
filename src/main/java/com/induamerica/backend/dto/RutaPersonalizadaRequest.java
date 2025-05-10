package com.induamerica.backend.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RutaPersonalizadaRequest {
    private Long idCarga;
    private Long idUnidad;
    private String comentario;
    private List<LocalOrdenado> localesOrdenados;

    // Getters y Setters

    @Getter
    @Setter
    public static class LocalOrdenado {
        private Long idLocal;
        private int orden;

        // Getters y Setters
    }
}
