# 🛒 Full-Stack Ecommerce

API REST para una aplicación ecommerce full‑stack. 

Incluye autenticación con JWT, gestión de usuarios, catálogo de productos, carrito de compras y sistema de pedidos.

Desarrollado con Spring Boot y PostgreSQL siguiendo buenas prácticas REST.

## 🌐 Demo pública

Backend desplegado en Render (puede tardar 20–60 segundos en la primera carga por cold start).

Frontend desplegado en Vercel.

🔗 Demo: https://ecommerce-frontend-seven-psi.vercel.app/

## ✨ Características Principales

    🔐 Autenticación Segura: Sistema de login y registro gestionado con Spring Security.

    📦 Gestión de Inventario: CRUD completo de productos con persistencia de datos.

    🛒 Carrito de Compras: Flujo dinámico de selección y gestión de artículos.

    👤 Perfil de Usuario: Espacio personalizado para la gestión de datos del cliente.

    📜 Historial de Pedidos: Registro detallado de transacciones pasadas.

    🎨 UI/UX Moderna: Interfaz estilizada con SCSS, incluyendo visualización de imágenes.

## 🛠️ Stack Tecnológico

Backend

    Core: Java 21 & Spring Boot 3.4.1

    Procesamiento por lotes: Spring Batch & @Scheduled (Cron Jobs nocturnos)

    Seguridad: Spring Security

    Persistencia: Spring Data JPA + Hibernate

    Gestión de Dependencias: Maven

    Base de Datos: PostgreSQL hospedado en Neon.tech (Persistencia real).

Frontend

    Framework: Angular 21

    Lenguaje: TypeScript

    Estilos: SCSS / HTML5

## 📂 Estructura del proyecto

/ecommerce

/backend/src/main/java/com/tienda/ecommerce/

├—— auth # Lógica específica de login y registro.

├—— config # Configuraciones generales (CORS, Beans, etc.).

├—— controller # Endpoints de la API REST que reciben las peticiones.

├——  dto # Objetos de transferencia de datos (evitan exponer entidades directamente).

├—— model # Entidades JPA que representan las tablas en PostgreSQL.

├—— repository # Interfaces que se comunican con la base de datos (Spring Data JPA).

├—— security # Configuración de Spring Security y gestión de JWT/Roles.

└—— service # Lógica de negocio (donde ocurre la "magia" antes de guardar datos).

/resources

## ⚙️ Configuración del backend (Spring Boot)

### 1. Configurar la base de datos  

En `src/main/resources/application.properties`:

Properties

    spring.datasource.url=jdbc:postgresql://localhost:5432/tienda_db
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    spring.jpa.hibernate.ddl-auto=update

### 2. Gestión de Imágenes (Cloudinary)

El sistema permite la subida de archivos de hasta 10MB. 

Debes configurar tus credenciales de Cloudinary como variables de entorno o en el archivo de propiedades:

Properties

    cloudinary.api-key=${CLOUDINARY_API_KEY}
    cloudinary.api-secret=${CLOUDINARY_API_SECRET}
    cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}

### 3. Ejecutar el backend  

    mvn spring-boot:run

El backend quedará disponible en: http://localhost:8080

## 🔐 Endpoints principales


| Categoría | Método | Endpoint | Descripción |
|:---:|:---:|:---:|:---:|
| Auth | POST | /api/auth/login | Obtener token de acceso |
| Auth | POST | /api/auth/register | Registro de usuario |
| Productos | GET | /api/products | Listar productos |
| Productos | POST | /api/products | Crear (Requiere Cloudinary) |
| Usuarios | GET | /api/users | Listar usuarios (Admin) |
| Pedidos | GET | /api/orders/{id}/pdf | Descargar factura (PDF generado en caliente) |

## 🚀 Despliegue

Este backend está optimizado para ser desplegado en:

    Render: Ideal para servicios de Spring Boot con PostgreSQL.
    Cloudinary: Almacena de forma externa las imágenes subidas desde el CRUD de productos.
    Vercel: Recomendado para conectar el frontend que consumirá esta API.

Nota: Al desplegar en producción, asegúrate de cambiar spring.jpa.hibernate.ddl-auto a validate o none para proteger la integridad de los datos.

## 🗄️ Persistencia de Datos

El proyecto utiliza Neon como proveedor de base de datos PostgreSQL serverless. 
Esto permite que los productos y usuarios creados se mantengan almacenados de forma permanente, permitiendo una gestión de inventario real y segura.

## 🧪 Estado actual del proyecto - REDISEÑO

  El proyecto nació como un MVP (Producto Mínimo Viable) funcional en su primera version y actualmente se encuentra en su tercera version de rediseño arquitectónico y robustez empresarial.

  VERSION 1: MVP funcional con persistencia en memoria (H2) y sin gestión de imágenes.

  [x] Backend inicial configurado

  [x] Configuración de Spring Security finalizada

  [x] CRUD de productos

  [x] Carrito de compras con CRUD

  [x] Pestaña Perfil e Historial de pedidos.

  VERSION 2: Rediseño con persistencia real en PostgreSQL y mejoras de seguridad.

  [X] Gestión de imágenes con Cloudinary implementada.

  [X] Interfaz de usuario mejorada con estilos CSS.

  [X] Migración a PostgreSQL desde Render a Neon.tech.

  VERSION 3: Mejoras de Arquitectura e Infraestructura.

  [X] Refactorización del código y mejoras de rendimiento.

  [X] Migración Financiera: Transición total de tipos de datos flotantes a `BigDecimal` para garantizar precisión milimétrica en los cálculos.

  [X] Módulo de Facturación Automatizada: Integración del motor Flying Saucer para la generación e inyección dinámica de PDFs de facturas en base a las compras del usuario.

  [X] Integridad de Ventas: Uso de transacciones de base de datos para asegurar que, si algo falla durante la compra, el stock y los pedidos siempre se mantengan sincronizados y sin errores.

  [X] Módulo de Contabilidad en Segundo Plano (Spring Batch): Configuración de tareas programadas (`@Scheduled`) de trastienda para el control automático de inventario, auditoría de stock crítico y reportes automáticos.

  [X] Seguridad de Credenciales: Migración de credenciales de desarrollo hacia perfiles locales protegidos (`application-dev.properties`) inyectados por variables de entorno.

  [X] Escalabilidad.

### 📄 Licencia

Proyecto personal de aprendizaje. Uso libre para estudio y desarrollo.
