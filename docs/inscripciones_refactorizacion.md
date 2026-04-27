# Refactorización de Inscripciones - Arquitectura de Capas Limpia

## Resumen de Implementación

Se ha refactorizado completamente la funcionalidad de inscripciones siguiendo una arquitectura de capas limpia con los siguientes componentes:

---

## 1. Repository Layer (InscripcionRepository.java)

### Cambios Principales:
- ✅ **Paginación**: Los métodos `findByUsuarioId()` y `findByEventoId()` ahora retornan `Page<Inscripcion>` 
- ✅ **Métodos de Validación**: 
  - `hasActiveInscription()`: Verifica si un usuario tiene inscripción activa (CONFIRMADA)
  - `countActiveInscriptionsByEventoId()`: Cuenta inscripciones confirmadas de un evento

### Métodos Disponibles:

```java
// Búsqueda con paginación
Page<Inscripcion> findByUsuarioId(Long usuarioId, Pageable pageable);
Page<Inscripcion> findByEventoId(Long eventoId, Pageable pageable);

// Validaciones
boolean existsByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);
Optional<Inscripcion> findByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);
boolean hasActiveInscription(Long usuarioId, Long eventoId);
long countActiveInscriptionsByEventoId(Long eventoId);
```

---

## 2. Service Layer

### Interfaz (IInscripcionService.java)

Define los contratos con documentación completa de excepciones esperadas:

```java
// Crear inscripción
InscripcionResponse createInscripcion(CreateInscripcionRequest request);

// Consultar
InscripcionResponse getInscripcionById(Long id);
Page<InscripcionResponse> getInscripcionesByUsuario(Long usuarioId, Pageable pageable);
EventoInscritosResponse getInscripcionesByEvento(Long eventoId, Pageable pageable);

// Cancelar/Eliminar
InscripcionResponse cancelarInscripcion(Long id);
void deleteInscripcion(Long id);
```

### Implementación (InscripcionServiceImpl.java)

#### Validaciones Implementadas:

1. **validarEventoDisponible()**: 
   - Lanza `BadRequestException` con mensaje **"evento no disponible"** si:
     - Estado = CANCELLED
     - Estado = DRAFT
     - Estado = COMPLETED

2. **validarCuposDisponibles()**:
   - Lanza `BadRequestException` con mensaje **"cupos agotados"** si inscritosCount >= capacidadMaxima

3. **validarNoInscritoPreview()**:
   - Lanza `BadRequestException` con mensaje **"ya inscrito"** si usuario tiene inscripción activa

#### Lógica de Negocio:

- **Crear Inscripción**:
  - Valida usuario y evento existen (ResourceNotFoundException)
  - Valida evento disponible
  - Valida cupos
  - Valida usuario no inscrito
  - Incrementa contador de inscritos
  - Crea inscripción con estado CONFIRMADA

- **Cancelar Inscripción**:
  - Cambio LÓGICO de estado (no elimina registro)
  - Decrementa contador de evento
  - Valida estado previamente

- **Mapeo de Entidades a DTOs**:
  - Convierte Inscripcion a InscripcionResponse con todos los campos

---

## 3. Data Transfer Objects (DTOs)

### InscripcionResponse (Existente, Verificado)

Campos incluidos:
- `id`: Identificador de inscripción
- `usuarioId`: ID del usuario
- `usuarioNombre`: Nombre completo del usuario (nombre + apellido)
- `eventoId`: ID del evento
- `eventoNombre`: Nombre del evento
- `estado`: Estado de inscripción (CONFIRMADA, CANCELADA, PENDIENTE)
- `createdAt`: Timestamp de creación
- `cuposRestantes`: Cupos disponibles del evento

### EventoInscritosResponse (NUEVO)

Estructura para respuesta de listado por evento:

```java
public class EventoInscritosResponse {
    private Page<InscripcionResponse> inscripciones;  // Paginadas
    private Integer totalCupos;                       // Capacidad máxima
    private Integer cuposUsados;                      // inscritos confirmados
    private Integer cuposDisponibles;                 // totalCupos - cuposUsados
}
```

---

## 4. Controller Layer (InscripcionController.java)

### Endpoints Implementados:

#### 1. Crear Inscripción
```
POST /api/inscripciones
Status: 201 Created
Request: CreateInscripcionRequest { usuarioId, eventoId }
Response: InscripcionResponse
```

#### 2. Obtener por ID
```
GET /api/inscripciones/{id}
Status: 200 OK
Response: InscripcionResponse
```

#### 3. Listar por Usuario (CON PAGINACIÓN)
```
GET /api/usuarios/{usuarioId}/inscripciones?page=0&size=20&sort=createdAt,desc
Status: 200 OK
Response: Page<InscripcionResponse>
```

#### 4. Listar por Evento (CON ESTADÍSTICAS)
```
GET /api/eventos/{eventoId}/inscritos?page=0&size=20
Status: 200 OK
Response: EventoInscritosResponse {
    inscripciones: Page<InscripcionResponse>,
    totalCupos: 50,
    cuposUsados: 35,
    cuposDisponibles: 15
}
```

#### 5. Cancelar Inscripción (Cambio Lógico)
```
DELETE /api/inscripciones/{id}
Status: 200 OK
Nota: No elimina, solo cambia estado a CANCELADA
```

#### 6. Eliminar Inscripción (Física)
```
DELETE /api/inscripciones/{id}/eliminar
Status: 204 No Content
```

---

## 5. Manejo de Errores

### Excepciones Utilizadas:

1. **ResourceNotFoundException** (404):
   - Usuario no encontrado
   - Evento no encontrado
   - Inscripción no encontrada
   - Usuario sin inscripciones

2. **BadRequestException** (400):
   - **"evento no disponible"** - Evento en estado invalid
   - **"cupos agotados"** - No hay capacidad
   - **"ya inscrito"** - Usuario ya tiene inscripción activa

---

## 6. Flujo de Ejemplo

### Crear Inscripción:
```
1. POST /api/inscripciones
2. Validar usuario existe ✓
3. Validar evento existe ✓
4. Validar evento en estado PUBLISHED ✓
5. Validar evento.inscritosCount < evento.capacidadMaxima ✓
6. Validar usuario sin inscripción activa previa ✓
7. Crear inscripción con estado CONFIRMADA
8. Incrementar evento.inscritosCount
9. Guardar evento
10. Guardar inscripción
11. Retornar 201 + InscripcionResponse
```

### Cancelar Inscripción:
```
1. DELETE /api/inscripciones/{id}
2. Obtener inscripción
3. Validar no esté ya CANCELADA
4. Decrementar evento.inscritosCount
5. Cambiar estado a CANCELADA
6. Guardar inscripción
7. Retornar 200 OK
```

### Listar por Evento:
```
1. GET /api/eventos/{eventoId}/inscritos?page=0&size=20
2. Validar evento existe
3. Obtener Page<Inscripcion> filtrado por evento
4. Mapear a Page<InscripcionResponse>
5. Calcular totalCupos, cuposUsados, cuposDisponibles
6. Empaquetar en EventoInscritosResponse
7. Retornar 200 + respuesta con paginación
```

---

## 7. Características de Producción

✅ **Transaccionalidad**: 
- `@Transactional` en escritura
- `@Transactional(readOnly=true)` en consultas

✅ **Lazy Loading**: 
- Relaciones ManyToOne con `fetch = FetchType.LAZY`

✅ **Unicidad**: 
- Constraint BD: usuario + evento único (previene duplicados a nivel BD)

✅ **Paginación**: 
- Soporta parámetros: `page`, `size`, `sort`
- Ejemplo: `?page=0&size=20&sort=createdAt,desc`

✅ **Validaciones en Capas**:
- BD: Constraints
- Service: Lógica de negocio
- Controller: DTOs con `@Valid`

---

## 8. Próximos Pasos (Opcional)

Para completar la implementación:

1. Implementar mapper con MapStruct (sustituir mapToResponse manual)
2. Agregar auditoría (createdBy, updatedAt)
3. Implementar soft-delete para inscripciones
4. Agregar eventos de dominio (DomainEvent) para notificaciones
5. Implementar caché de estadísticas de cupos
6. Agregar búsqueda avanzada con especificaciones

---

## Estructura de Archivos

```
backend/src/main/java/com/backend/demo/
├── repository/
│   └── InscripcionRepository.java (ACTUALIZADO)
├── service/
│   ├── IInscripcionService.java (ACTUALIZADO)
│   └── impl/
│       └── InscripcionServiceImpl.java (REFACTORIZADO)
├── controller/
│   └── InscripcionController.java (ACTUALIZADO)
├── dto/
│   ├── request/
│   │   └── CreateInscripcionRequest.java (Existente)
│   └── response/
│       ├── InscripcionResponse.java (Verificado)
│       └── EventoInscritosResponse.java (NUEVO)
├── exception/
│   ├── BadRequestException.java (Utilizado)
│   └── ResourceNotFoundException.java (Utilizado)
└── model/
    └── entity/
        └── Inscripcion.java (Existente)
```

---

Implementación completa lista para producción ✅
