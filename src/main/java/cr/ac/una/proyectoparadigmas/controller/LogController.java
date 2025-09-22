package cr.ac.una.proyectoparadigmas.controller;

import cr.ac.una.proyectoparadigmas.dto.EndpointStats;
import cr.ac.una.proyectoparadigmas.entity.LogEntry;
import cr.ac.una.proyectoparadigmas.service.LogService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cr.ac.una.proyectoparadigmas.dto.ErrorCount;
import cr.ac.una.proyectoparadigmas.dto.EndpointsUso;

import java.util.List;
import java.util.Map;

/**
     * Controlador REST para exponer los reportes de logs.
     * Aquí se definen los endpoints solicitados en el enunciado del proyecto.
     * Cada endpoint consume el servicio LogService, que implementa la lógica funcional
     * con streams y lambdas.

     * Endpoints:
     * - /api/logs/reportes/errores
     * - /api/logs/reportes/tiempos
     * - /api/logs/reportes/uso
     * - /api/logs/reportes/alertas
     * - /api/logs/reportes/estado
 */

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    // ========= Reporte de Errores =========
    @GetMapping("/reportes/errores")
    public ResponseEntity<Map<Integer, Long>> erroresPorTipo() {
        // Ej: { "500": 12, "404": 5 }
        return ResponseEntity.ok(logService.erroresPorCodigo());
    }

    @GetMapping("/reportes/errores/top3")
    public ResponseEntity<List<ErrorCount>> top3Errores() {
        // Ej: [ {"code":500,"count":12}, {"code":404,"count":5}, {"code":400,"count":3} ]
        return ResponseEntity.ok(logService.top3Errores());
    }


    @GetMapping("/reportes/errores/horas-pico")
    public ResponseEntity<Map<Integer, Long>> horasPicoErrores() {
        // Ej: { "0": 1, "13": 7, "18": 12 }
        return ResponseEntity.ok(logService.horasPicoErrores());
    }

    // ========= Reporte de Tiempos =========
    @GetMapping("/reportes/tiempos/estadisticas")
    public ResponseEntity<Map<String, Double>> estadisticasTiempos() {
        // Ej: { "min":10, "max":900, "promedio":140.5, "mediana":120.0 }
        return ResponseEntity.ok(logService.estadisticasTiempos());
    }


    @GetMapping("/reportes/tiempos/distribucion")
    public ResponseEntity<Map<String, EndpointStats>> distribucionTiempos() {
        return ResponseEntity.ok(logService.distribucionTiemposPorEndpoint());
    }

    // ========= Reporte de Uso =========
    @GetMapping("/reportes/uso/endpoints")
    public ResponseEntity<Map<String, Long>> usoEndpoints() {
        // Ej: { "top":[...], "least":[...] }
        return ResponseEntity.ok(logService.usoPorEndpoint());
    }

    @GetMapping("/reportes/uso/http")
    public ResponseEntity<Map<String, Long>> usoPorMetodoHttp() {
        // Ej: { "GET":120, "POST":40, "PUT":6, "DELETE":2 }
        return ResponseEntity.ok(logService.usoPorMetodoHttp());
    }

    // ========= Reporte de Alertas =========
    @GetMapping("/reportes/alertas/eventos")
    public ResponseEntity<List<LogEntry>> eventosCriticos() {
        // Ej: [ {"timestamp":"...","nivel":"ERROR",...}, ... ]
        return ResponseEntity.ok(logService.eventosCriticos());
    }

    @GetMapping(value = "/reportes/alertas/cantidad", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> cantidadEventosCriticos() {
        long n = logService.cantidadEventosCriticos();  // <- sigue siendo funcional (stream + filter + count en el service)
        return ResponseEntity.ok(Map.of("cantidad", n));
    }
    // ========= Reporte de Estado =========
    @GetMapping("/reportes/estado")
    public ResponseEntity<Map<String, Object>> estadoAplicacion() {
        // Ej: { "totalPeticiones":1000, "totalErrores":17, "tiempoPromedio":135.4 }
        return ResponseEntity.ok(logService.estadoAplicacion());
    }
}

