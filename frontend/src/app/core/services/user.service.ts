// src/app/services/user.service.ts
import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../models/user';
import { Observable } from 'rxjs';
import { API } from '../config/api.config';
import { LoginResponse } from '../models/login-response';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = API.USERS;
  private authUrl = API.AUTH;

  currentUser = signal<User | null>(null);
  private logoutTimer?: any; // for auto logout

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {
    this.restoreFromStorage();
  }

  // -------------------
  // AUTH
  // -------------------
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.authUrl}/login`, {
      username,
      password,
    });
  }

  handleLoginSuccess(res: LoginResponse) {
    // save token
    localStorage.setItem('token', res.token);

    // save user
    const user: User = {
      id: res.id,
      username: res.username,
      role: res.role,
    };

    localStorage.setItem('user', JSON.stringify(user));
    this.currentUser.set(user);

    this.setupAutoLogout(res.token);
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  private restoreFromStorage() {
    const token = localStorage.getItem('token');
    const userJson = localStorage.getItem('user');

    if (!token || !userJson) {
      this.logout();
      return;
    }

    try {
      const user = JSON.parse(userJson) as User;

      if (!user.id || !user.username || !user.role) {
        this.logout();
        return;
      }

      // decode JWT and check expiration
      const payload = JSON.parse(atob(token.split('.')[1]));
      const now = Math.floor(Date.now() / 1000); // in seconds
      if (!payload.exp || payload.exp < now) {
        this.logout();
        return;
      }

      this.currentUser.set(user);

      // setup auto logout timer
      this.setupAutoLogout(token);
    } catch {
      this.logout();
    }
  }

  private setupAutoLogout(token: string) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiresInMs = payload.exp * 1000 - Date.now();

      if (expiresInMs <= 0) {
        this.logout();
        return;
      }

      if (this.logoutTimer) {
        clearTimeout(this.logoutTimer);
      }

      this.logoutTimer = setTimeout(() => this.logout(), expiresInMs);
    } catch {
      this.logout();
    }
  }

  getToken(): string | null {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const now = Math.floor(Date.now() / 1000);

      if (payload.exp < now) {
        this.logout();
        return null;
      }

      return token;
    } catch {
      this.logout();
      return null;
    }
  }

  isLoggedIn(): boolean {
    return !!this.currentUser();
  }

  isAdmin(): boolean {
    return this.currentUser()?.role === 'ADMIN';
  }

  getCurrentUser(): User | null {
    return this.currentUser();
  }

  // -------------------
  // CRUD methods
  // -------------------
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  getUserByUsername(username: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/by-username/${username}`);
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${user.id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
