package com.induamerica.backend.dto;

public class BultoDTO {
    private String codigoBulto;
    private String estadoRecepcion;
    private String codigoLocal;
    private String nombreLocal;
    private String codigoCarga;

    public BultoDTO(String codigoBulto, String estadoRecepcion, String codigoLocal, String nombreLocal, String codigoCarga) {
        this.codigoBulto = codigoBulto;
        this.estadoRecepcion = estadoRecepcion;
        this.codigoLocal = codigoLocal;
        this.nombreLocal = nombreLocal;
        this.codigoCarga = codigoCarga;
    }

    public String getCodigoBulto() {
        return codigoBulto;
    }

    public void setCodigoBulto(String codigoBulto) {
        this.codigoBulto = codigoBulto;
    }

    public String getEstadoRecepcion() {
        return estadoRecepcion;
    }

    public void setEstadoRecepcion(String estadoRecepcion) {
        this.estadoRecepcion = estadoRecepcion;
    }

    public String getCodigoLocal(){
        return codigoLocal;
    }

    public void setCodigoLocal(String codigoLocal){
        this.codigoLocal = codigoLocal;
    }

    public String getNombreLocal(){
        return nombreLocal;
    }

    public void setNombreLocal(String nombreLocal){
        this.nombreLocal = nombreLocal;
    }

    public String getCodigoCarga(){
        return codigoCarga;
    }

    public void setCodigoCarga(String codigoCarga){
        this.codigoCarga = codigoCarga;
    }
}
