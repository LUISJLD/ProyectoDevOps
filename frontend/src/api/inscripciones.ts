import { http } from "./http";
import type { Event } from "./eventos";

export type InscripcionResponse = {
    id: number;
    usuarioId: number;
    eventoId: number;
    evento: Event;
    estado: string;
    asistio: boolean;
    checkinAt?: string | null;
    qrUrl?: string;
};

export type PageResponse<T> = {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
};

export async function getInscripcionesByUser(usuarioId: string | number, params?: { page?: number; size?: number }) {
    const { data } = await http.get<PageResponse<InscripcionResponse>>(`/api/inscripciones/usuarios/${usuarioId}/inscripciones`, { params });
    return data;
}

export async function cancelInscripcion(inscripcionId: string | number) {
    await http.delete(`/api/inscripciones/${inscripcionId}`);
}

export async function getInscripcionQr(inscripcionId: string | number) {
    const { data } = await http.get<{ qrUrl: string }>(`/api/inscripciones/inscripciones/${inscripcionId}/qr`);
    return data;
}
