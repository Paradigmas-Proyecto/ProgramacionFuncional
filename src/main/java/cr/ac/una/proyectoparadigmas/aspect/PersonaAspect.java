package cr.ac.una.proyectoparadigmas.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect // Indica que esta clase es un Aspecto
@Component // Indica que esta clase es un Componente de Spring

public class PersonaAspect {
    private static final Logger logger = Logger.getLogger(PersonaAspect.class.getName());

    // Este método dice que antes de que se ejecute este método savePersona(), de la clase PersonaController, en el
    // paquete cr.ac.una.proyectoparadigmas.controller, con cualquier parámetro, se ejecuta este método logBeforeV2()
    // Los (..) indican que puede recibir cualquier número de parámetros
    @Before("execution(* cr.ac.una.proyectoparadigmas.controller.PersonaController.savePersona*(..))")
    public void logBeforeV2(JoinPoint joinPoint) {
        // Este logger es el que está en application.properties logging.file.name=app.log
        logger.info("Antes de ejecutar el metodo save persona : " + joinPoint.getSignature().getName());
    }

    // Este método dice que se ejecuta después de cualquier método, de la clase PersonaController.*(..)
    @After("execution(* cr.ac.una.proyectoparadigmas.controller.PersonaController.*(..))")
    public void logAfterV1(JoinPoint joinPoint) {
        // Con joinPoint.getSignature().getName() se obtiene el nombre del método que se está ejecutando
        logger.info("Después de de ejecutar cualquier método del controller : " + joinPoint.getSignature().getName());

    }

    // Este es similar al primero solo que este tiene un * PersonaController.*
    @Before("execution(* cr.ac.una.proyectoparadigmas.controller.PersonaController.*(..))")
    public void logAfterV2(JoinPoint joinPoint) {
        logger.info("Antes de  ejecutar cualquier método del controller :  " + joinPoint.getSignature().getName());

    }
}
