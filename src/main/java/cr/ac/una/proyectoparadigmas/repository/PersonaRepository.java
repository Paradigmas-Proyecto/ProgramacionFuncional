package cr.ac.una.proyectoparadigmas.repository;
// Interfaz es como un "machote" o plantilla de una clase que "estandariza" los metodos que debe tener una clase
import cr.ac.una.proyectoparadigmas.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
// Según la documentación de Spring Data JPA, al extender JpaRepository, esta interfaz hereda varios métodos para trabajar
// con la entidad Persona, como guardar, eliminar y encontrar personas. No es necesario escribir ninguna implementación
// para estos métodos, ya que Spring Data JPA los proporciona automáticamente en tiempo de ejecución.
}
