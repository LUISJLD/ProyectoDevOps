import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    getEvents,
    registerToEvent,
    type Event,
    type EventStatus,
    type PageResponse,
} from "../api/eventos";
import { getInscripcionesByUser } from "../api/inscripciones";
import { getErrorMessage } from "../api/errorMessage";
import { useAuth } from "../auth/AuthContext";

const P = {
    bgMid: "#0f2240",
    bgTo: "#091528",

    surface: "#162035",
    surfaceHover: "#1c2a45",

    border: "rgba(99,149,210,0.18)",
    borderMid: "rgba(99,149,210,0.3)",

    accent: "#2563eb",
    accentLight: "#93c5fd",
    accentSoft: "rgba(37,99,235,0.12)",

    text: "#f0f6ff",
    textMuted: "rgba(200,220,255,0.55)",
    textFaint: "rgba(200,220,255,0.28)",

    green: "#4ade80",
    greenSoft: "rgba(74,222,128,0.12)",

    red: "#f87171",
    redSoft: "rgba(248,113,113,0.12)",

    amber: "#fbbf24",
    amberSoft: "rgba(251,191,36,0.1)",

    purple: "#a78bfa",
    purpleSoft: "rgba(167,139,250,0.12)",

    cyan: "#67e8f9",
    cyanSoft: "rgba(103,232,249,0.1)",
};

const statusMap: Record<
    string,
    { color: string; soft: string; label: string }
> = {
    PUBLISHED: {
        color: P.green,
        soft: P.greenSoft,
        label: "Publicado",
    },

    DRAFT: {
        color: P.amber,
        soft: P.amberSoft,
        label: "Borrador",
    },

    CANCELLED: {
        color: P.red,
        soft: P.redSoft,
        label: "Cancelado",
    },

    CLOSED: {
        color: P.textFaint,
        soft: "rgba(255,255,255,0.05)",
        label: "Cerrado",
    },
};

function Badge({ estado }: { estado: string }) {
    const s = statusMap[estado] ?? {
        color: P.accentLight,
        soft: P.accentSoft,
        label: estado,
    };

    return (
        <span
            style={{
                fontSize: "10.5px",
                fontWeight: 700,
                letterSpacing: "0.08em",
                textTransform: "uppercase",
                padding: "3px 10px",
                borderRadius: "20px",
                background: s.soft,
                border: `1px solid ${s.color}35`,
                color: s.color,
            }}
        >
            {s.label}
        </span>
    );
}

const eventStatuses: EventStatus[] = [
    "DRAFT",
    "PUBLISHED",
    "CLOSED",
    "CANCELLED",
];

export function EventListPage() {
    const navigate = useNavigate();
    const { isAdmin, user } = useAuth();

    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);

    const [nombre, setNombre] = useState("");
    const [estado, setEstado] = useState<EventStatus | "">("");

    const [data, setData] = useState<PageResponse<Event> | null>(null);

    const [loading, setLoading] = useState(false);

    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);

    const [registeringId, setRegisteringId] = useState<number | null>(null);
    const [registeredEventIds, setRegisteredEventIds] = useState<Set<number>>(new Set());

    async function load(pageNumber: number = page) {
        setError(null);
        // ❌ NO BORRAR SUCCESS AQUÍ

        try {
            setLoading(true);

            const res = await getEvents({
                page: pageNumber,
                size,
                nombre: nombre.trim() || undefined,
                estado: estado || undefined,
            });

            setData(res);

            if (user?.id) {
                const insc = await getInscripcionesByUser(user.id, { size: 200 });
                const activeIds = insc.content
                    .filter((i) => i.estado === "CONFIRMADA" || i.estado === "ASISTIDA")
                    .map((i) => i.eventoId);
                setRegisteredEventIds(new Set(activeIds));
            }
        } catch (err) {
            setError(getErrorMessage(err));
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, [page, size]);

    async function applyFilters() {
        setPage(0);
        setSuccess(null); // ✔ opcional: limpiar mensaje al filtrar
        await load(0);
    }

    async function onRegister(eventoId: number) {
        setError(null);
        setSuccess(null);
        setRegisteringId(eventoId);

        try {
            await registerToEvent(eventoId);

            setSuccess(
                "Inscripción realizada. Revisa tu correo para recibir el QR de invitación."
            );

            await load(); // recarga lista
        } catch (err) {
            setError(getErrorMessage(err));
        } finally {
            setRegisteringId(null);
        }
    }

    return (
        <div
            style={{
                minHeight: "100vh",
                margin: 0,
                padding: 0,
                border: "none",

                background: `radial-gradient(ellipse 80% 60% at 50% -10%, #1a3a6e 0%, ${P.bgMid} 45%, ${P.bgTo} 100%)`,

                color: P.text,
                fontFamily:
                    "'Segoe UI', system-ui, sans-serif",

                boxSizing: "border-box",
            }}
        >
            {/* HEADER */}
            <header
                style={{
                    width: "100%",
                    display: "flex",
                    justifyContent: "center",

                    borderBottom: `1px solid ${P.border}`,

                    background: "rgba(9,21,40,0.6)",
                    backdropFilter: "blur(16px)",

                    position: "sticky",
                    top: 0,
                    zIndex: 10,

                    padding: "1rem 1.5rem",
                    boxSizing: "border-box",
                }}
            >
                <div
                    style={{
                        width: "100%",
                        maxWidth: "1200px",

                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",

                        gap: "1rem",
                        flexWrap: "wrap",
                    }}
                >
                    <div
                        style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "10px",
                        }}
                    >
                        <div
                            style={{
                                width: "28px",
                                height: "28px",
                                borderRadius: "8px",

                                background:
                                    "linear-gradient(135deg, #1d4ed8, #3b82f6)",

                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                            }}
                        >
                            <svg
                                width="14"
                                height="14"
                                viewBox="0 0 14 14"
                                fill="none"
                            >
                                <path
                                    d="M7 1.5L12 4.5v5L7 12.5 2 9.5v-5L7 1.5z"
                                    stroke="white"
                                    strokeWidth="1.2"
                                    fill="none"
                                    strokeLinejoin="round"
                                />

                                <circle
                                    cx="7"
                                    cy="7"
                                    r="1.5"
                                    fill="white"
                                />
                            </svg>
                        </div>

                        <span
                            style={{
                                fontSize: "14px",
                                fontWeight: 600,
                                color: P.text,
                            }}
                        >
                            Sistema de Eventos
                        </span>
                    </div>

                    <div
                        style={{
                            display: "flex",
                            gap: "10px",
                            flexWrap: "wrap",
                            justifyContent: "center",
                        }}
                    >
                        <button
                            onClick={() =>
                                navigate("/app")
                            }
                            style={btnSecondary}
                        >
                            Inicio
                        </button>

                        <button
                            onClick={() =>
                                navigate("/user")
                            }
                            style={btnSecondary}
                        >
                            Mi perfil
                        </button>

                        {isAdmin && (
                            <button
                                onClick={() =>
                                    navigate("/admin")
                                }
                                style={btnSecondary}
                            >
                                Admin
                            </button>
                        )}
                    </div>
                </div>
            </header>

            {/* MAIN */}
            <main
                style={{
                    width: "100%",
                    display: "flex",
                    justifyContent: "center",

                    padding: "2.5rem 1.5rem",
                    boxSizing: "border-box",
                }}
            >
                <div
                    style={{
                        width: "100%",
                        maxWidth: "1200px",
                    }}
                >
                    {/* TITLE */}
                    <div
                        style={{
                            marginBottom: "2rem",
                            textAlign: "center",

                            display: "flex",
                            flexDirection: "column",
                            alignItems: "center",
                        }}
                    >
                        <div
                            style={{
                                display: "inline-flex",
                                alignItems: "center",
                                gap: "6px",

                                fontSize: "11px",
                                letterSpacing: "0.15em",
                                textTransform: "uppercase",

                                fontWeight: 600,
                                color: P.accentLight,

                                background:
                                    "rgba(37,99,235,0.14)",

                                border:
                                    "1px solid rgba(59,130,246,0.3)",

                                borderRadius: "20px",

                                padding: "5px 14px",

                                marginBottom: "1rem",
                            }}
                        >
                            <span
                                style={{
                                    fontSize: "10px",
                                }}
                            >
                                ●
                            </span>

                            Explorar
                        </div>

                        <h1
                            style={{
                                fontSize:
                                    "clamp(1.8rem, 4vw, 2.7rem)",

                                fontWeight: 700,

                                margin:
                                    "0 0 0.6rem",

                                letterSpacing:
                                    "-0.02em",
                            }}
                        >
                            Eventos disponibles
                        </h1>

                        <p
                            style={{
                                fontSize: "0.95rem",
                                color: P.textMuted,
                                margin: 0,
                                maxWidth: "700px",
                                lineHeight: 1.7,
                            }}
                        >
                            Revisa los próximos eventos,
                            su estado y regístrate para
                            recibir tu invitación con QR.
                        </p>
                    </div>

                    {/* FILTERS */}
                    <div
                        style={{
                            background: P.surface,
                            border: `1px solid ${P.border}`,

                            borderRadius: "16px",

                            padding: "1.5rem",

                            display: "flex",
                            gap: "14px",

                            flexWrap: "wrap",

                            alignItems: "flex-end",

                            justifyContent: "center",

                            marginBottom: "1.8rem",
                        }}
                    >
                        <div
                            style={{
                                display: "flex",
                                flexDirection:
                                    "column",

                                gap: "6px",

                                flex:
                                    "1 1 280px",

                                maxWidth: "400px",
                            }}
                        >
                            <label
                                style={{
                                    fontSize:
                                        "11px",

                                    fontWeight:
                                        600,

                                    letterSpacing:
                                        "0.08em",

                                    textTransform:
                                        "uppercase",

                                    color:
                                        P.textFaint,

                                    textAlign:
                                        "center",
                                }}
                            >
                                Buscar por nombre
                            </label>

                            <input
                                value={nombre}
                                onChange={(e) =>
                                    setNombre(
                                        e.target.value
                                    )
                                }
                                onKeyDown={(e) =>
                                    e.key ===
                                        "Enter" &&
                                    applyFilters()
                                }
                                placeholder="Nombre del evento..."
                                style={{
                                    background:
                                        "rgba(255,255,255,0.05)",

                                    border:
                                        `1px solid ${P.border}`,

                                    borderRadius:
                                        "10px",

                                    color: P.text,

                                    fontSize:
                                        "14px",

                                    padding:
                                        "11px 14px",

                                    outline:
                                        "none",

                                    fontFamily:
                                        "inherit",

                                    textAlign:
                                        "center",
                                }}
                            />
                        </div>

                        <div
                            style={{
                                display: "flex",
                                flexDirection:
                                    "column",

                                gap: "6px",

                                flex:
                                    "1 1 180px",

                                maxWidth: "220px",
                            }}
                        >
                            <label
                                style={{
                                    fontSize:
                                        "11px",

                                    fontWeight:
                                        600,

                                    letterSpacing:
                                        "0.08em",

                                    textTransform:
                                        "uppercase",

                                    color:
                                        P.textFaint,

                                    textAlign:
                                        "center",
                                }}
                            >
                                Estado
                            </label>

                            <select
                                value={estado}
                                onChange={(e) =>
                                    setEstado(
                                        e.target
                                            .value as
                                            | EventStatus
                                            | ""
                                    )
                                }
                                style={{
                                    background:
                                        "#162035",

                                    border:
                                        `1px solid ${P.border}`,

                                    borderRadius:
                                        "10px",

                                    color: P.text,

                                    fontSize:
                                        "14px",

                                    padding:
                                        "11px 14px",

                                    cursor:
                                        "pointer",

                                    fontFamily:
                                        "inherit",

                                    textAlign:
                                        "center",
                                }}
                            >
                                <option value="">
                                    Todos
                                </option>

                                {eventStatuses.map(
                                    (s) => (
                                        <option
                                            key={s}
                                            value={s}
                                        >
                                            {s}
                                        </option>
                                    )
                                )}
                            </select>
                        </div>

                        <button
                            onClick={applyFilters}
                            style={{
                                ...btnPrimary,
                                minWidth: "140px",
                            }}
                        >
                            Buscar
                        </button>
                    </div>

                    {/* ALERTS */}
                    {error && (
                        <div
                            style={{
                                display: "flex",
                                justifyContent:
                                    "center",

                                marginBottom:
                                    "1.2rem",
                            }}
                        >
                            <div
                                style={{
                                    background:
                                        P.redSoft,

                                    border:
                                        `1px solid rgba(248,113,113,0.25)`,

                                    borderRadius:
                                        "10px",

                                    padding:
                                        "12px 16px",

                                    textAlign:
                                        "center",

                                    width: "100%",
                                    maxWidth:
                                        "700px",
                                }}
                            >
                                <span
                                    style={{
                                        fontSize:
                                            "13.5px",

                                        color:
                                            P.red,
                                    }}
                                >
                                    {error}
                                </span>
                            </div>
                        </div>
                    )}

                    {success && (
                        <div
                            style={{
                                display: "flex",
                                justifyContent:
                                    "center",

                                marginBottom:
                                    "1.2rem",
                            }}
                        >
                            <div
                                style={{
                                    background:
                                        P.greenSoft,

                                    border:
                                        `1px solid rgba(74,222,128,0.25)`,

                                    borderRadius:
                                        "10px",

                                    padding:
                                        "12px 16px",

                                    textAlign:
                                        "center",

                                    width: "100%",
                                    maxWidth:
                                        "700px",
                                }}
                            >
                                <span
                                    style={{
                                        fontSize:
                                            "13.5px",

                                        color:
                                            P.green,
                                    }}
                                >
                                    {success}
                                </span>
                            </div>
                        </div>
                    )}

                    {/* LOADING */}
                    {loading ? (
                        <div
                            style={{
                                display: "flex",
                                alignItems: "center",
                                justifyContent:
                                    "center",

                                minHeight: "260px",
                            }}
                        >
                            <div
                                style={{
                                    textAlign:
                                        "center",
                                }}
                            >
                                <div
                                    style={{
                                        width: "36px",
                                        height:
                                            "36px",

                                        borderRadius:
                                            "50%",

                                        border:
                                            `2px solid ${P.border}`,

                                        borderTopColor:
                                            P.accentLight,

                                        margin:
                                            "0 auto 12px",

                                        animation:
                                            "spin 0.8s linear infinite",
                                    }}
                                />

                                <style>
                                    {`@keyframes spin { to { transform: rotate(360deg); } }`}
                                </style>

                                <p
                                    style={{
                                        color:
                                            P.textMuted,

                                        fontSize:
                                            "14px",
                                    }}
                                >
                                    Cargando
                                    eventos...
                                </p>
                            </div>
                        </div>
                    ) : !data?.content.length ? (
                        <div
                            style={{
                                background:
                                    P.surface,

                                border:
                                    `1px solid ${P.border}`,

                                borderRadius:
                                    "16px",

                                padding: "3rem",

                                textAlign:
                                    "center",
                            }}
                        >
                            <div
                                style={{
                                    fontSize:
                                        "2rem",

                                    marginBottom:
                                        "0.75rem",
                                }}
                            >
                                🗓️
                            </div>

                            <p
                                style={{
                                    color:
                                        P.textMuted,

                                    fontSize:
                                        "14px",

                                    margin: 0,
                                }}
                            >
                                No se encontraron
                                eventos con los
                                filtros seleccionados.
                            </p>
                        </div>
                    ) : (
                        <>
                            {/* GRID */}
                            <div
                                style={{
                                    display: "grid",

                                    gridTemplateColumns:
                                        "repeat(auto-fit, minmax(320px, 1fr))",

                                    gap: "18px",

                                    marginBottom:
                                        "1.8rem",
                                }}
                            >
                                {data.content.map(
                                    (evento) => {
                                        const s =
                                            statusMap[
                                                evento
                                                    .estado
                                            ] ??
                                            statusMap.DRAFT;

                                        return (
                                            <article
                                                key={
                                                    evento.id
                                                }
                                                style={{
                                                    background:
                                                        P.surface,

                                                    border:
                                                        `1px solid ${P.border}`,

                                                    borderRadius:
                                                        "18px",

                                                    padding:
                                                        "1.5rem",

                                                    display:
                                                        "flex",

                                                    flexDirection:
                                                        "column",

                                                    textAlign:
                                                        "center",

                                                    transition:
                                                        "all 0.2s ease",

                                                    position:
                                                        "relative",

                                                    overflow:
                                                        "hidden",
                                                }}
                                                onMouseEnter={(
                                                    e
                                                ) => {
                                                    e.currentTarget.style.background =
                                                        P.surfaceHover;

                                                    e.currentTarget.style.borderColor =
                                                        `${s.color}40`;

                                                    e.currentTarget.style.transform =
                                                        "translateY(-3px)";
                                                }}
                                                onMouseLeave={(
                                                    e
                                                ) => {
                                                    e.currentTarget.style.background =
                                                        P.surface;

                                                    e.currentTarget.style.borderColor =
                                                        P.border;

                                                    e.currentTarget.style.transform =
                                                        "translateY(0)";
                                                }}
                                            >
                                                <div
                                                    style={{
                                                        position:
                                                            "absolute",

                                                        top: 0,

                                                        left:
                                                            "15%",

                                                        right:
                                                            "15%",

                                                        height:
                                                            "1px",

                                                        background:
                                                            `linear-gradient(90deg, transparent, ${s.color}55, transparent)`,
                                                    }}
                                                />

                                                {/* TOP */}
                                                <div
                                                    style={{
                                                        display:
                                                            "flex",

                                                        justifyContent:
                                                            "space-between",

                                                        alignItems:
                                                            "center",

                                                        marginBottom:
                                                            "1rem",

                                                        gap: "10px",
                                                    }}
                                                >
                                                    <Badge
                                                        estado={
                                                            evento.estado
                                                        }
                                                    />

                                                    <span
                                                        style={{
                                                            fontSize:
                                                                "12px",

                                                            color:
                                                                P.textFaint,
                                                        }}
                                                    >
                                                        {
                                                            evento.fecha
                                                        }{" "}
                                                        ·{" "}
                                                        {
                                                            evento.hora
                                                        }
                                                    </span>
                                                </div>

                                                {/* TITLE */}
                                                <h2
                                                    style={{
                                                        fontSize:
                                                            "1.1rem",

                                                        fontWeight:
                                                            700,

                                                        color:
                                                            P.text,

                                                        margin:
                                                            "0 0 0.7rem",

                                                        lineHeight:
                                                            1.35,
                                                    }}
                                                >
                                                    {
                                                        evento.nombre
                                                    }
                                                </h2>

                                                {/* DESC */}
                                                <p
                                                    style={{
                                                        fontSize:
                                                            "13px",

                                                        lineHeight:
                                                            1.7,

                                                        color:
                                                            P.textMuted,

                                                        margin:
                                                            "0 0 1rem",

                                                        flexGrow:
                                                            1,
                                                    }}
                                                >
                                                    {evento.descripcion ||
                                                        "Sin descripción disponible."}
                                                </p>

                                                {/* CHIPS */}
                                                <div
                                                    style={{
                                                        display:
                                                            "flex",

                                                        flexWrap:
                                                            "wrap",

                                                        justifyContent:
                                                            "center",

                                                        gap: "6px",

                                                        marginBottom:
                                                            "1.1rem",
                                                    }}
                                                >
                                                    {[
                                                        evento.ubicacion,

                                                        `Cupos: ${evento.capacidadMaxima ?? "-"}`,

                                                        evento.parkingAvailable
                                                            ? `P: ${evento.parkingSpots ?? 0}`
                                                            : "Sin parking",
                                                    ].map(
                                                        (
                                                            chip,
                                                            i
                                                        ) => (
                                                            <span
                                                                key={
                                                                    i
                                                                }
                                                                style={{
                                                                    fontSize:
                                                                        "11px",

                                                                    color:
                                                                        P.textFaint,

                                                                    background:
                                                                        "rgba(255,255,255,0.04)",

                                                                    border:
                                                                        `1px solid ${P.border}`,

                                                                    borderRadius:
                                                                        "20px",

                                                                    padding:
                                                                        "4px 10px",
                                                                }}
                                                            >
                                                                {
                                                                    chip
                                                                }
                                                            </span>
                                                        )
                                                    )}
                                                </div>

                                                {/* ACTIONS */}
                                                <div
                                                    style={{
                                                        display:
                                                            "flex",

                                                        gap: "8px",

                                                        flexWrap:
                                                            "wrap",

                                                        justifyContent:
                                                            "center",

                                                        borderTop:
                                                            `1px solid ${P.border}`,

                                                        paddingTop:
                                                            "1rem",
                                                    }}
                                                >
                                                    <button
                                                        onClick={() =>
                                                            navigate(
                                                                `/events/${evento.id}`
                                                            )
                                                        }
                                                        style={
                                                            btnSecondary
                                                        }
                                                    >
                                                        Ver
                                                        detalle
                                                    </button>

                                                    <button
                                                        onClick={() =>
                                                            onRegister(
                                                                evento.id
                                                            )
                                                        }
                                                        disabled={
                                                            evento.estado !==
                                                                "PUBLISHED" ||
                                                            registeringId ===
                                                                evento.id ||
                                                            registeredEventIds.has(evento.id)
                                                        }
                                                        style={{
                                                            ...btnPrimary,

                                                            opacity:
                                                                evento.estado !==
                                                                    "PUBLISHED" ||
                                                                registeringId ===
                                                                    evento.id ||
                                                                registeredEventIds.has(evento.id)
                                                                    ? 0.45
                                                                    : 1,

                                                            cursor:
                                                                evento.estado !==
                                                                    "PUBLISHED" ||
                                                                registeringId ===
                                                                    evento.id ||
                                                                registeredEventIds.has(evento.id)
                                                                    ? "not-allowed"
                                                                    : "pointer",

                                                            fontSize:
                                                                "13px",

                                                            padding:
                                                                "8px 16px",
                                                        }}
                                                    >
                                                        {evento.estado !== "PUBLISHED"
                                                            ? "No disponible"
                                                            : registeredEventIds.has(evento.id)
                                                                ? "Inscrito"
                                                                : registeringId === evento.id
                                                                    ? "Inscribiendo..."
                                                                    : "Inscribirse →"}
                                                    </button>

                                                    {isAdmin && (
                                                        <>
                                                            <button
                                                                onClick={() =>
                                                                    navigate(
                                                                        `/checkin/escanear/${evento.id}`
                                                                    )
                                                                }
                                                                style={{
                                                                    ...btnGhost,
                                                                    fontSize: "13px",
                                                                    padding: "8px 12px",
                                                                }}
                                                            >
                                                                QR
                                                            </button>

                                                            <button
                                                                onClick={() =>
                                                                    navigate(
                                                                        `/eventos/${evento.id}/reporte`
                                                                    )
                                                                }
                                                                style={{
                                                                    ...btnGhost,
                                                                    fontSize: "13px",
                                                                    padding: "8px 12px",
                                                                    color: P.purple,
                                                                    background: P.purpleSoft,
                                                                    borderColor: "rgba(167,139,250,0.25)",
                                                                }}
                                                            >
                                                                Rep.
                                                            </button>
                                                        </>
                                                    )}
                                                </div>
                                            </article>
                                        );
                                    }
                                )}
                            </div>

                            {/* PAGINATION */}
                            {data && (
                                <div
                                    style={{
                                        background:
                                            P.surface,

                                        border:
                                            `1px solid ${P.border}`,

                                        borderRadius:
                                            "14px",

                                        padding:
                                            "1rem 1.5rem",

                                        display:
                                            "flex",

                                        alignItems:
                                            "center",

                                        gap: "14px",

                                        flexWrap:
                                            "wrap",

                                        justifyContent:
                                            "center",
                                    }}
                                >
                                    <button
                                        disabled={
                                            loading ||
                                            page === 0
                                        }
                                        onClick={() =>
                                            setPage(
                                                (
                                                    p
                                                ) =>
                                                    Math.max(
                                                        0,
                                                        p -
                                                            1
                                                    )
                                            )
                                        }
                                        style={{
                                            ...btnSecondary,

                                            opacity:
                                                page ===
                                                0
                                                    ? 0.4
                                                    : 1,

                                            cursor:
                                                page ===
                                                0
                                                    ? "not-allowed"
                                                    : "pointer",
                                        }}
                                    >
                                        ←
                                        Anterior
                                    </button>

                                    <span
                                        style={{
                                            fontSize:
                                                "13px",

                                            color:
                                                P.textMuted,
                                        }}
                                    >
                                        Página{" "}
                                        <strong
                                            style={{
                                                color:
                                                    P.text,
                                            }}
                                        >
                                            {page +
                                                1}
                                        </strong>{" "}
                                        de{" "}
                                        <strong
                                            style={{
                                                color:
                                                    P.text,
                                            }}
                                        >
                                            {data.totalPages ??
                                                1}
                                        </strong>
                                    </span>

                                    <button
                                        disabled={
                                            loading ||
                                            !data ||
                                            page +
                                                1 >=
                                                data.totalPages
                                        }
                                        onClick={() =>
                                            setPage(
                                                (
                                                    p
                                                ) =>
                                                    p +
                                                    1
                                            )
                                        }
                                        style={{
                                            ...btnSecondary,

                                            opacity:
                                                !data ||
                                                page +
                                                    1 >=
                                                    data.totalPages
                                                    ? 0.4
                                                    : 1,

                                            cursor:
                                                !data ||
                                                page +
                                                    1 >=
                                                    data.totalPages
                                                    ? "not-allowed"
                                                    : "pointer",
                                        }}
                                    >
                                        Siguiente →
                                    </button>

                                    <div
                                        style={{
                                            display:
                                                "flex",

                                            alignItems:
                                                "center",

                                            gap: "8px",
                                        }}
                                    >
                                        <span
                                            style={{
                                                fontSize:
                                                    "12px",

                                                color:
                                                    P.textFaint,
                                            }}
                                        >
                                            Mostrar
                                        </span>

                                        <select
                                            value={
                                                size
                                            }
                                            onChange={(
                                                e
                                            ) =>
                                                setSize(
                                                    Number(
                                                        e
                                                            .target
                                                            .value
                                                    )
                                                )
                                            }
                                            style={{
                                                background:
                                                    "#162035",

                                                border:
                                                    `1px solid ${P.border}`,

                                                borderRadius:
                                                    "7px",

                                                color:
                                                    P.text,

                                                fontSize:
                                                    "13px",

                                                padding:
                                                    "5px 10px",

                                                cursor:
                                                    "pointer",

                                                fontFamily:
                                                    "inherit",
                                            }}
                                        >
                                            <option value={5}>
                                                5
                                            </option>

                                            <option value={10}>
                                                10
                                            </option>

                                            <option value={20}>
                                                20
                                            </option>
                                        </select>

                                        <span
                                            style={{
                                                fontSize:
                                                    "12px",

                                                color:
                                                    P.textFaint,
                                            }}
                                        >
                                            por
                                            página
                                        </span>
                                    </div>
                                </div>
                            )}
                        </>
                    )}
                </div>
            </main>
        </div>
    );
}

const btnPrimary: React.CSSProperties = {
    background:
        "linear-gradient(135deg, #2563eb, #1d4ed8)",

    border: "none",

    borderRadius: "8px",

    color: "#fff",

    fontSize: "13px",

    fontWeight: 600,

    padding: "9px 18px",

    cursor: "pointer",

    boxShadow:
        "0 4px 14px rgba(37,99,235,0.35)",

    letterSpacing: "0.01em",
};

const btnSecondary: React.CSSProperties = {
    background: "transparent",

    border:
        "1px solid rgba(99,149,210,0.3)",

    borderRadius: "8px",

    color: "rgba(200,220,255,0.7)",

    fontSize: "13px",

    fontWeight: 500,

    padding: "9px 18px",

    cursor: "pointer",
};

const btnGhost: React.CSSProperties = {
    background:
        "rgba(103,232,249,0.08)",

    border:
        "1px solid rgba(103,232,249,0.2)",

    borderRadius: "8px",

    color: "#67e8f9",

    fontSize: "13px",

    fontWeight: 500,

    padding: "9px 14px",

    cursor: "pointer",
};