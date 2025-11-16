import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private apiUrl = 'http://localhost:8080/api/client/reservations';

  constructor(private http: HttpClient) {}

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) headers = headers.set('Authorization', `Bearer ${token}`);
    return headers;
  }

  crearReserva(reserva: any): Observable<any> {
    return this.http.post(this.apiUrl, reserva, { headers: this.authHeaders() });
  }

  listarMisReservas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/me`, { headers: this.authHeaders() });
  }

  cancelarReserva(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/cancel`, {}, { headers: this.authHeaders() });
  }

  eliminarReserva(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.authHeaders() });
  }
}
