package cr.ac.una.proyectoparadigmas.controller;

import cr.ac.una.proyectoparadigmas.entity.Persona;
import cr.ac.una.proyectoparadigmas.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController// Indica que es un servicio web
@RequestMapping("/api")

public class PersonaController {
    @Autowired // Carga una variable de PersonaRepository que ya no es una interfaz sino una clase gracias a Autowired
    PersonaRepository personaRepository;

    @GetMapping("/persona") // Mapea la URL /api/persona, es decir, responde a esa URL
    public ResponseEntity<List<Persona>> findAll() {

        return ResponseEntity.ok(personaRepository.findAll()); // Retorna un 200 OK con la lista de personas en el cuerpo
    }

    @PostMapping("/persona")
    ResponseEntity<Persona> savePersona(@RequestBody Persona Persona){
        return ResponseEntity.ok(personaRepository.save(Persona));
    }


}
