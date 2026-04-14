// repository/UserRepository.java
package com.backend.demo.repository;

import com.backend.demo.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Busca un usuario por su token de recuperación de contraseña.
    // Se usa en procesos de reset de password.
    Optional<User> findByResetToken(String token);

    // Consulta personalizada con filtros opcionales usando JPQL.
    // Permite buscar usuarios por nombre y apellido de forma flexible.
    // Si algún parámetro es NULL, ese filtro se ignora.
    @Query("SELECT u FROM User u WHERE " +
            "(:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:apellido IS NULL OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :apellido, '%')))")
    Page<User> findByFilters(
            @Param("nombre") String nombre,
            @Param("apellido") String apellido,
            Pageable pageable
    );

    // Consulta para buscar usuarios filtrando por nombre y rol.
    // Los parámetros NULL se ignoran en los filtros.
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.roles r WHERE " +
            "(:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:rol IS NULL OR r.name = :rol)")
    Page<User> findByNombreAndRol(
            @Param("nombre") String nombre,
            @Param("rol") com.backend.demo.model.enums.ERole rol,
            Pageable pageable
    );
}