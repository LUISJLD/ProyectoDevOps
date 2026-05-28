# Sistema de Gestión de Eventos - Proyecto DevOps

## Repositorio
🔗 https://github.com/LUISJLD/ProyectoDevOps.git

## Integrantes
- Juan Pablo Támara
- Daniel Bocachica Castillo
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
1. **PBI 1:** Configuración inicial del proyecto Spring Boot y establecimiento de la conexión con la base de datos.  
2. **PBI 2:** Creación del modelo de datos para la entidad `User` y su respectiva persistencia.  
3. **PBI 3:** Implementación del registro de usuarios con almacenamiento seguro de contraseñas (cifrado).  
4. **PBI 4:** Implementación del inicio de sesión mediante autenticación basada en roles.  
5. **PBI 5:** Validación de unicidad del correo electrónico y verificación de campos obligatorios.  
6. **PBI 6:** Implementación de la funcionalidad de activación e inactivación de cuentas de usuario.  

Los PBIs fueron desglosados en historias de usuario (US) y tareas técnicas (TK), las cuales fueron gestionadas como Issues dentro de GitHub.  
Los PBIs detallados se gestionan en GitHub Projects/Issues.

---

## Módulo de Inscripciones y Control de Cupos

Este módulo permite la interacción directa entre usuarios y eventos, gestionando inscripciones y controlando la disponibilidad de cupos en tiempo real.

### Funcionalidades
- **Inscripción a eventos:** un usuario puede inscribirse a un evento disponible.
- **Control de cupos:** el sistema valida que el evento no supere su capacidad máxima antes de aceptar una inscripción.
- **Prevención de doble inscripción:** un mismo usuario no puede inscribirse dos veces al mismo evento.
- **Restricciones de estado:** no se permiten inscripciones en eventos cerrados, cancelados o con cupos agotados.
- **Consulta de inscritos:** vista del listado de usuarios inscritos por evento.
- **Cancelación de inscripción:** un usuario puede cancelar su participación en un evento.

### PBIs del Módulo

| # | Descripción |
|---|---|
| PBI 1 | Creación del modelo de datos para la entidad `Inscripcion` y su relación con `User` y `Evento`. |
| PBI 2 | Registro de inscripción con validación de cupos disponibles. |
| PBI 3 | Prevención de doble inscripción para un mismo usuario en el mismo evento. |
| PBI 4 | Restricción de inscripciones en eventos con estado no habilitado. |
| PBI 5 | Consulta de inscritos por evento y eventos por usuario. |
| PBI 6 | Cancelación de inscripción. |

---

## Módulo de Notificaciones por Correo y Check-in con QR

Este módulo extiende el sistema con notificaciones transaccionales y validación de asistencia mediante códigos QR.

### Funcionalidades
- **Confirmación de inscripción:** al inscribirse, el usuario recibe un correo con los datos del evento y su código QR embebido.
- **Recordatorio automático:** 24 horas antes del evento se envía un recordatorio (simulable con endpoint manual).
- **Notificación de cambios:** si el organizador modifica fecha, hora o lugar, todos los inscritos reciben un aviso.
- **Check-in con QR:** el organizador escanea el QR desde la cámara del navegador; el sistema valida la inscripción y registra la asistencia.
- **Confirmación de check-in:** al validarse el QR, el asistente recibe un correo confirmando su presencia.
- **Reporte de asistencia:** vista de inscritos vs. asistentes confirmados por evento.

### Decisiones Técnicas

| Componente | Decisión | Justificación |
|---|---|---|
| Correo (dev) | Mailtrap (sandbox) | Captura correos sin enviarlos a cuentas reales; ideal para CI |
| Correo (prod) | Brevo SMTP | 300 correos/día gratis, SMTP estándar, sin tarjeta de crédito |
| Generación QR | api.qrserver.com | Sin API key, sin dependencia Maven, devuelve PNG directo |
| Envío asíncrono | `@Async` + `ThreadPoolTaskExecutor` | Desacopla el envío del flujo HTTP; no bloquea la respuesta al usuario |
| Credenciales | Variables de entorno (`application.yml` + `.env`) | Nunca hardcodeadas; alternancia dev/prod solo cambiando el entorno |

### Variables de Entorno Requeridas
Ver `.env.example` en la raíz del repositorio. Las variables principales son:
```env
# Base de datos
DB_NAME=
DB_USER=
DB_PASSWORD=
SPRING_DATASOURCE_URL=
SPRING_PORT=
JWT_SECRET=
JWT_EXPIRATION=

# SMTP — Mailtrap (dev) / Brevo (prod)
MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_FROM=
MAIL_SANDBOX=

# QR
QR_API_URL=
QR_SIZE=
QR_API_KEY=

# Cola asíncrona
QUEUE_DRIVER=
QUEUE_RETRY_ATTEMPTS=
```

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
git clone https://github.com/LUISJLD/ProyectoDevOps.git
```

2. Copiar el archivo de variables de entorno y completar los valores:
```bash
cp .env.example .env
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
```bash
docker-compose up --build
```

---

## Documentación
La documentación detallada del sistema (requerimientos funcionales, no funcionales y backlog) se encuentra en:  
📄 [docs/requerimientos.md](docs/documentacion.md)  
📄 [docs/decisiones_tecnicas.pdf](docs/decisiones_tecnicas.pdf)
📄 [docs/Documento_Tecnico_Pipeline_CD.pdf](docs/Documento_Tecnico_Pipeline_CD.pdf)
