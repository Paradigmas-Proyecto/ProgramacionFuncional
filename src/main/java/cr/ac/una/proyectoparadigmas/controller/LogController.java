package cr.ac.una.proyectoparadigmas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    // ========= Reporte de Errores =========
    @GetMapping("/reportes/errores")
    public ResponseEntity<?> erroresPorTipo() {
        // Ej: { "500": 12, "404": 5 }
        return ResponseEntity.ok(Map.of());
    }

    @GetMapping("/reportes/errores/top3")
    public ResponseEntity<?> top3Errores() {
        // Ej: [ {"status":500,"conteo":12}, {"status":404,"conteo":5}, {"status":400,"conteo":3} ]
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/reportes/errores/horas-pico")
    public ResponseEntity<?> horasPicoErrores() {
        // Ej: { "0": 1, "13": 7, "18": 12 }
        return ResponseEntity.ok(Map.of());
    }

    // ========= Reporte de Tiempos =========
    @GetMapping("/reportes/tiempos/estadisticas")
    public ResponseEntity<?> estadisticasTiempos() {
        // Ej: { "min":10, "max":900, "promedio":140.5, "mediana":120.0 }
        return ResponseEntity.ok(Map.of("min",0,"max",0,"promedio",0,"mediana",0));
    }

    @GetMapping("/reportes/tiempos/distribucion")
    public ResponseEntity<?> distribucionTiempos() {
        // Ej: { "/api/persona": { "min":10,"max":200,"promedio":80 }, ... }
        return ResponseEntity.ok(Map.of());
    }

    // ========= Reporte de Uso =========
    @GetMapping("/reportes/uso/endpoints")
    public ResponseEntity<?> usoEndpoints() {
        // Ej: { "top":[...], "least":[...] }
        return ResponseEntity.ok(Map.of("top", List.of(), "least", List.of()));
    }

    @GetMapping("/reportes/uso/http")
    public ResponseEntity<?> usoPorMetodoHttp() {
        // Ej: { "GET":120, "POST":40, "PUT":6, "DELETE":2 }
        return ResponseEntity.ok(Map.of("GET",0,"POST",0,"PUT",0,"DELETE",0));
    }

    // Alertas y Estado
    @GetMapping("/reportes/alertas")
    public ResponseEntity<?> alertas() {
        // Ej: [ {"timestamp":"...","nivel":"ERROR",...}, ... ]
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/reportes/alertas/cantidad")
    public ResponseEntity<?> cantidadAlertas() {
        return ResponseEntity.ok(0);
    }

    @GetMapping("/reportes/estado")
    public ResponseEntity<?> estado() {
        // Ej: { "totalPeticiones":1000, "totalErrores":17, "tiempoPromedio":135.4 }
        return ResponseEntity.ok(Map.of("totalPeticiones",0,"totalErrores",0,"tiempoPromedio",0));
    }
}

