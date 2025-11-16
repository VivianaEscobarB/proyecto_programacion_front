import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

interface FavoritePayload {
  accommodation: { id: number };
}

@Injectable({ providedIn: 'root' })
export class FavoriteService {
  private apiUrl = 'http://localhost:8080/api/favorites';

  constructor(private http: HttpClient) {}

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  addFavorite(accommodationId: number): Observable<any> {
    const body: FavoritePayload = { accommodation: { id: accommodationId } };
    return this.http.post<any>(this.apiUrl, body, { headers: this.authHeaders() });
  }

  listMyFavorites(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.authHeaders() });
  }

  deleteFavorite(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.authHeaders() });
  }

  isFavorite(accommodationId: number): Observable<boolean> {
    return this.http.get<{favorite:boolean}>(`${this.apiUrl}/my/accommodation/${accommodationId}`, { headers: this.authHeaders() })
      .pipe(map(res => !!res?.favorite));
  }
}
