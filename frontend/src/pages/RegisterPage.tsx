import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { register } from "../api/auth";
import { getErrorMessage } from "../api/errorMessage";

export function RegisterPage() {
    const navigate = useNavigate();

    const [nombre, setNombre] = useState("");
    const [apellido, setApellido] = useState("");
    const [telefono, setTelefono] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    function validate(): string | null {
        if (!nombre.trim()) return "El nombre es obligatorio.";
        if (!apellido.trim()) return "El apellido es obligatorio.";
        if (!telefono.trim()) return "El teléfono es obligatorio.";
        if (!email.trim()) return "El email es obligatorio.";
        if (!password.trim()) return "La contraseña es obligatoria.";

        // Validación alineada con backend: ^[+]?[0-9]{10}$
        // (10 dígitos, opcional + al inicio)
        const phoneOk = /^[+]?[0-9]{10}$/.test(telefono.trim());
        if (!phoneOk) return "El teléfono debe contener 10 dígitos (puede iniciar con +).";

        // Validación alineada con backend: password min 8
        if (password.length < 8) return "La contraseña debe tener mínimo 8 caracteres.";

        return null;
    }

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError(null);

        const validationError = validate();
        if (validationError) {
            setError(validationError);
            return;
        }

        try {
            setLoading(true);
            await register({
                nombre: nombre.trim(),
                apellido: apellido.trim(),
                telefono: telefono.trim(),
                email: email.trim(),
                password,
            });

            // Criterio: tras registro, redirige al login
            navigate("/login", { replace: true });
        } catch (err) {
            setError(getErrorMessage(err));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ maxWidth: 420, margin: "40px auto", fontFamily: "system-ui" }}>
            <h2>Registro</h2>

            {error && (
                <div
                    style={{
                        background: "#fee",
                        border: "1px solid #f99",
                        padding: 12,
                        marginBottom: 12,
                    }}
                >
                    {error}
                </div>
            )}

            <form onSubmit={onSubmit}>
                <label>Nombre</label>
                <input
                    style={{ width: "100%", padding: 10, margin: "6px 0 12px" }}
                    type="text"
                    value={nombre}
                    onChange={(e) => setNombre(e.target.value)}
                    placeholder="Tu nombre"
                />

                <label>Apellido</label>
                <input
                    style={{ width: "100%", padding: 10, margin: "6px 0 12px" }}
                    type="text"
                    value={apellido}
                    onChange={(e) => setApellido(e.target.value)}
                    placeholder="Tu apellido"
                />

                <label>Teléfono</label>
                <input
                    style={{ width: "100%", padding: 10, margin: "6px 0 12px" }}
                    type="tel"
                    value={telefono}
                    onChange={(e) => setTelefono(e.target.value)}
                    placeholder="Ej: 3001234567"
                />

                <label>Email</label>
                <input
                    style={{ width: "100%", padding: 10, margin: "6px 0 12px" }}
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="correo@dominio.com"
                />

                <label>Contraseña</label>
                <input
                    style={{ width: "100%", padding: 10, margin: "6px 0 12px" }}
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Mínimo 8 caracteres"
                />

                <button disabled={loading} style={{ width: "100%", padding: 10 }}>
                    {loading ? "Registrando..." : "Crear cuenta"}
                </button>
            </form>

            <div style={{ marginTop: 12 }}>
                ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
            </div>
        </div>
    );
}
