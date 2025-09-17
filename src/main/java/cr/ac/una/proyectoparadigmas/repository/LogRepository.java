package cr.ac.una.proyectoparadigmas.repository;

import cr.ac.una.proyectoparadigmas.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a datos para la entidad LogEntry.
 */
public interface LogRepository  extends JpaRepository<LogEntry, Long> {
}
