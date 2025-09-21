package cr.ac.una.proyectoparadigmas.aspect;

import cr.ac.una.proyectoparadigmas.entity.LogEntry;
import cr.ac.una.proyectoparadigmas.repository.LogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.logging.Logger;

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

    //cris
    private HttpServletRequest req() {
        ServletRequestAttributes atts = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return atts != null ? atts.getRequest() : null;
    }

    private HttpServletResponse res() {
        ServletRequestAttributes atts = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return atts != null ? atts.getResponse() : null;
    }



    /*@Around("execution(* cr.ac.una.proyectoparadigmas.controller.PersonaController.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.currentTimeMillis();

        Object result = joinPoint.proceed(); // Ejecuta el método original

        long fin = System.currentTimeMillis();
        long tiempoRespuesta = fin - inicio;

        // Crear un LogEntry y guardarlo en la base de datos
        LogEntry log = new LogEntry();
        log.setTimestamp(LocalDateTime.now());
        log.setNivel("INFO");
        log.setMensaje("Ejecutado: " + joinPoint.getSignature().getName());
        log.setEndpoint("/api/persona"); // Se puede mejorar para obtener dinámicamente
        log.setMetodoHttp("HTTP"); // Aquí también se podría capturar dinámicamente
        log.setStatusCode(200); // Valor fijo, se puede refinar con @AfterReturning / @AfterThrowing
        log.setTiempoRespuesta(tiempoRespuesta);

        logRepository.save(log);

        logger.info("Tiempo de respuesta (" + joinPoint.getSignature().getName() + "): " + tiempoRespuesta + " ms");

        return result;
    }*/
    //@Around("execution(* cr.ac.una.proyectoparadigmas.controller..*(..))")
    @Around("execution(* cr.ac.una.proyectoparadigmas.controller..*(..)) && " +
            "!@within(org.springframework.web.bind.annotation.RestControllerAdvice)")

    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.currentTimeMillis();

        HttpServletRequest request = req();
        HttpServletResponse response = res();

        String endpoint = request != null ? request.getRequestURI() : "(desconocido)";
        String metodo   = request != null ? request.getMethod()     : "(desconocido)";

        try {
            Object result = joinPoint.proceed(); // Ejecuta el método original

            long tiempoRespuesta = System.currentTimeMillis() - inicio;
            //int status = response != null ? response.getStatus() : 200;
            // NUEVO: si el método devolvió ResponseEntity, usa ese status
            int status;
            if (result instanceof org.springframework.http.ResponseEntity<?> resp) {
                status = resp.getStatusCodeValue();
            } else {
                status = (response != null ? response.getStatus() : 200);
            }

            LogEntry log = new LogEntry();
            log.setTimestamp(LocalDateTime.now());
            log.setNivel("INFO");
            log.setMensaje("Ejecutado: " + joinPoint.getSignature().getName());
            log.setEndpoint(endpoint);
            log.setMetodoHttp(metodo);
            log.setStatusCode(status);
            log.setTiempoRespuesta(tiempoRespuesta);
            logRepository.save(log);

            logger.info("Tiempo de respuesta (" + joinPoint.getSignature().getName() + "): " + tiempoRespuesta + " ms");
            return result;

        } catch (Throwable ex) {
            long tiempoRespuesta = System.currentTimeMillis() - inicio;
            int status = (response != null && response.getStatus() >= 400) ? response.getStatus() : 500;

            LogEntry log = new LogEntry();
            log.setTimestamp(LocalDateTime.now());
            log.setNivel("ERROR");
            log.setMensaje(ex.getClass().getSimpleName() + ": " + (ex.getMessage() != null ? ex.getMessage() : "Error"));
            log.setEndpoint(endpoint);
            log.setMetodoHttp(metodo);
            log.setStatusCode(status);
            log.setTiempoRespuesta(tiempoRespuesta);
            logRepository.save(log);

            logger.warning("ERROR (" + metodo + " " + endpoint + ") status=" + status + " en " + tiempoRespuesta + " ms");
            throw ex; // importante: re-lanzar para que Spring responda con el error real
        }
    }

}
