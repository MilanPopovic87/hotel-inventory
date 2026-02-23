export interface LoginResponse {
  token: string;
  id: number;
  username: string;
  role: 'USER' | 'ADMIN';
}
