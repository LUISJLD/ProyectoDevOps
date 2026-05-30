import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { getEventsByUser, type Event } from "../api/eventos";
import { getInscripcionesByUser, cancelInscripcion, getInscripcionQr, type InscripcionResponse } from "../api/inscripciones";

const P = {
    bgMid: "#0f2240",
    bgTo: "#091528",
    surface: "#162035",
    surfaceHover: "#1c2a45",
    border: "rgba(99,149,210,0.18)",
    borderMid: "rgba(99,149,210,0.3)",
    text: "#f0f6ff",
    textMuted: "rgba(200,220,255,0.55)",
    textFaint: "rgba(200,220,255,0.3)",
    accent: "#2563eb",
    accentHover: "#1d4ed8",
    red: "#f87171",
    green: "#4ade80",
    purple: "#a78bfa"
};

export function MyEventsPage() {
    const { user } = useAuth();
    const navigate = useNavigate();

    const [tab, setTab] = useState<"inscripciones" | "organizados">("inscripciones");
    
    // Inscriptions state
    const [inscripciones, setInscripciones] = useState<InscripcionResponse[]>([]);
    const [loadingInsc, setLoadingInsc] = useState(false);

    // Organized events state
    const [organizados, setOrganizados] = useState<Event[]>([]);
    const [loadingOrg, setLoadingOrg] = useState(false);

    // Modal state for QR
    const [qrUrl, setQrUrl] = useState<string | null>(null);

    useEffect(() => {
        if (!user) return;
        if (tab === "inscripciones") {
            loadInscripciones();
        } else {
            loadOrganizados();
        }
    }, [tab, user]);

    const loadInscripciones = async () => {
        setLoadingInsc(true);
        try {
            if (user?.id) {
                const res = await getInscripcionesByUser(user.id, { size: 100 });
                setInscripciones(res.content);
            }
        } catch (e) {
            console.error("Error cargando inscripciones", e);
        } finally {
            setLoadingInsc(false);
        }
    };

    const loadOrganizados = async () => {
        setLoadingOrg(true);
        try {
            if (user?.id) {
                const data = await getEventsByUser(user.id);
                setOrganizados(data);
            }
        } catch (e) {
            console.error("Error cargando eventos organizados", e);
        } finally {
            setLoadingOrg(false);
        }
    };

    const handleCancelInscripcion = async (id: number) => {
        if (!confirm("¿Estás seguro de cancelar tu inscripción?")) return;
        try {
            await cancelInscripcion(id);
            loadInscripciones();
        } catch (e) {
            alert("Error al cancelar la inscripción.");
        }
    };

    const handleShowQr = async (id: number) => {
        try {
            const data = await getInscripcionQr(id);
            setQrUrl(data.qrUrl);
        } catch (e) {
            alert("No se pudo cargar el QR.");
        }
    };

    return (
        <div style={{
            minHeight: "100vh",
            background: `radial-gradient(ellipse 80% 60% at 50% -10%, #1a3a6e 0%, ${P.bgMid} 45%, ${P.bgTo} 100%)`,
            color: P.text,
            fontFamily: "'Segoe UI', system-ui, sans-serif",
            padding: "2rem",
            display: "flex",
            flexDirection: "column",
            alignItems: "center"
        }}>
            <div style={{ width: "100%", maxWidth: "800px", marginBottom: "1rem" }}>
                <button onClick={() => navigate("/app")} style={{ background: "transparent", border: "none", color: P.textMuted, cursor: "pointer", fontSize: "14px", padding: 0 }}>
                    ← Volver a Inicio
                </button>
            </div>

            <div style={{ width: "100%", maxWidth: "800px", display: "flex", gap: "10px", marginBottom: "2rem" }}>
                <button
                    onClick={() => setTab("inscripciones")}
                    style={{
                        flex: 1, padding: "12px", borderRadius: "12px", border: "none", cursor: "pointer", fontWeight: 600, fontSize: "15px",
                        background: tab === "inscripciones" ? "linear-gradient(135deg, #2563eb, #1d4ed8)" : "rgba(255,255,255,0.05)",
                        color: tab === "inscripciones" ? "#fff" : P.textMuted,
                        boxShadow: tab === "inscripciones" ? "0 4px 14px rgba(37,99,235,0.35)" : "none"
                    }}
                >
                    Mis Inscripciones
                </button>
                <button
                    onClick={() => setTab("organizados")}
                    style={{
                        flex: 1, padding: "12px", borderRadius: "12px", border: "none", cursor: "pointer", fontWeight: 600, fontSize: "15px",
                        background: tab === "organizados" ? "linear-gradient(135deg, #2563eb, #1d4ed8)" : "rgba(255,255,255,0.05)",
                        color: tab === "organizados" ? "#fff" : P.textMuted,
                        boxShadow: tab === "organizados" ? "0 4px 14px rgba(37,99,235,0.35)" : "none"
                    }}
                >
                    Eventos que Organizo
                </button>
            </div>

            <div style={{ width: "100%", maxWidth: "800px" }}>
                {tab === "inscripciones" && (
                    <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
                        {loadingInsc ? <p>Cargando...</p> : inscripciones.length === 0 ? <p style={{color: P.textMuted}}>No tienes inscripciones activas.</p> : inscripciones.map(i => (
                            <div key={i.id} style={{ background: P.surface, border: `1px solid ${P.border}`, borderRadius: "12px", padding: "1.5rem", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                                <div>
                                    <h3 style={{ margin: "0 0 5px", fontSize: "1.2rem" }}>{i.evento?.nombre || "Evento Desconocido"}</h3>
                                    <p style={{ margin: "0", color: P.textMuted, fontSize: "14px" }}>Estado: {i.estado}</p>
                                </div>
                                <div style={{ display: "flex", gap: "10px" }}>
                                    <button onClick={() => handleShowQr(i.id)} style={{ background: P.accent, color: "#fff", border: "none", borderRadius: "8px", padding: "8px 16px", cursor: "pointer", fontWeight: 500 }}>
                                        Ver QR
                                    </button>
                                    <button onClick={() => handleCancelInscripcion(i.id)} style={{ background: "rgba(248,113,113,0.1)", border: `1px solid ${P.red}`, color: P.red, borderRadius: "8px", padding: "8px 16px", cursor: "pointer", fontWeight: 500 }}>
                                        Cancelar
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {tab === "organizados" && (
                    <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
                        {loadingOrg ? <p>Cargando...</p> : organizados.length === 0 ? <p style={{color: P.textMuted}}>No has organizado ningún evento.</p> : organizados.map(e => (
                            <div key={e.id} style={{ background: P.surface, border: `1px solid ${P.border}`, borderRadius: "12px", padding: "1.5rem", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                                <div>
                                    <h3 style={{ margin: "0 0 5px", fontSize: "1.2rem" }}>{e.nombre}</h3>
                                    <p style={{ margin: "0", color: P.textMuted, fontSize: "14px" }}>Estado: {e.estado} | {e.fecha}</p>
                                </div>
                                <div style={{ display: "flex", gap: "10px" }}>
                                    <button onClick={() => navigate(`/events/${e.id}`)} style={{ background: P.accent, color: "#fff", border: "none", borderRadius: "8px", padding: "8px 16px", cursor: "pointer", fontWeight: 500 }}>
                                        Gestionar
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* QR Modal */}
            {qrUrl && (
                <div style={{ position: "fixed", top: 0, left: 0, right: 0, bottom: 0, background: "rgba(0,0,0,0.8)", display: "flex", justifyContent: "center", alignItems: "center", zIndex: 1000 }}>
                    <div style={{ background: P.surface, padding: "2rem", borderRadius: "16px", textAlign: "center", border: `1px solid ${P.border}` }}>
                        <h2 style={{ margin: "0 0 1rem" }}>Código QR de Ingreso</h2>
                        <img src={qrUrl} alt="QR Code" style={{ width: "250px", height: "250px", objectFit: "contain", background: "#fff", padding: "10px", borderRadius: "8px" }} />
                        <br/>
                        <button onClick={() => setQrUrl(null)} style={{ marginTop: "1rem", background: "rgba(255,255,255,0.1)", border: "none", color: "#fff", padding: "10px 20px", borderRadius: "8px", cursor: "pointer", fontWeight: 600 }}>
                            Cerrar
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
