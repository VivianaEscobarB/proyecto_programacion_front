import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

import { ReservaService } from '../../../core/services/reserva/reserva.service';
import {ButtonComponent} from '../../atoms/button/button';
import {LabelComponent} from '../../atoms/label/label';
import {HeaderComponent} from '../../organisms/header/header/header';
import {FormsModule} from '@angular/forms';
import {CurrencyPipe} from '@angular/common';
import {FooterComponent} from '../../organisms/footer/footer/footer';
import { StarRatingComponent } from '../../atoms/star-rating/star-rating';
import { AvatarComponent } from '../../atoms/avatar/avatar';
import { AccommodationService } from '../../../core/services/accommodation/accommodation';
import { FavoriteService } from '../../../core/services/accommodation/favorite.service';
import { ImgFallbackDirective } from '../../atoms/img-fallback/img-fallback.directive';
import { resolveAccommodationImage } from '../../../domain/entities/accommodation-images';

@Component({
  selector: 'app-detalles-alojamientos',
  templateUrl: './detalles-alojamiento.html',
  imports: [
    ButtonComponent,
    LabelComponent,
    HeaderComponent,
    FormsModule,
    CurrencyPipe,
    FooterComponent,
    StarRatingComponent,
    AvatarComponent,
    CommonModule,
    ImgFallbackDirective
  ],
  styleUrls: ['./detalles-alojamiento.scss']
})
export class DetallesAlojamientoPageComponent implements OnInit {
  fechaLlegada: string = '';
  fechaSalida: string = '';
  cantidadHuespedes: number = 4;
  total: number = 4500000;
  alojamiento: any;
  noches: number = 1;
  favorito = false;
  reservando = false;
  errorReserva: string | null = null;
  capacidadMax: number | null = null;

  constructor(private reservaService: ReservaService, private route: ActivatedRoute, private accommodationService: AccommodationService, private favoriteService: FavoriteService) {}

  ngOnInit(): void {
    const nav = (window as any).history.state;
    if (nav && nav.alojamiento) {
      this.alojamiento = nav.alojamiento;
      this.alojamiento.imagenUrl = resolveAccommodationImage(this.alojamiento.id, this.alojamiento.imagenUrl);
      this.updateCapacidadMaxFromAlojamiento();
      this.calcularTotal();
    }
    this.route.data.subscribe(data => {
      if (data['alojamiento']) {
        const dto = data['alojamiento'];
        dto.imagenUrl = resolveAccommodationImage(dto.id, dto.imagenUrl);
        this.alojamiento = { ...dto, imagenUrl: resolveAccommodationImage(dto.id, dto.imagenUrl) };
        this.updateCapacidadMaxFromAlojamiento();
        this.calcularTotal();
      } else if (!this.alojamiento) {
        const idParam = this.route.snapshot.paramMap.get('id');
        const id = idParam ? parseInt(idParam, 10) : null;
        if (id !== null) {
          this.accommodationService.getAlojamientoById(id).subscribe(a => {
            a.imagenUrl = resolveAccommodationImage(a.id, a.imagenUrl);
            this.alojamiento = a;
            this.updateCapacidadMaxFromAlojamiento();
            this.calcularTotal();
          });
        }
      }
    });
    // Después de resolver alojamiento, verificar favorito
    setTimeout(() => {
      if (this.alojamiento?.id) {
        this.favoriteService.isFavorite(this.alojamiento.id).subscribe({
          next: (isFav) => this.favorito = isFav,
          error: () => this.favorito = false
        });
      }
    }, 0);
  }

  onFechasChange() {
    // Recalcular noches sólo cuando ambas fechas estén presentes
    if (!this.fechaLlegada || !this.fechaSalida) {
      this.noches = 1;
      this.calcularTotal();
      return;
    }
    const inDate = this.parseYMD(this.fechaLlegada);
    const outDate = this.parseYMD(this.fechaSalida);
    const diffMs = outDate.getTime() - inDate.getTime();
    const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    this.noches = isFinite(days) && days > 0 ? days : 1;
    this.calcularTotal();
  }

  cambiarImagenPrincipal(url: string) {
    if (this.alojamiento) {
      this.alojamiento.imagenUrl = url;
    }
  }

  calcularTotal() {
    const precio = Number(this.alojamiento?.precio) || 0;
    const noches = Number(this.noches) || 1;
    // Política: precio por noche, independiente de huéspedes. Si se requiere por persona, multiplicar por this.cantidadHuespedes
    this.total = Math.max(0, precio * Math.max(1, noches));
  }

  onHuespedesChange() {
    // Asegurar entero mínimo 1 y no superar capacidad del alojamiento
    let val = Math.max(1, Math.trunc(Number(this.cantidadHuespedes) || 1));
    if (this.capacidadMax !== null) {
      val = Math.min(val, this.capacidadMax);
    }
    this.cantidadHuespedes = val;
    this.calcularTotal();
  }

  // Utilidad: parsear 'yyyy-mm-dd' sin efectos de zona horaria/DST
  private parseYMD(value: string): Date {
    const [y, m, d] = value.split('-').map(v => parseInt(v, 10));
    return new Date(y, (m || 1) - 1, d || 1);
  }

  private datosReservaValidos(): boolean {
    if (!this.alojamiento?.id) return false;
    if (!this.fechaLlegada || !this.fechaSalida) return false;
    if (!this.cantidadHuespedes || this.cantidadHuespedes < 1) return false;
    const inDate = this.parseYMD(this.fechaLlegada);
    const outDate = this.parseYMD(this.fechaSalida);
    if (!(outDate.getTime() > inDate.getTime())) return false;
    // capacidad
    if (this.capacidadMax !== null && this.cantidadHuespedes > this.capacidadMax) return false;
    return true;
  }

  reservar() {
    this.errorReserva = null;
    if (!localStorage.getItem('token')) {
      alert('Debes iniciar sesión para reservar');
      return;
    }
    if (!this.datosReservaValidos()) {
      alert('Completa fechas válidas y número de huéspedes');
      return;
    }
    const reserva = {
      accommodationId: this.alojamiento.id,
      checkIn: this.fechaLlegada,
      checkOut: this.fechaSalida,
      countRoommates: this.cantidadHuespedes
    };
    this.reservando = true;
    this.reservaService.crearReserva(reserva).subscribe({
      next: () => {
        this.reservando = false;
        alert('Reserva creada con éxito');
      },
      error: (err) => {
        this.reservando = false;
        const msg = err?.error?.message || err?.error?.error || err?.message || 'Error al reservar';
        this.errorReserva = msg;
        alert('No se pudo reservar: ' + msg);
      }
    });
  }


  regresar() {
    window.history.back();
  }

  agregarFavoritos() {
    if (!this.alojamiento?.id || this.favorito) return;
    this.favoriteService.addFavorite(this.alojamiento.id).subscribe({
      next: () => {
        this.favorito = true;
        alert('Añadido a favoritos');
      },
      error: err => alert('Error al guardar favorito: ' + (err.error?.message || err.message))
    });
  }

  private updateCapacidadMaxFromAlojamiento() {
    const raw = this.alojamiento?.capacidad;
    const cap = typeof raw === 'number' ? raw : parseInt(raw, 10);
    this.capacidadMax = Number.isFinite(cap) && cap > 0 ? cap : null;
    // Clamp el valor actual
    this.cantidadHuespedes = Math.max(1, Math.trunc(Number(this.cantidadHuespedes) || 1));
    if (this.capacidadMax !== null) {
      this.cantidadHuespedes = Math.min(this.cantidadHuespedes, this.capacidadMax);
    }
  }
}
