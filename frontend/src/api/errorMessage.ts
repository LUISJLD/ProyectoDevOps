import axios from "axios";

export function getErrorMessage(err: unknown): string {
    if (!axios.isAxiosError(err)) return "Error inesperado. Intenta nuevamente.";

    const status = err.response?.status;

    if (status === 400) return "Solicitud inválida. Revisa los datos ingresados.";
    if (status === 401) return "Credenciales incorrectas o sesión no autorizada.";
    if (status === 404) return "Recurso no encontrado (endpoint incorrecto o no disponible).";

    // si backend manda mensaje:
    const backendMsg =
        (err.response?.data as any)?.message ||
        (err.response?.data as any)?.error ||
        undefined;

    return backendMsg ?? "Error al conectar con el servidor.";
}
