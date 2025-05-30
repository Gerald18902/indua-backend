package com.induamerica.backend.dto;

import java.util.List;

public class ReporteRecepcionDTO {
    private int total;
    private double porcentajeBuenEstado;
    private double porcentajeDeteriorado;
    private double porcentajeFaltante;
    private List<BultoDTO> bultosProblema;

    public ReporteRecepcionDTO(int total, double porcentajeBuenEstado, double porcentajeDeteriorado,
                               double porcentajeFaltante, List<BultoDTO> bultosProblema) {
        this.total = total;
        this.porcentajeBuenEstado = porcentajeBuenEstado;
        this.porcentajeDeteriorado = porcentajeDeteriorado;
        this.porcentajeFaltante = porcentajeFaltante;
        this.bultosProblema = bultosProblema;
    }

    public int getTotal() {
        return total;
    }

    public double getPorcentajeBuenEstado() {
        return porcentajeBuenEstado;
    }

    public double getPorcentajeDeteriorado() {
        return porcentajeDeteriorado;
    }

    public double getPorcentajeFaltante() {
        return porcentajeFaltante;
    }

    public List<BultoDTO> getBultosProblema() {
        return bultosProblema;
    }
}
