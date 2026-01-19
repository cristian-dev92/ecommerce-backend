# 🧠 Notas del Backend (Spring Boot) 

Documentación del progreso, decisiones técnicas y problemas resueltos durante el desarrollo del backend. 

## 🟩 PASO 1 — Creación del proyecto

- Creación del proyecto con **Spring Boot**.

- Añadidas las librerías necesarias (Spring Web, Spring Security, JPA, PostgreSQL Driver, etc.). 
 
## 🟩 PASO 2 — Estructura inicial del backend

### AUTENTICACIÓN

- AuthService

### DTOs

- LoginDto
  
- RegisterDto
 
### Controladores 

- ProductController
 
### Configuración 

- CorsConfig

- SecurityConfig

- DataLoader

### Modelos 

- User

- Product
  
### Repositorios 

- UserRepository

- ProductRepository 

## 🟩 PASO 3 — Registro de usuarios y corrección de seguridad

Intentamos registrar usuarios desde Angular, pero la configuración de seguridad estaba incorrecta.

Se actualizó **SecurityConfig** para permitir las rutas necesarias: 

java authorizeHttpRequests(auth -> auth .requestMatchers("/", "error", "api/auth/**", "api/users/**", "api/products/**", "api/swagger-ui/**", "api/v3/api-docs/**") );

Esto permitió que Angular pudiera comunicarse correctamente con el backend.

## 🟩 PASO 4 — Nuevos componentes del backend

Necesitamos registrar usuarios en la página web.

- Para DTO: UserDto

- Para Controller: UserController

## 🟩 PASO 5 — Pruebas con Postman

Antes de conectar Angular, se instaló Postman para: Probar endpoints. Validar respuestas. 

Detectar errores en autenticación y productos.

## 🟩 PASO 6 — Registro de usuarios funcionando

Se elimio CorsConfig y se hizo una nueva SecurityConfig para que quedara todo más compacto y funcional. 

Se actualizó AuthController.

Resultado: ✔ El registro de usuarios funciona correctamente.

## 🟩 PASO 7 — Control de versiones

Creación del repositorio en GitHub. 

Ya estaba doumentando el proyecto aparte pero quiero aprender todo lo relacionado con GitHub que es donde se alojan proyectos de software usando Git, un sistema de control de versiones. 
