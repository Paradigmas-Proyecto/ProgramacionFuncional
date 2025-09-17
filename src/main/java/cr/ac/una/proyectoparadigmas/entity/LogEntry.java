package cr.ac.una.proyectoparadigmas.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad que representa un registro de log en el sistema.
 * Será usada por el repositorio y servicios para analizar eventos,
 * errores y tiempos de respuesta.
 */
@Entity
@Data
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Requeridos por el proyecto

    private LocalDateTime timestamp; // Momento exacto en que ocurrió la petición o evento
    private String nivel; //Nivel del log: INFO, ERROR, WARN, DEBUG, TRACE
    private String mensaje; // /** Mensaje descriptivo del log (detalles del evento) */
    private String endpoint; // Endpoint al que se hizo la petición, ej: /api/persona
    private String metodoHttp; // URL o ruta del endpoint accedido
    private int statusCode;    // Código de respuesta HTTP devuelto: 200, 404, 500, etc.
    private long tiempoRespuesta;//Tiempo total de respuesta de la petición en milisegundos

}
