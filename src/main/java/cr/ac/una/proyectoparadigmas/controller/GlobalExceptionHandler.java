package cr.ac.una.proyectoparadigmas.controller;

import cr.ac.una.proyectoparadigmas.entity.LogEntry;
import cr.ac.una.proyectoparadigmas.repository.LogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final LogRepository logRepository;

    public GlobalExceptionHandler(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // === SOLO 400: se registra en DB para que aparezca en los reportes ===
    @ExceptionHandler({
            HttpMessageNotReadableException.class,        // JSON malformado
            MethodArgumentNotValidException.class,        // @Valid falló
            MissingServletRequestParameterException.class // parámetro requerido faltante
    })
    public ResponseEntity<Map<String,Object>> handleBadRequest(Exception ex, HttpServletRequest req) {
        LogEntry log = new LogEntry();
        log.setTimestamp(LocalDateTime.now());
        log.setNivel("ERROR");
        log.setMensaje(ex.getClass().getSimpleName());
        log.setEndpoint(req != null ? req.getRequestURI() : "(desconocido)");
        log.setMetodoHttp(req != null ? req.getMethod() : "(desconocido)");
        log.setStatusCode(400);
        log.setTiempoRespuesta(0L); // no medimos aquí
        logRepository.save(log);

        return ResponseEntity.badRequest().body(Map.of(
                "error", "Bad Request",
                "detail", ex.getClass().getSimpleName()
        ));
    }

    // === 500: NO guardamos en DB para evitar duplicado (lo guarda el Aspect) ===
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleInternal(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "detail", ex.getClass().getSimpleName()
                ));
    }
}
