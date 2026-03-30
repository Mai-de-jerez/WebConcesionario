# 🚗 WebConcesionario Pro - Arquitectura Vanilla Full Stack

Sistema de gestión integral de vehículos desarrollado con **Java 21** y **Jakarta EE**, enfocado en el alto rendimiento y el control total del código sin frameworks CSS/JS.

## 🚀 Funcionalidades Avanzadas
* **Arquitectura SPA (Single Page Application):** Navegación fluida mediante **JavaScript Vanilla** y **Fetch API**.
* **Motor de Búsqueda y Paginación:** Filtrado dinámico de vehículos y gestión de grandes volúmenes de datos en cliente y servidor.
* **Sistema de Reservas Transaccional:** * Proceso atómico: Creación de pedido (Pendiente) + Reserva de vehículo + Actualización de estado del Coche a "Reservado".
* **Gestión de Perfil:** Área privada con subida y previsualización de imágenes de usuario.
* **Seguridad Robusta:** * Autenticación mediante **JWT (JSON Web Tokens)**.
  * Encriptación de contraseñas con **BCrypt**.
  * **Jakarta Filters:** Control de acceso y protección de rutas en el lado del servidor.

## 🛠️ Stack Tecnológico (Zero Frameworks Policy)
* **Backend:** Java 21, Maven, **JPA / Hibernate 6**.
* **Frontend:** **HTML5, CSS3 Puro** (Sin Bootstrap/Tailwind), **JS Puro** (Sin React/Vue).
* **Base de Datos:** MySQL 9 optimizada con **HikariCP** (Connection Pooling de alto rendimiento).
* **Servidor:** Apache Tomcat 11.
* **Intercambio de Datos:** Formato **JSON** nativo.

## 🚀 Características
* **Gestión de Vehículos/Usuarios/Reservas/Pedidos/Consultas:** Alta, baja, modificación y consulta con imágenes.
* **Seguridad:** Autenticación con **JWT** y encriptación de claves con **BCrypt**.
* **Rendimiento:** Pool de conexiones optimizado con **HikariCP**.
* **Arquitectura:** Single Page Application (SPA) usando Vanilla JavaScript y Fetch API.
## 🏗️ Detalles de Implementación
* **Persistencia:** Uso de `EntityManager` y gestión manual de transacciones para garantizar la integridad de los datos en las reservas.
* **Diseño:** Layouts creados desde cero con **CSS Flexbox y Grid** para un control total de la interfaz.
* **Modularidad:** Separación estricta de responsabilidades (DAO, Controllers, Services, Filters).

## ⚙️ Configuración Necesaria
El sistema requiere las siguientes variables de entorno para la conexión a la DB:
* `DB_USER`: Usuario de MySQL.
* `DB_PASSWORD`: Password de MySQL.

## 🧙‍♂️ Nota del Autor
"Con unos cuantos prompts y una visión clara, el código senior emerge. Aquí no hay magia, hay arquitectura."