import { Component, OnInit } from '@angular/core';

import { ReservaService } from '../../../core/services/reserva/reserva.service';
import {ButtonComponent} from '../../atoms/button/button';
import {LabelComponent} from '../../atoms/label/label';
import {HeaderComponent} from '../../organisms/header/header/header';
import {FormsModule} from '@angular/forms';
import {CurrencyPipe} from '@angular/common';
import {FooterComponent} from '../../organisms/footer/footer/footer';
import { StarRatingComponent } from '../../atoms/star-rating/star-rating';
import { AvatarComponent } from '../../atoms/avatar/avatar';

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
    AvatarComponent
  ],
  styleUrls: ['./detalles-alojamiento.scss']
})
export class DetallesAlojamientoPageComponent implements OnInit {
  fechaLlegada: string = '';
  fechaSalida: string = '';
  cantidadHuespedes: number = 4;
  total: number = 4500000;
  alojamiento: any = {
    descripcion: "Esta finca ofrece una espectacular piscina al aire libre rodeada de cafetales, perfecta para relajarse y disfrutar de la naturaleza en un entorno tranquilo. Con impresionantes paisajes montañosos, amplias zonas verdes y cómodas áreas de descanso, es el lugar ideal para desconectarse, descansar y deleitarse con la belleza del campo.",
    direccion: "Vereda El Caimo",
    ciudad: "Armenia",
    departamento: "Quindío",
    capacidad: "8 habitaciones individuales",
    calificacion: 4,
    precio: 450000,
    anfitrion: "Jhoan Sebastian Urrea Sanchez",
    anfitrionImagen: "assets/images/avatar/avatar.png",
    imagen: "assets/images/deptos/finca_robles.png"
  };

  constructor(private reservaService: ReservaService) {}

  ngOnInit(): void {
    // Puedes cargar datos adicionales aquí si se obtienen desde un API
  }

  reservar() {
    const reserva = {
      accommodationId: 1, // Cambia esto por el ID real del alojamiento si lo tienes
      checkIn: this.fechaLlegada,   // formato 'YYYY-MM-DD'
      checkOut: this.fechaSalida,   // formato 'YYYY-MM-DD'
      countRoommates: this.cantidadHuespedes
    };
    this.reservaService.crearReserva(reserva).subscribe({
      next: () => alert('Reserva realizada correctamente'),
      error: (err: { error: { message: any; }; message: any; }) => alert('Error al reservar: ' + (err.error?.message || err.message))
    });
  }
}
