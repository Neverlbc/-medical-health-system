import http from '@/api/http';

export interface LoginPayload {
  username: string;
  password: string;
}

export interface LoginResponse {
  userId: number;
  username: string;
  nickname?: string;
  role: string;
  token: string;
  expiresIn?: number;
}

export const login = (payload: LoginPayload) =>
  http.post<LoginResponse>('/auth/login', payload);

export const logout = () => http.post('/auth/logout');

export interface RegisterPayload {
  username: string;
  password: string;
  phone: string;
  email?: string;
  role: 'PATIENT' | 'DOCTOR';
}

export const register = (payload: RegisterPayload) =>
  http.post('/auth/register', payload);

