package com.induamerica.backend.controller;

import com.induamerica.backend.dto.LocalDTO;
import com.induamerica.backend.dto.RutaPersonalizadaDTO;
import com.induamerica.backend.dto.RutaPersonalizadaRequest;
import com.induamerica.backend.dto.ReporteTransporteDTO;
import com.induamerica.backend.dto.RutaMapaDTO;
import com.induamerica.backend.model.Carga;
import com.induamerica.backend.model.UnidadTransporte;
import com.induamerica.backend.service.RutaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping
    public List<RutaPersonalizadaDTO> listarRutas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) String codigoCarga) {
        return rutaService.listarRutasPersonalizadas(fecha, codigoCarga);
    }

    @GetMapping("/cargas-disponibles")
    public List<Carga> listarCargasDisponibles() {
        return rutaService.obtenerCargasDisponiblesParaRuta();
    }

    @GetMapping("/unidades-transporte")
    public List<UnidadTransporte> listarUnidades() {
        return rutaService.listarUnidades();
    }

    @GetMapping("/locales-en-frecuencia/{idCarga}")
    public List<LocalDTO> listarLocalesNoAsignados(@PathVariable Long idCarga) {
        return rutaService.obtenerLocalesPendientesPorAsignar(idCarga);
    }

    @PostMapping
    public ResponseEntity<Map<String, Integer>> registrarRuta(@RequestBody RutaPersonalizadaRequest request) {
        Map<String, Integer> resumen = rutaService.registrarRutaPersonalizada(request);
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/locales-de-ruta/{idRuta}")
    public List<LocalDTO> listarLocalesDeRuta(@PathVariable Long idRuta) {
        return rutaService.obtenerLocalesPorRuta(idRuta);
    }

    @GetMapping("/reporte-transporte/{idCarga}")
    public ReporteTransporteDTO generarReporteTransporte(@PathVariable Long idCarga) {
        return rutaService.obtenerReporteTransporte(idCarga);
    }

    @GetMapping("/cargas-con-ruta")
    public List<Carga> listarCargasConRuta() {
        return rutaService.obtenerCargasConRuta();
    }

    @GetMapping("/mapa-ruta/{idRuta}")
    public RutaMapaDTO obtenerMapaDeRuta(@PathVariable Long idRuta) {
        return rutaService.obtenerMapaPorRuta(idRuta);
    }
}
