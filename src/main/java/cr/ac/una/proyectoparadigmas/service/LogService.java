package cr.ac.una.proyectoparadigmas.service;

import cr.ac.una.proyectoparadigmas.entity.LogEntry;
import java.util.List;
import java.util.Map;
/**
 * Servicio de logs.
 * Persona 2 implementará aquí el procesamiento funcional
 * (streams, lambdas, filtros, agrupaciones, etc.)
 */
public interface LogService {

    // Reportes de Errores
    Map<Integer, Long> erroresPorCodigo();
    List<Map.Entry<Integer, Long>> top3Errores();
    Map<Integer, Long> horasPicoErrores();

    // Reportes de Tiempos
    record Estadisticas(long min, long max, double promedio, double mediana) {}
    Estadisticas estadisticasTiempos();
    Map<String, Estadisticas> distribucionTiemposPorEndpoint();

    // Reportes de Uso
    Map<String, Long> usoPorEndpoint();
    Map<String, Long> usoPorMetodoHttp();

    // Alertas y Estado
    List<LogEntry> alertasCriticas();
    long cantidadAlertasCriticas();
    record Estado(long totalPeticiones, long totalErrores, double tiempoPromedio) {}
    Estado estadoAplicacion();
}

