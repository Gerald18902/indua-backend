package com.induamerica.backend.dto;

public class AsignarFechaTransporteRequest {

    private String nombreLocal;
    private String codigoCarga;

    private String fechaTransporte;

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

    public String getFechaTransporte() {
        return fechaTransporte;
    }

    public void setFechaTransporte(String fechaTransporte) {
        this.fechaTransporte = fechaTransporte;
    }
}
