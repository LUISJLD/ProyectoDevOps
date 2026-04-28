import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../api/auth";
import { getErrorMessage } from "../api/errorMessage";
import { useAuth } from "../auth/AuthContext";

export function LoginPage() {
    const navigate = useNavigate();
    const { loginWithToken } = useAuth();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError(null);

        // Validación de vacíos (requerida por el US)
        if (!email.trim() || !password.trim()) {
            setError("Email y contraseña son obligatorios.");
            return;
        }

        try {
            setLoading(true);
            const res = await login({ email: email.trim(), password });
            loginWithToken(res.token); // si backend usa accessToken, cambiamos aquí
            navigate("/app", { replace: true });
        } catch (err) {
            setError(getErrorMessage(err));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ maxWidth: 420, margin: "40px auto", fontFamily: "system-ui" }}>
            <h2>Iniciar sesión</h2>

            {error && (
                <div style={{ background: "#fee", border: "1px solid #f99", padding: 12, marginBottom: 12 }}>
                    {error}
                </div>
            )}

            <form onSubmit={onSubmit}>
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
                    placeholder="********"
                />

                <button disabled={loading} style={{ width: "100%", padding: 10 }}>
                    {loading ? "Ingresando..." : "Ingresar"}
                </button>
            </form>

            <div style={{ marginTop: 12 }}>
                <Link to="/forgot-password">¿Olvidaste tu contraseña?</Link>
            </div>
            <div style={{ marginTop: 8 }}>
                ¿No tienes cuenta? <Link to="/register">Regístrate</Link>
            </div>
        </div>
    );
}
