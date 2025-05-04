package com.induamerica.backend.dto;

import java.util.List;

public class ReporteFrecuenciaDTO {
    private double porcentajeEnFrecuencia;
    private double porcentajeFueraFrecuencia;
    private List<LocalFrecuenciaDTO> localesEnFrecuencia;
    private List<LocalFrecuenciaDTO> localesFueraFrecuencia;


    public ReporteFrecuenciaDTO(double porcentajeEnFrecuencia, double porcentajeFueraFrecuencia,
                                List<LocalFrecuenciaDTO> localesEnFrecuencia, List<LocalFrecuenciaDTO> localesFueraFrecuencia) {
        this.porcentajeEnFrecuencia = porcentajeEnFrecuencia;
        this.porcentajeFueraFrecuencia = porcentajeFueraFrecuencia;
        this.localesEnFrecuencia = localesEnFrecuencia;
        this.localesFueraFrecuencia = localesFueraFrecuencia;
    }

    public double getPorcentajeEnFrecuencia(){
        return porcentajeEnFrecuencia;
    }

    public void setPorcentajeEnFrencuencia(double porcentajeEnFrecuencia){
        this.porcentajeEnFrecuencia = porcentajeEnFrecuencia;
    }

    public double getPorcentajeFueraFrecuencia(){
        return porcentajeFueraFrecuencia;
    }

    public void setPorcentajeFueraFrencuencia(double porcentajeFueraFrecuencia){
        this.porcentajeFueraFrecuencia = porcentajeFueraFrecuencia;
    }

    public List<LocalFrecuenciaDTO> getLocalesEnFrecuencia(){
        return localesEnFrecuencia;
    }

    public List<LocalFrecuenciaDTO> getLocalesFueraFrecuencia(){
        return localesFueraFrecuencia;
    }
}
