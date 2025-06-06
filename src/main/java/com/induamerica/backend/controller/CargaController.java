package com.induamerica.backend.controller;

import com.induamerica.backend.dto.CargaDTO;
import com.induamerica.backend.dto.CargaRequest;
import com.induamerica.backend.dto.ReporteFrecuenciaDTO;
import com.induamerica.backend.dto.ReporteRecepcionDTO;
import com.induamerica.backend.repository.CargaRepository;
import com.induamerica.backend.service.CargaService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.induamerica.backend.dto.ReporteDespachoDTO;

@RestController
@RequestMapping("/api/cargas")
public class CargaController {

    @Autowired
    private CargaService cargaService;

    @Autowired
    private CargaRepository cargaRepository;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> registrarCarga(
            @RequestParam("fechaCarga") String fechaCarga,
            @RequestParam("codigoCarga") String codigoCarga,
            @RequestParam("placaCarreta") String placaCarreta,
            @RequestParam("duenoCarreta") String duenoCarreta,
            @RequestParam("file") MultipartFile file) {

        CargaRequest request = new CargaRequest();
        request.setFechaCarga(fechaCarga);
        request.setCodigoCarga(codigoCarga);
        request.setPlacaCarreta(placaCarreta);
        request.setDuenoCarreta(duenoCarreta);

        return cargaService.registrarCargaConBultos(request, file);
    }

    @GetMapping("/reporte-recepcion/{idCarga}")
    public ResponseEntity<ReporteRecepcionDTO> getReporteRecepcion(@PathVariable Long idCarga) {
        return ResponseEntity.ok(cargaService.generarReporteRecepcion(idCarga));
    }

    @GetMapping("/reporte-frecuencia/{idCarga}")
    public ResponseEntity<ReporteFrecuenciaDTO> getReporteFrecuencia(@PathVariable Long idCarga) {
        return ResponseEntity.ok(cargaService.generarReporteFrecuencia(idCarga));
    }

    @GetMapping("/reporte-despacho/{codigoCarga}")
    public ResponseEntity<ReporteDespachoDTO> getReporteDespacho(@PathVariable String codigoCarga) {
        return ResponseEntity.ok(cargaService.generarReporteDespacho(codigoCarga));
    }

    @GetMapping
    public List<CargaDTO> listarCargas() {
        return cargaRepository.findAll()
                .stream()
                .map(c -> new CargaDTO(c.getIdCarga(), c.getCodigoCarga(), c.getFechaCarga())) // ✅ incluye idCarga
                .toList();
    }

}
