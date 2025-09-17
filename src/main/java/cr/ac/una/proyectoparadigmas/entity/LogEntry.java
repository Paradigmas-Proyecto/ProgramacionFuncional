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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMetodoHttp() {
        return metodoHttp;
    }

    public void setMetodoHttp(String metodoHttp) {
        this.metodoHttp = metodoHttp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public long getTiempoRespuesta() {
        return tiempoRespuesta;
    }

    public void setTiempoRespuesta(long tiempoRespuesta) {
        this.tiempoRespuesta = tiempoRespuesta;
    }
}
