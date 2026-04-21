package com.backend.demo.controller;

import com.backend.demo.dto.request.CreateInscripcionRequest;
import com.backend.demo.dto.response.InscripcionResponse;
import com.backend.demo.service.IInscripcionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    @Autowired
    private IInscripcionService inscripcionService;

    @PostMapping
    public ResponseEntity<InscripcionResponse> create(
            @Valid @RequestBody CreateInscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscripcionService.createInscripcion(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscripcionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.getInscripcionById(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<InscripcionResponse>> getByUsuario(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(inscripcionService.getInscripcionesByUsuario(usuarioId));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<InscripcionResponse>> getByEvento(
            @PathVariable Long eventoId) {
        return ResponseEntity.ok(inscripcionService.getInscripcionesByEvento(eventoId));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<InscripcionResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(inscripcionService.cancelarInscripcion(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inscripcionService.deleteInscripcion(id);
        return ResponseEntity.noContent().build();
    }
}