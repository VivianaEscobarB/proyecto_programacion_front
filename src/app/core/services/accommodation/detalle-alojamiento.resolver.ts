import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Accommodation } from '../../../domain/entities/accommodation';
import { AccommodationService } from './accommodation';

@Injectable({ providedIn: 'root' })
export class DetalleAlojamientoResolver implements Resolve<Accommodation> {
  constructor(private accommodationService: AccommodationService) {}
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Accommodation> {
    const id = Number(route.paramMap.get('id'));
    return this.accommodationService.getAlojamientoById(id);
  }
}

