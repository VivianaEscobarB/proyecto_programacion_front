import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

interface UserProfile {
  email: string | null;
  role: string | null;
  imageUrl: string | null;
  userId: string | null;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth'; // Endpoint real

  private userSubject = new BehaviorSubject<UserProfile>(this.readFromStorage());
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Llama a la API de login, espera recibir token y datos de usuario
  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('userId', response.userId);
        localStorage.setItem('userEmail', response.email);
        localStorage.setItem('userRole', response.roles?.[0] || 'CLIENTE');
        if (response.urlAccountPhoto) {
          localStorage.setItem('userImage', response.urlAccountPhoto.replace('/view', '/preview'));
        }
        this.userSubject.next(this.readFromStorage());
      })
    );
  }

  // Registro
  register(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, payload);
  }

  setUserImage(url: string) {
    localStorage.setItem('userImage', url);
    this.userSubject.next(this.readFromStorage());
  }

  updateUser(partial: Partial<UserProfile>) {
    const current = this.readFromStorage();
    const updated = { ...current, ...partial };
    if (updated.email !== null) localStorage.setItem('userEmail', updated.email);
    if (updated.role !== null) localStorage.setItem('userRole', updated.role);
    if (updated.imageUrl !== null) localStorage.setItem('userImage', updated.imageUrl);
    if (updated.userId !== null) localStorage.setItem('userId', updated.userId);
    this.userSubject.next(this.readFromStorage());
  }

  // Retorna la información actual del usuario autenticado (desde localStorage)
  getCurrentUser(): UserProfile {
    return this.readFromStorage();
  }

  private readFromStorage(): UserProfile {
    return {
      email: localStorage.getItem('userEmail'),
      role: localStorage.getItem('userRole'),
      imageUrl: localStorage.getItem('userImage') || '/assets/images/user_image_default.png',
      userId: localStorage.getItem('userId')
    };
  }

  // Valida si el usuario está logueado (basado en la existencia del token)
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // Opcional: Método para cerrar sesión y limpiar datos
  logout(): void {
    localStorage.clear();
    this.userSubject.next(this.readFromStorage());
  }
}
