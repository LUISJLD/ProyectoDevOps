package com.backend.demo.service;

import com.backend.demo.dto.request.CreateInscripcionRequest;
import com.backend.demo.dto.response.InscripcionResponse;

import java.util.List;

public interface IInscripcionService {
    InscripcionResponse createInscripcion(CreateInscripcionRequest request);
    InscripcionResponse getInscripcionById(Long id);
    List<InscripcionResponse> getInscripcionesByUsuario(Long usuarioId);
    List<InscripcionResponse> getInscripcionesByEvento(Long eventoId);
    InscripcionResponse cancelarInscripcion(Long id);
    void deleteInscripcion(Long id);
}