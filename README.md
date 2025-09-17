# Proyecto Paradigmas - Procesamiento Funcional de Logs

## Descripción
Este proyecto implementa una API RESTful desarrollada en Spring Boot para el procesamiento y análisis de logs.  
El enfoque principal es aplicar programación funcional en Java (Streams, Lambdas, Optional) para analizar los datos, generar reportes y demostrar la eficacia de este paradigma en la manipulación de flujos de datos.

El sistema permite obtener métricas sobre errores, tiempos de respuesta, uso de endpoints, alertas críticas y un estado general de la aplicación.

## Objetivos
- Demostrar el uso de programación funcional en la manipulación de datos.
- Implementar reportes automáticos a partir de los logs de una aplicación.
- Generar métricas útiles para la detección de problemas y la mejora del rendimiento.
- Documentar los endpoints mediante Swagger/OpenAPI.

## Tecnologías utilizadas
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- MySQL (base de datos principal)
- H2 (base de datos en memoria para pruebas rápidas)
- Postman (colecciones de pruebas de API)
- Maven (gestión de dependencias y build)

## Estructura del Proyecto
- LogEntry → Entidad principal que almacena cada registro de log.
- LogRepository → Acceso a la base de datos mediante JPA.
- LogService → Procesamiento funcional con Streams y Lambdas.
- LogController → Exposición de los endpoints REST.
- PersonaAspect → Uso de AOP para registrar automáticamente cada petición en la base de datos.

## Reportes implementados

### Reporte de Errores
- Número total de errores por tipo (404, 500, etc.).
- Top 3 errores más frecuentes.
- Horas pico de errores.

### Reporte de Tiempos de Respuesta
- Estadísticas: mínimo, máximo, promedio, mediana.
- Distribución de tiempos por endpoint.

### Reporte de Uso de Endpoints
- Endpoints más y menos utilizados.
- Cantidad de peticiones por método HTTP (GET, POST, PUT, DELETE).

### Reporte de Alertas y Eventos Críticos
- Lista de eventos críticos (status ≥ 500 o nivel CRITICAL).
- Cantidad total de eventos críticos.

### Reporte de Estado de la Aplicación
- Total de peticiones.
- Total de errores.
- Tiempo promedio de respuesta.

## Instalación y configuración

### Requisitos
- Java 17
- Maven
- MySQL

### Crear base de datos MySQL
``sql
CREATE DATABASE paradigmas;``
## Configuración en application.properties
- spring.datasource.url=jdbc:mysql://localhost:3306/paradigmas?useSSL=false&serverTimezone=UTC
- spring.datasource.username=root
- spring.datasource.password=12345678
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=true
- spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
- Ajustar username y password según la instalación.

## Ejecutar aplicación
mvn spring-boot:run

## Endpoints principales
### Errores
GET /api/logs/reportes/errores
GET /api/logs/reportes/errores/top3
GET /api/logs/reportes/errores/horas-pico

### Tiempos de respuesta
GET /api/logs/reportes/tiempos/estadisticas
GET /api/logs/reportes/tiempos/distribucion

### Uso de endpoints
GET /api/logs/reportes/uso/endpoints
GET /api/logs/reportes/uso/http

### Alertas
GET /api/logs/reportes/alertas/eventos
GET /api/logs/reportes/alertas/cantidad

### Estado
GET /api/logs/reportes/estado

## Pruebas con Postman
El repositorio incluye una colección de Postman con todos los endpoints listos para probar.
Pasos:
- Importar el archivo .json de la colección en Postman.
- Usar los endpoints para generar y consultar reportes.
- Para que aparezcan resultados en los reportes de errores, deben existir logs con statusCode >= 400.
  
## Estudiantes

- Cristina Zúñiga Cárdenas (Estudiante 1)  
  - Diseño de la API y entidades.  
  - Extensión del proyecto con la entidad LogEntry y repositorio asociado.  
  - Creación de LogController con endpoints base.  
  - Organización de paquetes (entity, repository, service, controller).  
  - Documentación técnica: diseño de la API, estructura de paquetes y endpoints iniciales.  

- Ian Villegas Jiménez (Estudiante 2)  
  - Procesamiento funcional de logs mediante Streams y Lambdas.  
  - Implementación de LogService con funciones puras.  
  - Conexión con Aspect (AOP) para registrar logs en base de datos.  
  - Documentación funcional: explicación de conceptos de programación funcional aplicados (inmutabilidad, funciones puras, lambdas, streams).  

- Marisol Hidalgo Murillo (Estudiante 3)  
  - Implementación de reportes de Errores (totales, top 3, horas pico).  
  - Implementación de reportes de Tiempos de respuesta (mínimo, máximo, promedio, mediana, distribución).  
  - Documentación de resultados: capturas de salidas JSON y explicación de métricas.  

- Bayron Camacho Ledezma (Estudiante 4)  
  - Implementación de reportes de Uso de endpoints (más/menos usados, conteo por HTTP).  
  - Implementación de reportes de Alertas y eventos críticos.  
  - Implementación de reporte de Estado general de la aplicación.  
  - Documentación de validación: evidencias de pruebas en Swagger y Postman, explicando el proceso de verificación.  

## Licencia
Proyecto desarrollado con fines académicos para el curso Paradigmas de Programación en la Universidad Nacional de Costa Rica.

# Fecha de entrega
22 de septiembre del 2025
