import { useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export function AppHome() {
    const navigate = useNavigate();
    const { logout } = useAuth();

    return (
        <div style={{ maxWidth: 720, margin: "40px auto", fontFamily: "system-ui" }}>
            <h2>Área principal</h2>
            <p>Login exitoso. Aquí irá el contenido del sistema.</p>

            <button
                onClick={() => {
                    logout();
                    navigate("/login", { replace: true });
                }}
            >
                Cerrar sesión
            </button>
        </div>
    );
}
