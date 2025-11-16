// lodging.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Accommodation} from '../../../domain/entities/accommodation';
import { map } from 'rxjs/operators';
import { resolveAccommodationImage } from '../../../domain/entities/accommodation-images';

@Injectable({ providedIn: 'root' })
export class AccommodationService {
  private apiUrl = 'http://localhost:8080/api/accommodations';

  constructor(private http: HttpClient) {}

  getAlojamientos(params?: any): Observable<Accommodation[]> {
    return this.http.get<any[]>(this.apiUrl, { params }).pipe(
      map(list => list.map(this.mapDtoToAccommodation))
    );
  }

  getAlojamientoById(id: number): Observable<Accommodation> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(this.mapDtoToAccommodation)
    );
  }

  getAlojamientosPaginados(page=0, size=10): Observable<{content: Accommodation[], totalElements: number}> {
    return this.http.get<any>(`${this.apiUrl}/page`, { params: { page, size } }).pipe(
      map((res:any) => ({ content: (res.content||[]).map(this.mapDtoToAccommodation), totalElements: res.totalElements || 0 }))
    );
  }

  getDestacados(limit=2): Observable<Accommodation[]> {
    return this.http.get<any[]>(`${this.apiUrl}/featured`, { params: { limit } }).pipe(
      map(list => list.map(this.mapDtoToAccommodation))
    );
  }

  private mapDtoToAccommodation = (dto: any): Accommodation => {
    const baseUrl = dto.mainPictureUrl || '/assets/images/deptos/finca_robles.png';
    const finalUrl = resolveAccommodationImage(dto.id, baseUrl);
    return {
      id: dto.id,
      nombre: dto.title,
      descripcion: dto.description,
      direccion: dto.address,
      ciudad: dto.cityName,
      estado: dto.stateName,
      anfitrion: dto.hostName,
      anfitrionImagen: dto.hostPhotoUrl || '/assets/images/avatar/avatar.png',
      precio: dto.dayPrice ?? 0,
      capacidad: dto.capacity?.toString(),
      imagenUrl: finalUrl,
      estrellas: 4, // temporal hasta tener rating real
      pictures: dto.pictures,
      services: dto.services
    } as any; // mantenemos compatibilidad hasta actualizar interfaz
  }
}
