import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth'; // Endpoint real

  constructor(private http: HttpClient) {}

  // Llama a la API de login, espera recibir token y datos de usuario
  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  // Retorna la información actual del usuario autenticado (desde localStorage)
  getCurrentUser() {
    const userId = localStorage.getItem('userId');
    const email = localStorage.getItem('userEmail');
    console.log('userId from localStorage:', userId, 'email:', email);
    return {
      email: localStorage.getItem('userEmail'),
      role: localStorage.getItem('userRole'),
      imageUrl: localStorage.getItem('userImage'),
      userId: userId
    };
  }

  // Valida si el usuario está logueado (basado en la existencia del token)
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  // Opcional: Método para cerrar sesión y limpiar datos
  logout(): void {
    localStorage.clear();
  }
}
