# 🛒 Ecommerce App 

Aplicación web full‑stack para la gestión de productos, usuarios y autenticación.

Desarrollada con **Angular** en el frontend y **Spring Boot** en el backend, utilizando **PostgreSQL** como base de datos. 
 
## 🚀 Tecnologías utilizadas

### Frontend - Angular 21 - TypeScript - HTML / SCSS 
### Backend - Spring Boot 3.4.1 - Spring Web - Spring Security - JPA / Hibernate - Maven 
### Base de datos - PostgreSQL - pgAdmin 
### Herramientas de desarrollo - IntelliJ IDEA (backend) - VS Code (frontend) - Git + GitHub

## 📦 Estructura del proyecto

/ecommerce

├── backend/ (Spring Boot)

│    ├── src/main/java/com/tienda/ecommerce

│    ├── src/main/resources

│    └── pom.xml

└── frontend/ (Angular)

├── src/

├── angular.json

└── package.json

## ⚙️ Configuración del backend (Spring Boot)

### 1. Requisitos
- Java 21
- Maven
- PostgreSQL en ejecución

### 2. Configurar la base de datos  
En `src/main/resources/application.properties`:

spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce

spring.datasource.username=postgres

spring.datasource.password=postgres92

spring.jpa.hibernate.ddl-auto=update

### 3. Ejecutar el backend  
Desde IntelliJ:
- Abrir `EcommerceApplication.java`
- Ejecutar con el botón verde

El backend quedará disponible en: http://localhost:8080

## 🖥️ Configuración del frontend (Angular)

### 1. Instalar dependencias

- npm install

### 2. Ejecutar Angular: 

- ng serve
 
El frontend quedará disponible en: http://localhost:4200

### 🔐 Endpoints principales

- Autenticación

POST /api/auth/login

POST /api/auth/register

- Usuarios

GET /api/users

GET /api/users/{id}

- Productos

GET /api/products

POST /api/products

PUT /api/products/{id}

DELETE /api/products/{id}

### 🧪 Estado actual del proyecto - FINALIZADO

  [x] Backend inicial configurado

  [x] Seguridad básica con Spring Security

  [x] CRUD de productos

  [x] Carrito de compras con CRUD

  [x] Pestaña Perfil de usuario
 
  [X] Mejorar la interfaz con fotos y un estilo de página sencillo

  [X] Terminar con un historial de pedidos

📄 Licencia

Proyecto personal de aprendizaje. Uso libre para estudio.
