package cr.ac.una.proyectoparadigmas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity // Esta clase ya es una tabla con Entity
@Data // Genera los getters y setters de lombok

public class Persona {
    @Id // Llave primaria, necesario si se usa Entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto Incremental

    Long id;
    String nombre;
    String apellido;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}
