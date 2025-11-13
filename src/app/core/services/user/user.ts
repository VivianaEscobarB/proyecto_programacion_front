import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = 'http://localhost:8080/api/auth/user'; // URL base de la API

  constructor(private http: HttpClient) {}

  // Nuevo método para obtener perfil por ID
   getProfileById(userId: string): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    // Ajusta el endpoint para usar ID, ejemplo: /api/usuarios/{id}
    return this.http.get(`${this.apiUrl}/${userId}`, { headers });
  }
}
