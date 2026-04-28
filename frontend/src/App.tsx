import { Navigate, Route, Routes } from "react-router-dom";
import { ProtectedRoute } from "./auth/ProtectedRoute";
import { AppHome } from "./pages/AppHome";
import { ForgotPasswordPage } from "./pages/ForgotPasswordPage";
import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { useAuth } from "./auth/AuthContext";

export default function App() {
  const { isAuthenticated } = useAuth();

  return (
      <Routes>
        <Route path="/" element={<Navigate to={isAuthenticated ? "/app" : "/login"} replace />} />

        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />

        <Route
            path="/app"
            element={
              <ProtectedRoute>
                <AppHome />
              </ProtectedRoute>
            }
        />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
  );
}
