package com.induamerica.backend.dto;

import java.time.LocalDate;

public class CargaDTO {
    private Long idCarga;              // ✅ nuevo campo
    private String codigoCarga;
    private String fechaCarga;

    public CargaDTO(Long idCarga, String codigoCarga, LocalDate fechaCarga) {
        this.idCarga = idCarga;
        this.codigoCarga = codigoCarga;
        this.fechaCarga = fechaCarga != null ? fechaCarga.toString() : null;
    }

    public Long getIdCarga() {
        return idCarga;
    }

    public String getCodigoCarga() {
        return codigoCarga;
    }

    public String getFechaCarga() {
        return fechaCarga;
    }
}
