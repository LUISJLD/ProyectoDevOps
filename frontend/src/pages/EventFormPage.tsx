import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getEvent, createEvent, updateEvent, type Event } from "../api/eventos";

const P = {
    bgMid: "#0f2240",
    bgTo: "#091528",
    surface: "#162035",
    border: "rgba(99,149,210,0.18)",
    borderFocus: "rgba(99,149,210,0.5)",
    text: "#f0f6ff",
    textMuted: "rgba(200,220,255,0.55)",
    textFaint: "rgba(200,220,255,0.3)",
    accent: "#2563eb",
    accentHover: "#1d4ed8",
    red: "#f87171",
};

const inputStyle: React.CSSProperties = {
    width: "100%",
    background: "rgba(9,21,40,0.3)",
    border: `1px solid ${P.border}`,
    borderRadius: "8px",
    padding: "10px 14px",
    color: P.text,
    fontSize: "14px",
    outline: "none",
    boxSizing: "border-box",
    transition: "border 0.2s ease",
};

export function EventFormPage() {
    const { eventoId } = useParams();
    const navigate = useNavigate();
    const isEditing = !!eventoId;

    const [formData, setFormData] = useState<Partial<Event>>({
        nombre: "",
        descripcion: "",
        fecha: "",
        hora: "",
        ubicacion: "",
        capacidadMaxima: 10,
        parkingAvailable: false,
        parkingSpots: 0,
    });
    
    const [loading, setLoading] = useState(false);
    const [initialLoading, setInitialLoading] = useState(isEditing);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (isEditing && eventoId) {
            getEvent(eventoId)
                .then((data) => {
                    setFormData(data);
                    setInitialLoading(false);
                })
                .catch((e) => {
                    setError("No se pudo cargar el evento: " + e.message);
                    setInitialLoading(false);
                });
        }
    }, [eventoId, isEditing]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        try {
            if (isEditing && eventoId) {
                await updateEvent(eventoId, formData);
            } else {
                await createEvent(formData);
            }
            navigate("/my-events");
        } catch (err: any) {
            setError(err.response?.data?.message || err.message || "Error al guardar el evento");
        } finally {
            setLoading(false);
        }
    };

    if (initialLoading) {
        return (
            <div style={{ minHeight: "100vh", background: `radial-gradient(ellipse 80% 60% at 50% -10%, #1a3a6e 0%, ${P.bgMid} 45%, ${P.bgTo} 100%)`, color: P.text, display: "flex", justifyContent: "center", alignItems: "center" }}>
                Cargando...
            </div>
        );
    }

    return (
        <div
            style={{
                minHeight: "100vh",
                background: `radial-gradient(ellipse 80% 60% at 50% -10%, #1a3a6e 0%, ${P.bgMid} 45%, ${P.bgTo} 100%)`,
                color: P.text,
                fontFamily: "'Segoe UI', system-ui, sans-serif",
                padding: "2rem",
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                boxSizing: "border-box"
            }}
        >
            <div style={{ width: "100%", maxWidth: "600px", marginBottom: "1rem" }}>
                <button
                    onClick={() => navigate(-1)}
                    style={{
                        background: "transparent",
                        border: "none",
                        color: P.textMuted,
                        cursor: "pointer",
                        fontSize: "14px",
                        padding: 0
                    }}
                >
                    ← Volver
                </button>
            </div>

            <div
                style={{
                    background: P.surface,
                    border: `1px solid ${P.border}`,
                    borderRadius: "16px",
                    padding: "2rem",
                    width: "100%",
                    maxWidth: "600px",
                    boxShadow: "0 10px 40px rgba(0,0,0,0.25)",
                    boxSizing: "border-box"
                }}
            >
                <h1 style={{ margin: "0 0 1.5rem", fontSize: "1.5rem" }}>
                    {isEditing ? "Editar Evento" : "Crear Nuevo Evento"}
                </h1>

                {error && (
                    <div style={{ background: "rgba(248,113,113,0.1)", border: `1px solid ${P.red}`, color: P.red, padding: "10px", borderRadius: "8px", marginBottom: "1rem" }}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
                    <div>
                        <label style={{ display: "block", marginBottom: "5px", fontSize: "13px", color: P.textMuted }}>Nombre del Evento</label>
                        <input
                            type="text"
                            required
                            value={formData.nombre}
                            onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                            style={inputStyle}
                        />
                    </div>

                    <div>
                        <label style={{ display: "block", marginBottom: "5px", fontSize: "13px", color: P.textMuted }}>Descripción</label>
                        <textarea
                            required
                            rows={3}
                            value={formData.descripcion}
                            onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
                            style={{ ...inputStyle, resize: "vertical" }}
                        />
                    </div>

                    <div style={{ display: "flex", gap: "1rem" }}>
                        <div style={{ flex: 1 }}>
                            <label style={{ display: "block", marginBottom: "5px", fontSize: "13px", color: P.textMuted }}>Fecha</label>
                            <input
                                type="date"
                                required
                                value={formData.fecha}
                                onChange={(e) => setFormData({ ...formData, fecha: e.target.value })}
                                style={inputStyle}
                            />
                        </div>
                        <div style={{ flex: 1 }}>
                            <label style={{ display: "block", marginBottom: "5px", fontSize: "13px", color: P.textMuted }}>Hora</label>
                            <input
                                type="time"
                                required
                                value={formData.hora?.substring(0,5)}
                                onChange={(e) => setFormData({ ...formData, hora: e.target.value + ":00" })}
                                style={inputStyle}
                            />
                        </div>
                    </div>

                    <div>
                        <label style={{ display: "block", marginBottom: "5px", fontSize: "13px", color: P.textMuted }}>Ubicación</label>
                        <input
                            type="text"
                            required
                            value={formData.ubicacion}
                            onChange={(e) => setFormData({ ...formData, ubicacion: e.target.value })}
                            style={inputStyle}
                        />
                    </div>

                    <div>
                        <label style={{ display: "block", marginBottom: "5px", fontSize: "13px", color: P.textMuted }}>Capacidad Máxima</label>
                        <input
                            type="number"
                            min="1"
                            required
                            value={formData.capacidadMaxima}
                            onChange={(e) => setFormData({ ...formData, capacidadMaxima: parseInt(e.target.value) || 0 })}
                            style={inputStyle}
                        />
                    </div>

                    <div style={{ display: "flex", alignItems: "center", gap: "10px", marginTop: "10px" }}>
                        <input
                            type="checkbox"
                            id="parking"
                            checked={formData.parkingAvailable}
                            onChange={(e) => setFormData({ ...formData, parkingAvailable: e.target.checked })}
                        />
                        <label htmlFor="parking" style={{ fontSize: "14px" }}>Habilitar Parqueadero</label>
                    </div>

                    {formData.parkingAvailable && (
                        <div>
                            <label style={{ display: "block", marginBottom: "5px", fontSize: "13px", color: P.textMuted }}>Cupos de Parqueadero</label>
                            <input
                                type="number"
                                min="0"
                                value={formData.parkingSpots}
                                onChange={(e) => setFormData({ ...formData, parkingSpots: parseInt(e.target.value) || 0 })}
                                style={inputStyle}
                            />
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        style={{
                            marginTop: "1rem",
                            background: "linear-gradient(135deg, #2563eb, #1d4ed8)",
                            border: "none",
                            borderRadius: "8px",
                            color: "#fff",
                            fontSize: "14px",
                            fontWeight: 600,
                            padding: "12px",
                            cursor: loading ? "not-allowed" : "pointer",
                            boxShadow: "0 4px 14px rgba(37,99,235,0.35)",
                        }}
                    >
                        {loading ? "Guardando..." : "Guardar Evento"}
                    </button>
                </form>
            </div>
        </div>
    );
}
