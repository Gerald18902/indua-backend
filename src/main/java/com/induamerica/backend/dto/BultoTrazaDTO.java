package com.induamerica.backend.dto;

public class BultoTrazaDTO {
    private String codigoBulto;
    private String nombreLocal;
    private String estadoRecepcion;
    private String estadoTransporte;
    private String fechaTransporte;
    private String estadoDespacho;
    private String fechaDespacho;
    private String codigoCarga;

    public BultoTrazaDTO(String codigoBulto, String estadoRecepcion, String estadoTransporte,
            String fechaTransporte, String estadoDespacho, String fechaDespacho,
            String nombreLocal, String codigoCarga) {
        this.codigoBulto = codigoBulto;
        this.estadoRecepcion = estadoRecepcion;
        this.estadoTransporte = estadoTransporte;
        this.fechaTransporte = fechaTransporte;
        this.estadoDespacho = estadoDespacho;
        this.fechaDespacho = fechaDespacho;
        this.nombreLocal = nombreLocal;
        this.codigoCarga = codigoCarga;
    }

    // Getters y setters

    public String getCodigoBulto() {
        return codigoBulto;
    }

    public void setCodigoBulto(String codigoBulto) {
        this.codigoBulto = codigoBulto;
    }

    public String getNombreLocal() {
        return nombreLocal;
    }

    public void setNombreLocal(String nombreLocal) {
        this.nombreLocal = nombreLocal;
    }

    public String getEstadoRecepcion() {
        return estadoRecepcion;
    }

    public void setEstadoRecepcion(String estadoRecepcion) {
        this.estadoRecepcion = estadoRecepcion;
    }

    public String getEstadoTransporte() {
        return estadoTransporte;
    }

    public void setEstadoTransporte(String estadoTransporte) {
        this.estadoTransporte = estadoTransporte;
    }

    public String getFechaTransporte() {
        return fechaTransporte;
    }

    public void setFechaTransporte(String fechaTransporte) {
        this.fechaTransporte = fechaTransporte;
    }

    public String getEstadoDespacho() {
        return estadoDespacho;
    }

    public void setEstadoDespacho(String estadoDespacho) {
        this.estadoDespacho = estadoDespacho;
    }

    public String getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(String fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public String getCodigoCarga() {
        return codigoCarga;
    }

}
