package com.induamerica.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class AsignarFechaTransporteRequest {

    private String nombreLocal;
    private String codigoCarga;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaTransporte;

    // Getters y Setters
    public String getNombreLocal() {
        return nombreLocal;
    }

    public void setNombreLocal(String nombreLocal) {
        this.nombreLocal = nombreLocal;
    }

    public String getCodigoCarga() {
        return codigoCarga;
    }

    public void setCodigoCarga(String codigoCarga) {
        this.codigoCarga = codigoCarga;
    }

    public LocalDate getFechaTransporte() {
        return fechaTransporte;
    }

    public void setFechaTransporte(LocalDate fechaTransporte) {
        this.fechaTransporte = fechaTransporte;
    }
}
