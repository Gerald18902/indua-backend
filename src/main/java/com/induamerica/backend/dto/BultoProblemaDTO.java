package com.induamerica.backend.dto;

public class BultoProblemaDTO {
    private String codigoBulto;
    private String estadoRecepcion;
    private String codigoLocal;
    private String nombreLocal;

    public BultoProblemaDTO(String codigoBulto, String estadoRecepcion, String codigoLocal, String nombreLocal) {
        this.codigoBulto = codigoBulto;
        this.estadoRecepcion = estadoRecepcion;
        this.codigoLocal = codigoLocal;
        this.nombreLocal = nombreLocal;
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
}
