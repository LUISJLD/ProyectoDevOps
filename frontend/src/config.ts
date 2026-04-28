export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL as string;

if (!API_BASE_URL) {
    // Esto ayuda a detectar cuando olvidaste el .env
    // (en prod puedes quitarlo)
    console.warn("VITE_API_BASE_URL no está definido");
}