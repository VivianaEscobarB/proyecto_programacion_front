import { Component, OnInit } from '@angular/core';
import { DeptoCardComponent } from '../../organisms/depto-card/depto-card';
import { CiudadCardComponent } from '../../organisms/ciudad-card/ciudad-card';
import { AlojamientoCardComponent } from '../../organisms/alojamiento-card/alojamiento-card';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';
import { CommonModule } from '@angular/common';
import { Accommodation } from '../../../domain/entities/accommodation';
import { AccommodationService } from '../../../core/services/accommodation/accommodation';
import { resolveAccommodationImage } from '../../../domain/entities/accommodation-images';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    FooterComponent,
    DeptoCardComponent,
    CiudadCardComponent,
    AlojamientoCardComponent
  ],
  templateUrl: './inicio.html',
  styleUrls: ['./inicio.scss']
})
export class InicioPageComponent implements OnInit {
  // Datos dummy
  departamentos = [
    { nombre: 'Quindío', imagenUrl: '/assets/images/ciudades/quindio.webp' },
    { nombre: 'Risaralda', imagenUrl: '/assets/images/ciudades/risaralda.webp' },
    { nombre: 'Antioquia', imagenUrl: '/assets/images/ciudades/antioquia.jpg' },
    { nombre: 'Caldas', imagenUrl: '/assets/images/ciudades/caldas.jpeg' },
    { nombre: 'Manizales', imagenUrl: '/assets/images/ciudades/manizales.jpg' }
  ];

  ciudades = [
    { nombre: 'Filandia', imagenUrl: '/assets/images/ciudades/filandia.webp' },
    { nombre: 'Medellin', imagenUrl: '/assets/images/ciudades/medellin.png' },
    { nombre: 'Bogotá', imagenUrl: '/assets/images/ciudades/bogota.jpg' },
    { nombre: 'Pereira', imagenUrl: '/assets/images/ciudades/pereira.jpg' },
    { nombre: 'Boyacá', imagenUrl: '/assets/images/ciudades/boyaca.jpg' }
  ];

  alojamientos: Accommodation[] = [];
  loadingFeatured = true;
  errorFeatured?: string;

  constructor(private accommodationService: AccommodationService) {}

  ngOnInit(): void {
    this.loadFeatured();
  }

  private loadFeatured() {
    this.accommodationService.getDestacados(4).subscribe({
      next: (list) => {
        this.alojamientos = list;
        this.loadingFeatured = false;
      },
      error: (err) => {
        console.error('[Inicio] Error cargando alojamientos', err);
        this.errorFeatured = 'No se pudieron cargar alojamientos destacados';
        this.alojamientos = [
          { id: 1, nombre: 'Casa Campestre Filandia', imagenUrl: '/assets/images/deptos/casa_con_plantas.jpg', precio: 450000, estrellas: 5 },
          { id: 2, nombre: 'Finca Los Robles', imagenUrl: '/assets/images/deptos/finca_robles.png', precio: 520000, estrellas: 4 }
        ];
        this.loadingFeatured = false;
      }
    });
  }
}
