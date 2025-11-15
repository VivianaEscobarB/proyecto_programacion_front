import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface HostReservationItem {
  id: number;
  userId: number;
  accommodationId: number;
  checkIn: string;
  checkOut: string;
  countRoommates: number;
  stateName: string;
  accommodationTitle?: string;
  cityName?: string;
  guestName?: string;
  guestEmail?: string;
  estimatedTotal?: number;
}

@Injectable({ providedIn: 'root' })
export class HostReservationsService {
  private baseUrl = 'http://localhost:8080/api/client/reservations';

  constructor(private http: HttpClient) {}

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) headers = headers.set('Authorization', `Bearer ${token}`);
    return headers;
  }

  listMine(): Observable<HostReservationItem[]> {
    return this.http.get<HostReservationItem[]>(`${this.baseUrl}/host/me`, { headers: this.authHeaders() });
  }

  listMineFiltered(state: string, page: number, size: number): Observable<HostReservationItem[]> {
    const params: any = { page, size };
    if (state) params.state = state;
    return this.http.get<HostReservationItem[]>(`${this.baseUrl}/host/me`, { headers: this.authHeaders(), params });
  }

  approve(reservationId: number): Observable<HostReservationItem> {
    return this.http.post<HostReservationItem>(`${this.baseUrl}/${reservationId}/approve`, {}, { headers: this.authHeaders() });
  }

  reject(reservationId: number): Observable<HostReservationItem> {
    return this.http.post<HostReservationItem>(`${this.baseUrl}/${reservationId}/reject`, {}, { headers: this.authHeaders() });
  }

  finalize(id: number): Observable<HostReservationItem> {
    return this.http.post<HostReservationItem>(`${this.baseUrl}/${id}/finalize`, {}, { headers: this.authHeaders() });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers: this.authHeaders() });
  }
}
