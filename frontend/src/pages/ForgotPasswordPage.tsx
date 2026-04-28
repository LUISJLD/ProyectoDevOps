import { useState } from "react";
import { Link } from "react-router-dom";
import { forgotPassword } from "../api/auth";
import { getErrorMessage } from "../api/errorMessage";

export function ForgotPasswordPage() {
    const [email, setEmail] = useState("");

    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError(null);
        setSuccess(null);

        if (!email.trim()) {
            setError("El email es obligatorio.");
            return;
        }

        try {
            setLoading(true);
            await forgotPassword(email.trim());
            setSuccess("Si el correo existe, recibirás instrucciones para recuperar tu contraseña.");
        } catch (err) {
            setError(getErrorMessage(err));
        } finally {
            setLoading(false);
        }
    }

    return (
        <div style={{ maxWidth: 420, margin: "40px auto", fontFamily: "system-ui" }}>
            <h2>Recuperar contraseña</h2>

            {error && (
                <div style={{ background: "#fee", border: "1px solid #f99", padding: 12, marginBottom: 12 }}>
                    {error}
                </div>
            )}

            {success && (
                <div style={{ background: "#efe", border: "1px solid #9f9", padding: 12, marginBottom: 12 }}>
                    {success}
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

                <button disabled={loading} style={{ width: "100%", padding: 10 }}>
                    {loading ? "Enviando..." : "Enviar"}
                </button>
            </form>

            <div style={{ marginTop: 12 }}>
                <Link to="/login">Volver al login</Link>
            </div>
        </div>
    );
}
