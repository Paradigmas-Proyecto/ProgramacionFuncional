package cr.ac.una.proyectoparadigmas.service;

import cr.ac.una.proyectoparadigmas.dto.EndpointsUso;
import cr.ac.una.proyectoparadigmas.entity.LogEntry;
import cr.ac.una.proyectoparadigmas.repository.LogRepository;
import org.springframework.stereotype.Service;
import cr.ac.una.proyectoparadigmas.dto.ErrorCount;
import cr.ac.una.proyectoparadigmas.dto.EndpointStats;

import java.util.*;
import java.util.function.Predicate;
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
   /* public List<Map.Entry<Integer, Long>> top3Errores() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 400)
                .collect(Collectors.groupingBy(LogEntry::getStatusCode, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());
    }*/
    public List<ErrorCount> top3Errores() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 400)
                .collect(Collectors.groupingBy(LogEntry::getStatusCode, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(3)
                .map(e -> new ErrorCount(e.getKey(), e.getValue())) // convertir a DTO
                .toList();
    }

    /**
     * Horas del día en las que se generaron más errores.
     * Ejemplo de salida: {0=1, 13=7, 18=12}
     */
   /* public Map<Integer, Long> horasPicoErrores() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 400)
                .collect(Collectors.groupingBy(
                        log -> log.getTimestamp().getHour(),
                        Collectors.counting()
                ));
    }
*/
    public Map<Integer, Long> horasPicoErrores() {
        return logRepository.findAll().stream()
                .filter(l -> Optional.ofNullable(l.getStatusCode()).orElse(0) >= 400) // solo errores
                .map(l -> Optional.ofNullable(l.getTimestamp()).map(ts -> ts.getHour()))
                .flatMap(Optional::stream) // descarta nulos
                .collect(Collectors.groupingBy(h -> h, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (a,b)->a, LinkedHashMap::new
                ));
    }


    // ========= Reporte de Tiempos de Respuesta =========

    /**
     * Estadísticas de los tiempos de respuesta: mínimo, máximo, promedio y mediana.
     * Ejemplo de salida: {"min"=10, "max"=900, "promedio"=140.5, "mediana"=120.0}
     */
    /*public Map<String, Double> estadisticasTiempos() {
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
    }*/
    public Map<String, Double> estadisticasTiempos() {
        List<Long> tiempos = logRepository.findAll().stream()
                .map(l -> Optional.ofNullable(l.getTiempoRespuesta()).orElse(0L))
                .filter(t -> t > 0)
                .sorted()
                .toList();

        if (tiempos.isEmpty()) {
            return Map.of("min", 0.0, "max", 0.0, "promedio", 0.0, "mediana", 0.0);
        }

        double promedio = tiempos.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        double min = tiempos.get(0);  // primer elemento
        double max = tiempos.get(tiempos.size() - 1); // último elemento

        double mediana = (tiempos.size() % 2 == 0)
                ? (tiempos.get(tiempos.size()/2 - 1) + tiempos.get(tiempos.size()/2)) / 2.0
                : tiempos.get(tiempos.size()/2);

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
   /* public Map<String, Long> distribucionTiemposPorEndpoint() {
        return logRepository.findAll().stream()
                .collect(Collectors.groupingBy(LogEntry::getEndpoint,
                        Collectors.summingLong(LogEntry::getTiempoRespuesta)));
    }*/
    public Map<String, EndpointStats> distribucionTiemposPorEndpoint() {
        return logRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        l -> Optional.ofNullable(l.getEndpoint()).orElse("(desconocido)"),
                        Collectors.mapping(
                                l -> Optional.ofNullable(l.getTiempoRespuesta()).orElse(0L),
                                Collectors.collectingAndThen(Collectors.toList(), tiempos -> {
                                    List<Long> vals = tiempos.stream().filter(t -> t > 0).sorted().toList();
                                    if (vals.isEmpty()) return new EndpointStats(0, 0, 0.0);
                                    long min = vals.get(0);
                                    long max = vals.get(vals.size() - 1);
                                    double avg = vals.stream().mapToLong(Long::longValue).average().orElse(0.0);
                                    return new EndpointStats(min, max, avg);
                                })
                        )
                ));
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
   /* public List<LogEntry> eventosCriticos() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 500)
                .toList();
    }
*/
    /**
     * Cantidad de eventos críticos detectados.
     */
    /*public long cantidadEventosCriticos() {
        return logRepository.findAll().stream()
                .filter(log -> log.getStatusCode() >= 500)
                .count();
    }*/
// helper privado
    private Predicate<LogEntry> esCritico() {
        return l -> "ERROR".equalsIgnoreCase(
                Optional.ofNullable(l.getNivel()).orElse("")
        )
                && Optional.ofNullable(l.getStatusCode()).orElse(0) >= 500;
    }

    // lista de eventos críticos
    public List<LogEntry> eventosCriticos() {
        return logRepository.findAll().stream()
                .filter(esCritico())
                .toList();
    }

    // cantidad de eventos críticos
    public long cantidadEventosCriticos() {
        return logRepository.findAll().stream()
                .filter(esCritico())
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

    //metodos nuevo de timepo de uso
    public EndpointsUso topYLeastEndpoints() {
        Map<String, Long> counts = logRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        l -> Optional.ofNullable(l.getEndpoint()).orElse("(desconocido)"),
                        Collectors.counting()
                ));

        List<Map.Entry<String, Long>> sorted = counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList();

        List<String> top = sorted.stream().limit(3).map(Map.Entry::getKey).toList();
        List<String> least = sorted.stream()
                .skip(Math.max(sorted.size() - 3, 0))
                .map(Map.Entry::getKey).toList();

        return new EndpointsUso(top, least);
    }

}
