export interface User {
  id?: number; // optional for new users
  username: string;
  password?: string; // optional in list view
  role: 'USER' | 'ADMIN';
}
