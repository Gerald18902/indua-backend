package com.induamerica.backend.controller;

import com.induamerica.backend.dto.CargaRequest;
import com.induamerica.backend.dto.ReporteFrecuenciaDTO;
import com.induamerica.backend.dto.ReporteRecepcionDTO;
import com.induamerica.backend.model.Carga;
import com.induamerica.backend.repository.CargaRepository;
import com.induamerica.backend.service.CargaService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cargas")
@CrossOrigin(origins = "http://localhost:5173")
public class CargaController {

    @Autowired
    private CargaService cargaService;

    @Autowired
    private CargaRepository cargaRepository;

    @GetMapping
    public List<Carga> listarCargas() {
        return cargaRepository.findAll();
    }

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
    public ResponseEntity<ReporteRecepcionDTO> getReporteRecepcion(@PathVariable int idCarga) {
        return ResponseEntity.ok(cargaService.generarReporteRecepcion(idCarga));
    }

    @GetMapping("/reporte-frecuencia/{idCarga}")
    public ResponseEntity<ReporteFrecuenciaDTO> getReporteFrecuencia(@PathVariable int idCarga) {
        return ResponseEntity.ok(cargaService.generarReporteFrecuencia(idCarga));
    }

}
