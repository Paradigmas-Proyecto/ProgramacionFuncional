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

    // ========= Reporte de Errores =========

    /**
     * Número total de errores registrados por código de estado HTTP.
     * Ejemplo de salida: {500=12, 404=5, 400=3}
     */
    public Map<Integer, Long> erroresPorCodigo() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 400) // Solo errores
                .collect(Collectors.groupingBy(LogEntry::getStatusCode, Collectors.counting()));
    }

    /**
     * Los 3 errores más frecuentes en los registros.
     * Ejemplo de salida: [{500=12}, {404=5}, {400=3}]
     */
    public List<Map.Entry<Integer, Long>> top3Errores() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 400)
                .collect(Collectors.groupingBy(LogEntry::getStatusCode, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * Horas del día en las que se generaron más errores.
     * Ejemplo de salida: {0=1, 13=7, 18=12}
     */
    public Map<Integer, Long> horasPicoErrores() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 400)
                .collect(Collectors.groupingBy(
                        log -> log.getTimestamp().getHour(),
                        Collectors.counting()
                ));
    }

    // ========= Reporte de Tiempos de Respuesta =========

    /**
     * Estadísticas de los tiempos de respuesta: mínimo, máximo, promedio y mediana.
     * Ejemplo de salida: {"min"=10, "max"=900, "promedio"=140.5, "mediana"=120.0}
     */
    public Map<String, Double> estadisticasTiempos() {
        List<Long> tiempos = logRepository.findAll().stream()
                .map(LogEntry::getTiempoRespuesta)
                .sorted()
                .toList();

        if (tiempos.isEmpty()) return Map.of();

        double promedio = tiempos.stream().mapToLong(Long::longValue).average().orElse(0);
        double min = tiempos.get(0);
        double max = tiempos.get(tiempos.size() - 1);
        double mediana = (tiempos.size() % 2 == 0) ?
                (tiempos.get(tiempos.size() / 2 - 1) + tiempos.get(tiempos.size() / 2)) / 2.0 :
                tiempos.get(tiempos.size() / 2);

        return Map.of(
                "min", min,
                "max", max,
                "promedio", promedio,
                "mediana", mediana
        );
    }

    /**
     * Distribución de los tiempos de respuesta agrupados por endpoint.
     * Ejemplo de salida: {"/api/persona"=1234, "/api/logs"=879}
     */
    public Map<String, Long> distribucionTiemposPorEndpoint() {
        return logRepository.findAll().stream()
                .collect(Collectors.groupingBy(LogEntry::getEndpoint,
                        Collectors.summingLong(LogEntry::getTiempoRespuesta)));
    }

    // ========= Reporte de Uso de Endpoints =========

    /**
     * Conteo de peticiones por endpoint.
     * Ejemplo de salida: {"/api/persona"=20, "/api/logs"=15}
     */
    public Map<String, Long> usoPorEndpoint() {
        return logRepository.findAll().stream()
                .collect(Collectors.groupingBy(LogEntry::getEndpoint, Collectors.counting()));
    }

    /**
     * Conteo de peticiones agrupadas por método HTTP.
     * Ejemplo de salida: {"GET"=30, "POST"=12}
     */
    public Map<String, Long> usoPorMetodoHttp() {
        return logRepository.findAll().stream()
                .collect(Collectors.groupingBy(LogEntry::getMetodoHttp, Collectors.counting()));
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
     */
    public Map<String, Object> estadoAplicacion() {
        List<LogEntry> logs = logRepository.findAll();
        long totalPeticiones = logs.size();
        long totalErrores = logs.stream().filter(log -> log.getStatusCode() >= 400).count();
        double tiempoPromedio = logs.stream().mapToLong(LogEntry::getTiempoRespuesta).average().orElse(0);

        return Map.of(
                "totalPeticiones", totalPeticiones,
                "totalErrores", totalErrores,
                "tiempoPromedio", tiempoPromedio
        );
    }
}
