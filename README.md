
# 🐇 Granja La Favorita

Granja La Favorita is a web-based management system designed to handle rabbit breeding operations, sales, and inventory control.

The project follows a monolithic architecture enhanced with supporting microservices, providing modularity, scalability, and clear separation of responsibilities.
## 🧩 Modules

The monolithic application includes the following modules:
 
 - Rabbits (Conejos)
 - Breeding (Montas)
 - Births (Nacimientos)
 - Sales (Ventas)
 - Breeds (Razas)
 - Availability
   - Specimens (Ejemplares)
   - Articles (Artículos)

Each module implements full CRUD operations, with support for pagination and filtering.
## 🔗 Microservices Architecture

In addition to the main monolithic service (conejoMontaNacimiento), the system consumes two independent microservices:

 - microservice-razas
 - microservice-articulos

These services are integrated using Spring Cloud and are consumed by the main application.
## 🖥️ Frontend

 - Server-side rendering using Thymeleaf
 - Client-side data consumption using JavaScript Fetch API
 - Combination of MVC views and REST API consumption
## ⚙️ Backend & Infrastructure

 - Java
 - Spring Boot
 - Spring Cloud
 - REST APIs
 - MySQL as the database
 - Cloudinary for image storage
 - GitHub for source code management
## 🌐 Cloud & Services

The project includes the following infrastructure components:

 - Config Data Service (centralized configuration)
 - Eureka Server (service discovery)
 - Gateway Server (routing and security)
 - Nginx (domain-based access without exposing ports)
## 🐳 Containerization

 - Each service runs inside its own Docker container
 - Docker Compose is used for service orchestration and deployment
## 📊 Monitoring

 - Grafana is used to visualize metrics through dashboards
 - Grafana also consumes selected application endpoints for monitoring purposes
## 🏢 Project Status

This system is actively used in production and supports real operational workflows.
