package com.induamerica.backend.dto;

import java.time.LocalDate;

import com.induamerica.backend.model.Bulto;
import com.induamerica.backend.model.TipoMerma;

public class BultoDespachoDTO {

    private String codigoBulto;
    private String codigoLocal;
    private String nombreLocal;
    private String codigoCarga;
    private LocalDate fechaCarga;
    private String estadoDespacho;
    private String tipoMerma;

    public BultoDespachoDTO(String codigoBulto,
            String codigoLocal,
            String nombreLocal,
            String codigoCarga,
            LocalDate fechaCarga,
            Bulto.EstadoDespacho estadoDespacho,
            TipoMerma tipoMerma) {
        this.codigoBulto = codigoBulto;
        this.codigoLocal = codigoLocal;
        this.nombreLocal = nombreLocal;
        this.codigoCarga = codigoCarga;
        this.fechaCarga = fechaCarga;
        this.estadoDespacho = (estadoDespacho != null) ? estadoDespacho.name() : null;
        this.tipoMerma = (tipoMerma != null) ? tipoMerma.name() : null;
    }

    public String getCodigoBulto() {
        return codigoBulto;
    }

    public void setCodigoBulto(String codigoBulto) {
        this.codigoBulto = codigoBulto;
    }

    public String getCodigoLocal() {
        return codigoLocal;
    }

    public void setCodigoLocal(String codigoLocal) {
        this.codigoLocal = codigoLocal;
    }

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

    public LocalDate getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDate fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public String getEstadoDespacho() {
        return estadoDespacho;
    }

    public void setEstadoDespacho(String estadoDespacho) {
        this.estadoDespacho = estadoDespacho;
    }

    public String getTipoMerma() {
        return tipoMerma;
    }

    public void setTipoMerma(String tipoMerma) {
        this.tipoMerma = tipoMerma;
    }
}
