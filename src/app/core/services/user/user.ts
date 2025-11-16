import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = 'http://localhost:8080/api/auth/user'; // URL base de la API

  constructor(private http: HttpClient) {}

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) headers = headers.set('Authorization', `Bearer ${token}`);
    return headers;
  }

  // Nuevo método para obtener perfil por ID
  getProfileById(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${userId}`, { headers: this.authHeaders() });
  }

  updatePhoto(userId: string, urlAccountPhoto: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${userId}/photo`, { urlAccountPhoto }, { headers: this.authHeaders() });
  }

  uploadPhotoFile(userId: string, file: File) {
    const form = new FormData();
    form.append('file', file);
    return this.http.post(`${this.apiUrl}/${userId}/photo/upload`, form, { headers: this.authHeaders().delete('Content-Type') });
  }

  updateProfile(userId: string, data: { phoneNumber?: string; homeAddress?: string; dayOfBirth?: string }): Observable<any> {
    return this.http.put(`${this.apiUrl}/${userId}`, data, { headers: this.authHeaders() });
  }
}
