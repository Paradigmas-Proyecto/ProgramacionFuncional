package cr.ac.una.proyectoparadigmas.service;

import cr.ac.una.proyectoparadigmas.entity.LogEntry;
import cr.ac.una.proyectoparadigmas.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para procesar los registros de logs de la aplicación.
 * Aquí se implementan todas las funciones solicitadas en el enunciado del proyecto,
 * utilizando programación funcional en Java (Streams, lambdas, funciones puras).
 *
 * Métodos implementados:
 * - Reportes de errores (conteo, top 3, horas pico)
 * - Reportes de tiempos de respuesta (mínimo, máximo, promedio, mediana, distribución por endpoint)
 * - Reportes de uso (conteo por endpoint y método HTTP)
 * - Reportes de alertas y eventos críticos
 * - Reporte del estado general de la aplicación
 *
 * Todos los cálculos se realizan sobre la lista de registros de logs almacenados
 * en la base de datos mediante LogRepository.
 */
@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // ========= Helpers para Map<String,Object> =========
    private static Map<String, Object> map2(String k1, Object v1, String k2, Object v2) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }

    // ========= Reporte de Errores =========

    /**
     * Número total de errores registrados por código de estado HTTP.
     * Ejemplo de salida (JSON “bonito” sin DTOs):
     * [
     *   {"codigo":500, "cantidad":12},
     *   {"codigo":404, "cantidad":5},
     *   {"codigo":400, "cantidad":3}
     * ]
     */
    public List<Map<String, Object>> erroresPorCodigo() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 400) // Solo errores
                .collect(Collectors.groupingBy(LogEntry::getStatusCode, Collectors.counting()))
                .entrySet().stream()
                .map(e -> map2("codigo", e.getKey(), "cantidad", e.getValue()))
                .sorted(Comparator.comparingLong(m -> -((Number) m.get("cantidad")).longValue()))
                .toList();
    }

    /**
     * Los 3 errores más frecuentes en los registros.
     * Ejemplo de salida:
     * [
     *   {"codigo":500, "cantidad":12},
     *   {"codigo":404, "cantidad":5},
     *   {"codigo":400, "cantidad":3}
     * ]
     */
    public List<Map<String, Object>> top3Errores() {
        return erroresPorCodigo().stream()
                .limit(3)
                .toList();
    }

    /**
     * Horas del día en las que se generaron más errores.
     * Ejemplo de salida:
     * [
     *   {"hora":18, "cantidad":12},
     *   {"hora":13, "cantidad":7},
     *   {"hora":0,  "cantidad":1}
     * ]
     */
    public List<Map<String, Object>> horasPicoErrores() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 400)
                .collect(Collectors.groupingBy(
                        log -> log.getTimestamp().getHour(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(e -> map2("hora", e.getKey(), "cantidad", e.getValue()))
                .sorted(Comparator.comparingLong(m -> -((Number) m.get("cantidad")).longValue()))
                .toList();
    }

    // ========= Reporte de Tiempos de Respuesta =========

    /**
     * Estadísticas de los tiempos de respuesta: mínimo, máximo, promedio y mediana.
     * Ejemplo de salida:
     * { "min":10, "max":900, "promedio":140.5, "mediana":120.0 }
     */
    public Map<String, Double> estadisticasTiempos() {
        List<Long> tiempos = logRepository.findAll().stream()
                .map(LogEntry::getTiempoRespuesta)
                .sorted()
                .toList();

        if (tiempos.isEmpty()) return Map.of(
                "min", 0d, "max", 0d, "promedio", 0d, "mediana", 0d
        );

        double promedio = tiempos.stream().mapToLong(Long::longValue).average().orElse(0);
        double min = tiempos.get(0);
        double max = tiempos.get(tiempos.size() - 1);
        double mediana = (tiempos.size() % 2 == 0)
                ? (tiempos.get(tiempos.size() / 2 - 1) + tiempos.get(tiempos.size() / 2)) / 2.0
                : tiempos.get(tiempos.size() / 2);

        return Map.of(
                "min", min,
                "max", max,
                "promedio", promedio,
                "mediana", mediana
        );
    }

    /**
     * Distribución de los tiempos de respuesta agrupados por endpoint (suma total).
     * Ejemplo de salida:
     * [
     *   {"endpoint":"/api/persona", "total":1234},
     *   {"endpoint":"/api/logs",    "total": 879}
     * ]
     */
    public List<Map<String, Object>> distribucionTiemposPorEndpoint() {
        return logRepository.findAll().stream()
                .collect(Collectors.groupingBy(LogEntry::getEndpoint,
                        Collectors.summingLong(LogEntry::getTiempoRespuesta)))
                .entrySet().stream()
                .map(e -> map2("endpoint", e.getKey(), "total", e.getValue()))
                .sorted(Comparator.comparingLong(m -> -((Number) m.get("total")).longValue()))
                .toList();
    }

    // ========= Reporte de Uso de Endpoints =========

    /**
     * Conteo de peticiones por endpoint.
     * Ejemplo de salida:
     * [
     *   {"endpoint":"/api/persona", "cantidad":20},
     *   {"endpoint":"/api/logs",    "cantidad":15}
     * ]
     */
    public List<Map<String, Object>> usoPorEndpoint() {
        return logRepository.findAll().stream()
                .collect(Collectors.groupingBy(LogEntry::getEndpoint, Collectors.counting()))
                .entrySet().stream()
                .map(e -> map2("endpoint", e.getKey(), "cantidad", e.getValue()))
                .sorted(Comparator.comparingLong(m -> -((Number) m.get("cantidad")).longValue()))
                .toList();
    }

    /**
     * Top 3 endpoints más utilizados.
     * Ejemplo de salida: 3 elementos ordenados desc por cantidad.
     */
    public List<Map<String, Object>> top3EndpointsMasUsados() {
        return usoPorEndpoint().stream()
                .limit(3)
                .toList();
    }

    /**
     * Top 3 endpoints menos utilizados.
     * Ejemplo de salida: 3 elementos ordenados asc por cantidad.
     */
    public List<Map<String, Object>> top3EndpointsMenosUsados() {
        return logRepository.findAll().stream()
                .collect(Collectors.groupingBy(LogEntry::getEndpoint, Collectors.counting()))
                .entrySet().stream()
                .map(e -> map2("endpoint", e.getKey(), "cantidad", e.getValue()))
                .sorted(Comparator.comparingLong(m -> ((Number) m.get("cantidad")).longValue()))
                .limit(3)
                .toList();
    }

    /**
     * Conteo de peticiones agrupadas por método HTTP.
     * Ejemplo de salida:
     * [
     *   {"metodo":"GET",  "cantidad":30},
     *   {"metodo":"POST", "cantidad":12}
     * ]
     */
    public List<Map<String, Object>> usoPorMetodoHttp() {
        return logRepository.findAll().stream()
                .collect(Collectors.groupingBy(LogEntry::getMetodoHttp, Collectors.counting()))
                .entrySet().stream()
                .map(e -> map2("metodo", e.getKey(), "cantidad", e.getValue()))
                .sorted(Comparator.comparing(m -> String.valueOf(m.get("metodo"))))
                .toList();
    }

    // ========= Reporte de Alertas y Eventos Críticos =========

    /**
     * Lista de eventos críticos detectados.
     * Consideramos críticos aquellos con status >= 500.
     */
    public List<LogEntry> eventosCriticos() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 500)
                .toList();
    }

    /**
     * Cantidad de eventos críticos detectados.
     */
    public long cantidadEventosCriticos() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 500)
                .count();
    }

    // ========= Reporte del Estado General =========

    /**
     * Resumen del estado de la aplicación basado en los logs:
     * total de peticiones, total de errores y tiempo promedio de respuesta.
     * Ejemplo de salida:
     * { "totalPeticiones":1000, "totalErrores":17, "tiempoPromedio":135.4 }
     */
    public Map<String, Object> estadoAplicacion() {
        List<LogEntry> logs = logRepository.findAll();
        long totalPeticiones = logs.size();
        long totalErrores = logs.stream().filter(log -> log.getStatusCode() >= 400).count();
        double tiempoPromedio = logs.stream().mapToLong(LogEntry::getTiempoRespuesta).average().orElse(0);

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("totalPeticiones", totalPeticiones);
        r.put("totalErrores", totalErrores);
        r.put("tiempoPromedio", tiempoPromedio);
        return r;
    }
}


