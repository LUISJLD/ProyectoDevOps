package com.backend.demo.repository;

import com.backend.demo.model.entity.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Verificar si ya existe la inscripcion
    boolean existsByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);

    // Buscar inscripcion específica
    Optional<Inscripcion> findByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);

    // Inscripciones de un usuario
    List<Inscripcion> findByUsuarioId(Long usuarioId);

    // Inscripciones de un evento
    List<Inscripcion> findByEventoId(Long eventoId);
}