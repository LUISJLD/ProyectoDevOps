# Sistema de Gestión de Eventos - Proyecto DevOps

## Repositorio

🔗 https://github.com/LUISJLD/ProyectoDevOps.git

---

## Integrantes
- Juan Pablo Támara  
- Daniel Bocachica  
- Jeferson Flórez  
- Luis David Pérez  
- Javier Figueroa  

---

## Stack Tecnológico

- **Backend:** Spring Boot  
- **Seguridad:** Spring Security + JWT  
- **Persistencia:** Spring Data JPA (Hibernate)  
- **Base de Datos:** PostgreSQL  
- **Infraestructura BD:** Supabase  
- **Frontend:** React.js  
- **Contenerización:** Docker  
- **Control de Versiones:** Git (GitFlow)  

---

## Justificación del Stack

Se seleccionó este stack tecnológico por su robustez, escalabilidad y compatibilidad con el desarrollo de aplicaciones modernas.  
Spring Boot permite construir APIs seguras y estructuradas, React facilita la creación de interfaces dinámicas, y PostgreSQL garantiza una gestión eficiente de datos.  
Además, el equipo cuenta con conocimientos previos en estas tecnologías, lo que permite un desarrollo más ágil y mantenible.

---

## Estructura del Proyecto

- `backend/`: API desarrollada con Spring Boot  
- `frontend/`: Aplicación cliente en React  
- `docs/`: Documentación del sistema  

---

## Backlog Inicial (PBIs - Módulo de Usuarios)

1. **PBI 1:** Configuración inicial del proyecto Spring Boot y conexión con la base de datos.  
2. **PBI 2:** Creación del modelo de datos para la entidad `User` y su persistencia.  
3. **PBI 3:** Registro de usuarios con cifrado de contraseñas.  
4. **PBI 4:** Inicio de sesión con autenticación basada en roles.  
5. **PBI 5:** Validación de correo único.  
6. **PBI 6:** Activación e inactivación de usuarios.  

---

## Backlog Módulo de Eventos

1. **US08:** Creación de la entidad Evento con persistencia en base de datos.  
2. **US09:** Registro de eventos con nombre, descripción, fecha, hora y ubicación.  
3. **US10:** Edición de eventos existentes.  
4. **US11:** Listado de eventos con filtros y paginación.  
5. **US12:** Consulta de detalle de un evento.  
6. **US13:** Definición de capacidad máxima de asistentes.  
7. **US14:** Configuración de parqueadero y cupos disponibles.  
8. **US15:** Gestión de estados del evento (DRAFT, PUBLISHED, CLOSED, CANCELLED).  

---

## Definition of Done (DoD)

- El código compila y ejecuta sin errores.  
- Cada funcionalidad se desarrolla en una rama `feature/*` antes de integrarse a `develop`.  
- El código no rompe la estabilidad de la rama `develop`.  
- Uso de variables de entorno para datos sensibles.  
- Las APIs son probadas mediante herramientas como Postman o Swagger.  
- Los commits son descriptivos (`feat:`, `fix:`, `chore:`).  
- La funcionalidad cumple con los criterios definidos en el PBI.  

---

## Ejecución del Proyecto
1. Clonar el repositorio:
  ```bash
  git clone  https://github.com/LUISJLD/ProyectoDevOps.git
  ```
### Backend (Spring Boot)
 ```bash
  cd backend
  ./mvnw spring-boot:run
 ```
### Frontend (React)
 ```bash
  cd frontend
  npm install
  npm start
 ```
### Ejecución con Docker (Opcional)
 ```
docker-compose up --build
 ```

---

## Documentación

La documentación detallada del sistema (requerimientos funcionales, no funcionales y backlog) se encuentra en:

📄 [docs/requerimientos.md](docs/documentacion.md)
