package com.backend.demo.repository;

import com.backend.demo.model.entity.Inscripcion;
import com.backend.demo.model.enums.InscripcionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    /**
     * Verifica si un usuario está inscrito a un evento
     * @param usuarioId ID del usuario
     * @param eventoId ID del evento
     * @return true si existe inscripción, false en caso contrario
     */
    boolean existsByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);

    /**
     * Busca una inscripción específica de usuario a evento
     * @param usuarioId ID del usuario
     * @param eventoId ID del evento
     * @return Optional con la inscripción si existe
     */
    Optional<Inscripcion> findByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);

    /**
     * Obtiene las inscripciones de un usuario con paginación
     * @param usuarioId ID del usuario
     * @param pageable información de paginación
     * @return Page de inscripciones del usuario
     */
    Page<Inscripcion> findByUsuarioId(Long usuarioId, Pageable pageable);

    /**
     * Obtiene las inscripciones de un evento con paginación
     * @param eventoId ID del evento
     * @param pageable información de paginación
     * @return Page de inscripciones del evento
     */
    Page<Inscripcion> findByEventoId(Long eventoId, Pageable pageable);

    /**
     * Cuenta las inscripciones activas (CONFIRMADA) de un evento
     * @param eventoId ID del evento
     * @return cantidad de inscripciones activas
     */
    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.evento.id = :eventoId AND i.estado = 'CONFIRMADA'")
    long countActiveInscriptionsByEventoId(@Param("eventoId") Long eventoId);

    /**
     * Verifica si un usuario tiene una inscripción activa a un evento
     * @param usuarioId ID del usuario
     * @param eventoId ID del evento
     * @return true si existe inscripción activa
     */
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Inscripcion i WHERE i.usuario.id = :usuarioId AND i.evento.id = :eventoId AND i.estado = 'CONFIRMADA'")
    boolean hasActiveInscription(@Param("usuarioId") Long usuarioId, @Param("eventoId") Long eventoId);
}