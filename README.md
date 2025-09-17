# Proyecto Paradigmas – Módulo de Logs

Este proyecto forma parte del curso **Paradigmas de Programación**.  
El objetivo es implementar un sistema de manejo de logs con procesamiento funcional en Java usando Spring Boot.

---

## Equipo
- **Persona 1 – Cristina**: Diseño de la API y Entidades + Documentación técnica.
- **Persona 2 – ...**: Procesamiento funcional de logs.
- **Persona 3 – ...**
- **Persona 4 – ...**

---

## Estructura del Proyecto
- `cr.ac.una.proyectoparadigmas.entity` → Entidades JPA (`Persona`, `LogEntry`).
- `cr.ac.una.proyectoparadigmas.repository` → Repositorios JPA (`LogRepository`, `PersonaRepository`).
- `cr.ac.una.proyectoparadigmas.service` → Servicios base (`LogService`).
- `cr.ac.una.proyectoparadigmas.controller` → Controladores REST (`PersonaController`, `LogController`).
- `cr.ac.una.proyectoparadigmas.aspect` → Aspectos AOP.

---

##  Entidades Implementadas
### Persona
- **Atributos**: nombre, apellido.  
- **Endpoints CRUD**:
  - `GET /api/persona`
  - `POST /api/persona`
  - `PUT /api/persona/{id}`
  - `DELETE /api/persona/{id}`

### LogEntry
- **Atributos**: id, timestamp, nivel, mensaje, endpoint, metodoHttp, statusCode, tiempoRespuesta.  
- **Repositorio**: `LogRepository`  

---

## Endpoints Iniciales de Logs
- `GET /api/logs/ping` → Verifica estado del módulo.  
- `GET /api/logs/reportes/errores` → Placeholder (devuelve {}).  
- `GET /api/logs/reportes/tiempos/estadisticas` → Placeholder (devuelve ceros).  
- `GET /api/logs/reportes/uso/http` → Placeholder (devuelve conteo 0).  

---

## Ejecución
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/<org>/<repo>.git
