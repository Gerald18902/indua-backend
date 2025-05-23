package com.induamerica.backend.dto;

import java.util.List;

public class ActualizarDespachoRequest {
    private List<String> codigosBulto;
    private String nuevoEstado;

    // Getters y setters
    public List<String> getCodigosBulto() {
        return codigosBulto;
    }

    public void setCodigosBulto(List<String> codigosBulto) {
        this.codigosBulto = codigosBulto;
    }

    public String getNuevoEstado() {
        return nuevoEstado;
    }

    public void setNuevoEstado(String nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }
}
