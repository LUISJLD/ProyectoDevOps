import { http } from "./http";

export type User = {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  activo: boolean;
  roles: string[];
};


export type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; 
  first: boolean;
  last: boolean;
};

export type RegisterUserRequest = {
  nombre: string;
  apellido: string;
  telefono: string;
  email: string;
  password: string;
};

export type UpdateUserRequest = {
  nombre: string;
  apellido: string;
  telefono: string;
  email: string;
};

export async function getUsers(params: {
  page: number;
  size: number;
  nombre?: string;
  apellido?: string;
}): Promise<PageResponse<User>> {
  const { data } = await http.get<PageResponse<User>>("/api/users", { params });
  return data;
}

export async function createUser(req: RegisterUserRequest): Promise<User> {
  const { data } = await http.post<User>("/api/users/register", req);
  return data;
}

export async function deleteUser(id: number): Promise<void> {
  await http.delete(`/api/users/${id}`);
}

export async function activateUser(id: number): Promise<User> {
  const { data } = await http.patch<User>(`/api/users/${id}/activate`);
  return data;
}

export async function deactivateUser(id: number): Promise<User> {
  const { data } = await http.patch<User>(`/api/users/${id}/deactivate`);
  return data;
}

/**
 * EDITAR (PUT) NO EXISTE en el backend actualmente.
 * Cuando lo implementemos, descomentar esto y úsarlo desde el formulario.
 */
// export async function updateUser(id: number, req: UpdateUserRequest): Promise<User> {
//   const { data } = await http.put<User>(`/api/users/${id}`, req);
//   return data;
// }