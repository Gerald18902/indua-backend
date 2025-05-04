package com.induamerica.backend.service;

import com.induamerica.backend.dto.BultoProblemaDTO;
import com.induamerica.backend.dto.CargaRequest;
import com.induamerica.backend.dto.LocalFrecuenciaDTO;
import com.induamerica.backend.dto.ReporteFrecuenciaDTO;
import com.induamerica.backend.dto.ReporteRecepcionDTO;
import com.induamerica.backend.model.*;
import com.induamerica.backend.repository.BultoRepository;
import com.induamerica.backend.repository.CargaRepository;
import com.induamerica.backend.repository.LocalRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CargaService {

    @Autowired
    private CargaRepository cargaRepository;

    @Autowired
    private BultoRepository bultoRepository;

    @Autowired
    private LocalRepository localRepository;

    private String obtenerValorComoTexto(Cell cell) {
        if (cell == null)
            return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue()).trim();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA -> cell.getCellFormula().trim();
            default -> "";
        };
    }

    public ResponseEntity<?> registrarCargaConBultos(CargaRequest request, MultipartFile excelFile) {
        try {
            boolean existe = cargaRepository.existsByCodigoCarga(request.getCodigoCarga().toUpperCase());
            if (existe) {
                return ResponseEntity.badRequest()
                        .body("Ya existe una carga con el código: " + request.getCodigoCarga());
            }

            // Validar y leer el archivo Excel primero
            InputStream inputStream = excelFile.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Preparar la carga (sin guardar aún)
            Carga carga = new Carga();
            carga.setFechaCarga(LocalDate.parse(request.getFechaCarga()));
            carga.setCodigoCarga(request.getCodigoCarga().toUpperCase());
            carga.setPlacaCarreta(request.getPlacaCarreta().toUpperCase());
            carga.setDuenoCarreta(Carga.DuenoCarreta.valueOf(request.getDuenoCarreta().toUpperCase()));

            Row headerRow = sheet.getRow(0);
            if (headerRow == null ||
                    headerRow.getPhysicalNumberOfCells() < 3 ||
                    !obtenerValorComoTexto(headerRow.getCell(0)).equalsIgnoreCase("Codigo Local") ||
                    !obtenerValorComoTexto(headerRow.getCell(1)).equalsIgnoreCase("Codigo Bulto") ||
                    !obtenerValorComoTexto(headerRow.getCell(2)).equalsIgnoreCase("Estado Recepcion")) {

                workbook.close();
                return ResponseEntity.badRequest()
                        .body("El archivo Excel no tiene el formato correcto. Se esperan las columnas: 'Codigo Local', 'Codigo Bulto' y 'Estado Recepcion'.");
            }

            List<Bulto> bultosTemp = new ArrayList<>();

            for (Row row : sheet) {
                int filaActual = row.getRowNum() + 1;
                if (row.getRowNum() == 0)
                    continue;

                String codigoLocal = obtenerValorComoTexto(row.getCell(0)).toUpperCase();
                String codigoBulto = obtenerValorComoTexto(row.getCell(1)).toUpperCase();
                String estadoRecepcionStr = obtenerValorComoTexto(row.getCell(2)).toUpperCase().replace(" ", "_");

                if (codigoLocal.isBlank() || codigoBulto.isBlank() || estadoRecepcionStr.isBlank()) {
                    workbook.close();
                    return ResponseEntity.badRequest()
                            .body("Fila " + filaActual + ": columnas vacías o mal formateadas.");
                }

                Optional<Local> localOpt = localRepository.findAll()
                        .stream()
                        .filter(l -> l.getCodigo().equalsIgnoreCase(codigoLocal))
                        .findFirst();

                if (localOpt.isEmpty()) {
                    workbook.close();
                    return ResponseEntity.badRequest()
                            .body("Fila " + filaActual + ": código de local inexistente (" + codigoLocal + ").");
                }

                Bulto.EstadoRecepcion estadoRecepcion;
                try {
                    estadoRecepcion = Bulto.EstadoRecepcion.valueOf(estadoRecepcionStr);
                } catch (IllegalArgumentException e) {
                    workbook.close();
                    return ResponseEntity.badRequest()
                            .body("Fila " + filaActual + ": estado de recepción inválido: " + estadoRecepcionStr);
                }

                Bulto bulto = new Bulto();
                bulto.setCodigoBulto(codigoBulto);
                bulto.setLocal(localOpt.get());
                bulto.setCarga(carga);
                bulto.setEstadoRecepcion(estadoRecepcion);
                bulto.setEstadoTransporte(
                        estadoRecepcion == Bulto.EstadoRecepcion.FALTANTE ? null : Bulto.EstadoTransporte.EN_ALMACEN);
                bulto.setEstadoDespacho(null);
                bulto.setFechaTransporte(null);
                bulto.setFechaDespacho(null);

                bultosTemp.add(bulto);
            }

            carga = cargaRepository.save(carga);
            for (Bulto b : bultosTemp) {
                bultoRepository.save(b);
            }

            workbook.close();

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Carga y bultos registrados correctamente.");
            response.put("idCarga", carga.getIdCarga()); // accede al ID ya persistido
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al registrar la carga: " + e.getMessage());
        }
    }

    public ReporteRecepcionDTO generarReporteRecepcion(int idCarga) {
        List<Bulto> bultos = bultoRepository.findByCargaIdCarga(idCarga);
        int total = bultos.size();

        long enBuenEstado = bultos.stream()
                .filter(b -> b.getEstadoRecepcion() == Bulto.EstadoRecepcion.EN_BUEN_ESTADO)
                .count();

        long deteriorados = bultos.stream()
                .filter(b -> b.getEstadoRecepcion() == Bulto.EstadoRecepcion.DETERIORADO)
                .count();

        long faltantes = bultos.stream()
                .filter(b -> b.getEstadoRecepcion() == Bulto.EstadoRecepcion.FALTANTE)
                .count();

        List<BultoProblemaDTO> problemas = bultos.stream()
                .filter(b -> b.getEstadoRecepcion() != Bulto.EstadoRecepcion.EN_BUEN_ESTADO)
                .map(b -> new BultoProblemaDTO(
                        b.getCodigoBulto(),
                        b.getEstadoRecepcion().toString(),
                        b.getLocal().getCodigo(),
                        b.getLocal().getNombre()))
                .collect(Collectors.toList());

        return new ReporteRecepcionDTO(
                total,
                enBuenEstado * 100.0 / total,
                deteriorados * 100.0 / total,
                faltantes * 100.0 / total,
                problemas);
    }

    public ReporteFrecuenciaDTO generarReporteFrecuencia(int idCarga) {
        Optional<Carga> cargaOpt = cargaRepository.findById(idCarga);
        if (cargaOpt.isEmpty()) {
            throw new IllegalArgumentException("Carga no encontrada con ID: " + idCarga);
        }

        LocalDate fechaEvaluar = cargaOpt.get().getFechaCarga().plusDays(2);

        // Obtener bultos y locales únicos
        List<Bulto> bultos = bultoRepository.findByCargaIdCarga(idCarga);
        Set<Local> localesUnicos = bultos.stream()
                .map(Bulto::getLocal)
                .collect(Collectors.toSet());

                List<LocalFrecuenciaDTO> localesEnFrecuencia = new ArrayList<>();
                List<LocalFrecuenciaDTO> localesFueraFrecuencia = new ArrayList<>();
                
                for (Local local : localesUnicos) {
                    PatronAtencion patron = local.getPatronAtencion();
                    DayOfWeek dia = fechaEvaluar.getDayOfWeek();
                
                    boolean enFrecuencia = switch (dia) {
                        case MONDAY -> patron.isLunes();
                        case TUESDAY -> patron.isMartes();
                        case WEDNESDAY -> patron.isMiercoles();
                        case THURSDAY -> patron.isJueves();
                        case FRIDAY -> patron.isViernes();
                        case SATURDAY -> patron.isSabado();
                        case SUNDAY -> patron.isDomingo();
                    };
                
                    LocalFrecuenciaDTO dto = new LocalFrecuenciaDTO(local.getNombre(), local.getCodigo());
                
                    if (enFrecuencia) {
                        localesEnFrecuencia.add(dto);
                    } else {
                        localesFueraFrecuencia.add(dto);
                    }
                }
                

        int total = localesEnFrecuencia.size() + localesFueraFrecuencia.size();
        double porcentajeEn = total > 0 ? (localesEnFrecuencia.size() * 100.0 / total) : 0.0;
        double porcentajeFuera = total > 0 ? (localesFueraFrecuencia.size() * 100.0 / total) : 0.0;

        return new ReporteFrecuenciaDTO(
                Math.round(porcentajeEn * 100.0) / 100.0,
                Math.round(porcentajeFuera * 100.0) / 100.0,
                localesEnFrecuencia,
                localesFueraFrecuencia);
    }

}
