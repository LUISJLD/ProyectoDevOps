import { useEffect, useMemo, useState } from "react";
import { createUser, deleteUser, deactivateUser, activateUser, getUsers, type User } from "../api/users";
import { getErrorMessage } from "../api/errorMessage";

type StatusFilter = "ALL" | "ACTIVE" | "INACTIVE";

export function UsersAdminPage() {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  // filtros backend existentes
  const [nombre, setNombre] = useState("");
  const [apellido, setApellido] = useState("");

  // filtro por estado (frontend)
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("ALL");

  const [data, setData] = useState<{ users: User[]; totalPages: number; totalElements: number } | null>(null);
  const [loading, setLoading] = useState(false);

  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Form crear usuario
  const [newNombre, setNewNombre] = useState("");
  const [newApellido, setNewApellido] = useState("");
  const [newTelefono, setNewTelefono] = useState("");
  const [newEmail, setNewEmail] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [creating, setCreating] = useState(false);

  async function load() {
    setError(null);
    setSuccess(null);

    try {
      setLoading(true);
      const res = await getUsers({
        page,
        size,
        nombre: nombre.trim() || undefined,
        apellido: apellido.trim() || undefined,
      });

      setData({
        users: res.content,
        totalPages: res.totalPages,
        totalElements: res.totalElements,
      });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, size]);

  // Al cambiar filtros de texto, reseteamos a primera página y recargamos
  async function applyTextFilters() {
    setPage(0);
    await loadWithPage0();
  }

  async function loadWithPage0() {
    setError(null);
    setSuccess(null);

    try {
      setLoading(true);
      const res = await getUsers({
        page: 0,
        size,
        nombre: nombre.trim() || undefined,
        apellido: apellido.trim() || undefined,
      });

      setData({
        users: res.content,
        totalPages: res.totalPages,
        totalElements: res.totalElements,
      });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  const filteredUsers = useMemo(() => {
    if (!data) return [];
    if (statusFilter === "ALL") return data.users;
    if (statusFilter === "ACTIVE") return data.users.filter((u) => u.activo);
    return data.users.filter((u) => !u.activo);
  }, [data, statusFilter]);

  function validateCreate(): string | null {
    if (!newNombre.trim()) return "Nombre es obligatorio.";
    if (!newApellido.trim()) return "Apellido es obligatorio.";
    if (!newTelefono.trim()) return "Teléfono es obligatorio.";
    if (!newEmail.trim()) return "Email es obligatorio.";
    if (!newPassword.trim()) return "Password es obligatorio.";

    if (!/^[+]?[0-9]{10}$/.test(newTelefono.trim())) {
      return "El teléfono debe contener 10 dígitos (puede iniciar con +).";
    }
    if (newPassword.length < 8) return "La contraseña debe tener mínimo 8 caracteres.";

    return null;
  }

  async function onCreate(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    const v = validateCreate();
    if (v) {
      setError(v);
      return;
    }

    try {
      setCreating(true);
      await createUser({
        nombre: newNombre.trim(),
        apellido: newApellido.trim(),
        telefono: newTelefono.trim(),
        email: newEmail.trim(),
        password: newPassword,
      });

      setSuccess("Usuario creado correctamente.");
      setNewNombre("");
      setNewApellido("");
      setNewTelefono("");
      setNewEmail("");
      setNewPassword("");

      // Recargar lista (primera página para verlo fácil)
      setPage(0);
      await loadWithPage0();
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setCreating(false);
    }
  }

  async function onDelete(u: User) {
    setError(null);
    setSuccess(null);

    const ok = window.confirm(`¿Seguro que deseas eliminar a ${u.nombre} ${u.apellido} (id=${u.id})?`);
    if (!ok) return;

    try {
      await deleteUser(u.id);
      setSuccess("Usuario eliminado correctamente.");
      await load();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }

  async function onToggleActive(u: User) {
    setError(null);
    setSuccess(null);

    try {
      if (u.activo) {
        await deactivateUser(u.id);
        setSuccess("Usuario desactivado.");
      } else {
        await activateUser(u.id);
        setSuccess("Usuario activado.");
      }
      await load();
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }

  return (
    <div style={{ maxWidth: 1100, margin: "30px auto", fontFamily: "system-ui", padding: 12 }}>
      <h2>Administración de Usuarios</h2>

      {error && (
        <div style={{ background: "#fee", border: "1px solid #f99", padding: 12, margin: "12px 0" }}>
          {error}
        </div>
      )}
      {success && (
        <div style={{ background: "#efe", border: "1px solid #9f9", padding: 12, margin: "12px 0" }}>
          {success}
        </div>
      )}

      {/* Filtros */}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr 160px", gap: 10, alignItems: "end" }}>
        <div>
          <label>Filtrar por nombre (backend)</label>
          <input
            style={{ width: "100%", padding: 10, marginTop: 6 }}
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
            placeholder="Ej: Juan"
          />
        </div>
        <div>
          <label>Filtrar por apellido (backend)</label>
          <input
            style={{ width: "100%", padding: 10, marginTop: 6 }}
            value={apellido}
            onChange={(e) => setApellido(e.target.value)}
            placeholder="Ej: Pérez"
          />
        </div>
        <div>
          <label>Filtrar por estado (frontend)</label>
          <select
            style={{ width: "100%", padding: 10, marginTop: 6 }}
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as StatusFilter)}
          >
            <option value="ALL">Todos</option>
            <option value="ACTIVE">Activos</option>
            <option value="INACTIVE">Inactivos</option>
          </select>
        </div>
        <button onClick={applyTextFilters} style={{ padding: 10 }}>
          Aplicar filtros
        </button>
      </div>

      {/* Crear */}
      <div style={{ marginTop: 20, borderTop: "1px solid #ddd", paddingTop: 16 }}>
        <h3>Crear usuario</h3>
        <form onSubmit={onCreate} style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
          <input style={{ padding: 10 }} value={newNombre} onChange={(e) => setNewNombre(e.target.value)} placeholder="Nombre" />
          <input style={{ padding: 10 }} value={newApellido} onChange={(e) => setNewApellido(e.target.value)} placeholder="Apellido" />
          <input style={{ padding: 10 }} value={newTelefono} onChange={(e) => setNewTelefono(e.target.value)} placeholder="Teléfono (10 dígitos)" />
          <input style={{ padding: 10 }} value={newEmail} onChange={(e) => setNewEmail(e.target.value)} placeholder="Email" type="email" />
          <input style={{ padding: 10 }} value={newPassword} onChange={(e) => setNewPassword(e.target.value)} placeholder="Password (min 8)" type="password" />
          <button disabled={creating} style={{ padding: 10 }}>
            {creating ? "Creando..." : "Crear"}
          </button>
        </form>
      </div>

      {/* Listado */}
      <div style={{ marginTop: 20, borderTop: "1px solid #ddd", paddingTop: 16 }}>
        <h3>Listado</h3>

        <div style={{ display: "flex", gap: 10, alignItems: "center", marginBottom: 10 }}>
          <button disabled={page === 0 || loading} onClick={() => setPage((p) => Math.max(0, p - 1))}>
            Anterior
          </button>
          <div>
            Página: <b>{page + 1}</b> / <b>{data?.totalPages ?? 1}</b> — Total usuarios: <b>{data?.totalElements ?? 0}</b>
          </div>
          <button
            disabled={loading || !data || page + 1 >= data.totalPages}
            onClick={() => setPage((p) => p + 1)}
          >
            Siguiente
          </button>

          <div style={{ marginLeft: "auto" }}>
            <label>Tamaño</label>{" "}
            <select value={size} onChange={(e) => setSize(Number(e.target.value))}>
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
            </select>
          </div>
        </div>

        {loading && <div>Cargando...</div>}

        {!loading && (
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr>
                {["ID", "Nombre", "Apellido", "Email", "Teléfono", "Activo", "Acciones"].map((h) => (
                  <th key={h} style={{ borderBottom: "1px solid #ddd", textAlign: "left", padding: 8 }}>
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map((u) => (
                <tr key={u.id}>
                  <td style={{ borderBottom: "1px solid #eee", padding: 8 }}>{u.id}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: 8 }}>{u.nombre}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: 8 }}>{u.apellido}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: 8 }}>{u.email}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: 8 }}>{u.telefono}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: 8 }}>{u.activo ? "Sí" : "No"}</td>
                  <td style={{ borderBottom: "1px solid #eee", padding: 8, display: "flex", gap: 8, flexWrap: "wrap" }}>
                    <button onClick={() => onToggleActive(u)}>{u.activo ? "Desactivar" : "Activar"}</button>
                    {/* Editar requiere PUT /api/users/{id} (no existe aún) */}
                    <button disabled title="Falta endpoint PUT /api/users/{id} en backend">
                      Editar
                    </button>
                    <button onClick={() => onDelete(u)} style={{ color: "darkred" }}>
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
              {filteredUsers.length === 0 && (
                <tr>
                  <td colSpan={7} style={{ padding: 12 }}>
                    No hay usuarios para mostrar con los filtros seleccionados.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        )}

        <p style={{ marginTop: 10, color: "#555" }}>
          Nota: el filtro por estado (activo/inactivo) se aplica en frontend porque el backend no expone query param para
          “activo”. Si lo agregan, la paginación reflejará mejor el filtro.
        </p>
      </div>
    </div>
  );
}