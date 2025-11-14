// lodging.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Accommodation} from '../../../domain/entities/accommodation';

@Injectable({ providedIn: 'root' })
export class AccommodationService {
  private apiUrl = 'http://localhost:8080/api/alojamientos'; // modifica según tu API

  constructor(private http: HttpClient) {}

  getAlojamientos(params?: any): Observable<Accommodation[]> {
    // Permite enviar filtros por departamento, ciudad, etc
    return this.http.get<Accommodation[]>(this.apiUrl, { params });
  }
}
