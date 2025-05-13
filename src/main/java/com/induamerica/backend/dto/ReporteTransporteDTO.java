package com.induamerica.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReporteTransporteDTO {
    private String codigoCarga;
    private String fechaCarga;
    private List<Ruta> rutas;

    public ReporteTransporteDTO(String codigoCarga, String fechaCarga, List<Ruta> rutas) {
        this.codigoCarga = codigoCarga;
        this.fechaCarga = fechaCarga;
        this.rutas = rutas;
    }

    @Getter
    @Setter
    public static class Ruta {
        private String placa;
        private String comentario;
        private List<LocalDTO> locales;

        public Ruta(String placa, String comentario, List<LocalDTO> locales) {
            this.placa = placa;
            this.comentario = comentario;
            this.locales = locales;
        }
    }
}
