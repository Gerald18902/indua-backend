package com.induamerica.backend.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ReporteDespachoDTO {
    private String codigoCarga;
    private String fechaCarga;
    private Map<String, Map<String, List<String>>> deteriorados; // Sustento -> Listado de líneas
    private Map<String, Map<String, List<String>>> discrepancias; // Sustento -> Listado de líneas
    private List<String> faltantes;
}
