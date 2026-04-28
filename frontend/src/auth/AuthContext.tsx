import React, { createContext, useContext, useMemo, useState } from "react";
import { clearToken, getToken, setToken } from "./token";

type AuthContextValue = {
    isAuthenticated: boolean;
    token: string | null;
    loginWithToken: (token: string) => void;
    logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [token, setTokenState] = useState<string | null>(() => getToken());

    const value = useMemo<AuthContextValue>(() => {
        return {
            isAuthenticated: !!token,
            token,
            loginWithToken: (t: string) => {
                setToken(t);
                setTokenState(t);
            },
            logout: () => {
                clearToken();
                setTokenState(null);
            },
        };
    }, [token]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error("useAuth debe usarse dentro de <AuthProvider>");
    return ctx;
}
