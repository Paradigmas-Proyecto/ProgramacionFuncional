package cr.ac.una.proyectoparadigmas.controller;

import cr.ac.una.proyectoparadigmas.entity.LogEntry;
import cr.ac.una.proyectoparadigmas.service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para exponer los reportes de logs.
 * Aquí se definen los endpoints solicitados en el enunciado del proyecto.
 * Cada endpoint consume el servicio LogService, que implementa la lógica funcional
 * con streams y lambdas.
 *
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
    public ResponseEntity<List<Map<String, Object>>> erroresPorTipo() {
        // Ej: [ {"codigo":500,"cantidad":12}, {"codigo":404,"cantidad":5} ]
        return ResponseEntity.ok(logService.erroresPorCodigo());
    }

    @GetMapping("/reportes/errores/top3")
    public ResponseEntity<List<Map<String, Object>>> top3Errores() {
        // Ej: [ {"codigo":500,"cantidad":12}, {"codigo":404,"cantidad":5}, {"codigo":400,"cantidad":3} ]
        return ResponseEntity.ok(logService.top3Errores());
    }

    @GetMapping("/reportes/errores/horas-pico")
    public ResponseEntity<List<Map<String, Object>>> horasPicoErrores() {
        // Ej: [ {"hora":18,"cantidad":12}, {"hora":13,"cantidad":7}, {"hora":0,"cantidad":1} ]
        return ResponseEntity.ok(logService.horasPicoErrores());
    }

    // ========= Reporte de Tiempos =========
    @GetMapping("/reportes/tiempos/estadisticas")
    public ResponseEntity<Map<String, Double>> estadisticasTiempos() {
        // Ej: { "min":10, "max":900, "promedio":140.5, "mediana":120.0 }
        return ResponseEntity.ok(logService.estadisticasTiempos());
    }

    @GetMapping("/reportes/tiempos/distribucion")
    public ResponseEntity<List<Map<String, Object>>> distribucionTiempos() {
        // Ej: [ {"endpoint":"/api/persona","total":1234}, {"endpoint":"/api/logs","total":879} ]
        return ResponseEntity.ok(logService.distribucionTiemposPorEndpoint());
    }

    // ========= Reporte de Uso =========
    @GetMapping("/reportes/uso/endpoints")
    public ResponseEntity<List<Map<String, Object>>> usoEndpoints() {
        // Ej: [ {"endpoint":"/api/persona","cantidad":20}, {"endpoint":"/api/logs","cantidad":15} ]
        return ResponseEntity.ok(logService.usoPorEndpoint());
    }

    @GetMapping("/reportes/uso/top3-mas")
    public ResponseEntity<List<Map<String, Object>>> top3MasUsados() {
        // Ej: [ {"endpoint":"/api/persona","cantidad":20}, ... ] (3 elementos)
        return ResponseEntity.ok(logService.top3EndpointsMasUsados());
    }

    @GetMapping("/reportes/uso/top3-menos")
    public ResponseEntity<List<Map<String, Object>>> top3MenosUsados() {
        // Ej: [ {"endpoint":"/api/poco","cantidad":1}, ... ] (3 elementos)
        return ResponseEntity.ok(logService.top3EndpointsMenosUsados());
    }

    @GetMapping("/reportes/uso/http")
    public ResponseEntity<List<Map<String, Object>>> usoPorMetodoHttp() {
        // Ej: [ {"metodo":"GET","cantidad":120}, {"metodo":"POST","cantidad":40}, {"metodo":"PUT","cantidad":6}, {"metodo":"DELETE","cantidad":2} ]
        return ResponseEntity.ok(logService.usoPorMetodoHttp());
    }

    // ========= Reporte de Alertas =========
    @GetMapping("/reportes/alertas/eventos")
    public ResponseEntity<List<LogEntry>> eventosCriticos() {
        // Ej: [ {"timestamp":"...","nivel":"ERROR",...}, ... ]
        return ResponseEntity.ok(logService.eventosCriticos());
    }

    @GetMapping("/reportes/alertas/cantidad")
    public ResponseEntity<Long> cantidadEventosCriticos() {
        return ResponseEntity.ok(logService.cantidadEventosCriticos());
    }

    // ========= Reporte de Estado =========
    @GetMapping("/reportes/estado")
    public ResponseEntity<Map<String, Object>> estadoAplicacion() {
        // Ej: { "totalPeticiones":1000, "totalErrores":17, "tiempoPromedio":135.4 }
        return ResponseEntity.ok(logService.estadoAplicacion());
    }
}

