package cr.ac.una.proyectoparadigmas.aspect;

import cr.ac.una.proyectoparadigmas.entity.LogEntry;
import cr.ac.una.proyectoparadigmas.repository.LogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.logging.Logger;


import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * Aspecto que intercepta métodos de PersonaController.

 * Este aspecto:
 * - Registra mensajes en consola (logger.info).
 * - Guarda en base de datos un LogEntry por cada petición realizada.
 * - Mide el tiempo de respuesta real usando @Around.
 */
@Aspect // Indica que esta clase es un Aspecto de AOP
@Component // Indica que esta clase es un Componente de Spring
public class PersonaAspect {
    private static final Logger logger = Logger.getLogger(PersonaAspect.class.getName());
    private final LogRepository logRepository;

    public PersonaAspect(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    // ========= Antes de ejecutar savePersona =========
    /**
     * Este método se ejecuta ANTES de que se invoque savePersona().
     * Solo escribe en los logs de consola.
     */
    @Before("execution(* cr.ac.una.proyectoparadigmas.controller.PersonaController.savePersona*(..))")
    public void logBeforeSave() {
        logger.info("Antes de ejecutar savePersona()");
    }

    // ========= Después de ejecutar cualquier método =========
    /**
     * Este método se ejecuta DESPUÉS de cualquier método de PersonaController.
     * Solo escribe en los logs de consola.
     */
    @After("execution(* cr.ac.una.proyectoparadigmas.controller.PersonaController.*(..))")
    public void logAfter() {
        logger.info("Después de ejecutar método en PersonaController");
    }

    // ========= Medición del tiempo de respuesta =========
    /**
     * Este método rodea la ejecución de cualquier método de PersonaController.
     * - Calcula el tiempo real de ejecución.
     * - Guarda un LogEntry en la base de datos con toda la información.
     */
    @Around("execution(* cr.ac.una.proyectoparadigmas.controller.PersonaController.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.currentTimeMillis();

        // obtener request/response para capturar endpoint, método y status reales
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
        HttpServletResponse response = attrs != null ? attrs.getResponse() : null;

        int status = 200;
        Object result = null; // <--- agregar esta línea
        try {
            result = joinPoint.proceed(); // Ejecuta el método original
            status = (response != null && response.getStatus() > 0) ? response.getStatus() : 200;
            return result;
        } catch (Throwable t) {
            status = 500; // Si hubo excepción, marca 500 (crítico)
            throw t;
        } finally {
            long fin = System.currentTimeMillis();
            long tiempoRespuesta = fin - inicio;

            // Crear un LogEntry y guardarlo en la base de datos (con datos reales)
            LogEntry log = new LogEntry();
            log.setTimestamp(LocalDateTime.now());
            log.setNivel(status >= 500 ? "ERROR" : "INFO");
            log.setMensaje("Ejecutado: " + joinPoint.getSignature().getName());
            log.setEndpoint(request != null ? request.getRequestURI() : "/api/persona");
            log.setMetodoHttp(request != null ? request.getMethod() : "HTTP");
            int effectiveStatus =
                    (status >= 500) ? status :
                            (result instanceof ResponseEntity<?> re) ? re.getStatusCodeValue() :
                                    (response != null && response.getStatus() > 0) ? response.getStatus() :
                                            200;


            log.setStatusCode(effectiveStatus);
            log.setTiempoRespuesta(tiempoRespuesta);

            logRepository.save(log);

            logger.info("Tiempo de respuesta (" + joinPoint.getSignature().getName() + "): " + tiempoRespuesta + " ms");
        }
    }
}
