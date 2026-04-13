# Sistema de Gestión de Eventos - Proyecto DevOps

## Repositorio

🔗 https://github.com/LUISJLD/ProyectoDevOps.git

## Integrantes
- Juan Pablo Tamara
- Daniel Bocachica
- Jeferson Flórez
- Luis David Pérez

---

## Stack Tecnológico

- **Backend:** Java con Spring Boot (Maven)
- **Seguridad:** Spring Security + JWT
- **Persistencia:** Spring Data JPA (Hibernate)
- **Frontend:** React.js
- **Base de Datos:** PostgreSQL
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

1. **PBI 1:** Configuración inicial del proyecto Spring Boot y establecimiento de la conexión con la base de datos.  
2. **PBI 2:** Creación del modelo de datos para la entidad `User` y su respectiva persistencia.  
3. **PBI 3:** Implementación del registro de usuarios con almacenamiento seguro de contraseñas (cifrado).  
4. **PBI 4:** Implementación del inicio de sesión mediante autenticación basada en roles.  
5. **PBI 5:** Validación de unicidad del correo electrónico y verificación de campos obligatorios.  
6. **PBI 6:** Implementación de la funcionalidad de activación e inactivación de cuentas de usuario.  

Los PBIs detallados se gestionan en GitHub Projects/Issues.

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
git clone URL_DEL_REPOSITORIO

### Backend (Spring Boot)
cd backend
./mvnw spring-boot:run

### Frontend (React)
cd frontend
npm install
npm start

---

## Documentación

La documentación detallada del sistema (requerimientos funcionales, no funcionales y backlog) se encuentra en:

📄 `docs/requerimientos.md`
