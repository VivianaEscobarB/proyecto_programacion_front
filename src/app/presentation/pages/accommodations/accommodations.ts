import { Component, OnInit } from '@angular/core';
import { AccommodationService } from '../../../core/services/accommodation/accommodation';
import { Accommodation } from '../../../domain/entities/accommodation';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';
import { CommonModule } from '@angular/common';
import { AlojamientoCardComponent } from '../../organisms/alojamiento-card/alojamiento-card';
import { SearchBarComponent } from '../../atoms/search-bar/search-bar';

@Component({
  selector: 'app-alojamientos',
  standalone: true,
  templateUrl: './accommodations.html',
  styleUrls: ['./accommodations.scss'],
  imports: [HeaderComponent, FooterComponent, CommonModule, AlojamientoCardComponent, SearchBarComponent]
})
export class AccommodationsPageComponent implements OnInit {

  loading = true;
  alojamientos: Accommodation[] = [
    {
      nombre: 'Casa en San Andres',
      imagenUrl: '/assets/images/deptos/casa_con_plantas.jpg',
      precio: '$450.000 COP x día',
      estrellas: 5,
      id: 0,
      ciudad: '',
      departamento: ''
    },
    {
      nombre: 'Casa en Medellín',
      imagenUrl: '/assets/images/deptos/cocina_casa.jpg',
      precio: '$540.000 COP x día',
      estrellas: 4,
      id: 0,
      ciudad: '',
      departamento: ''
    }
  ];

  constructor(private accomodationService: AccommodationService) {}

  ngOnInit() {
    this.accomodationService.getAlojamientos().subscribe({
      next: (data) => {
        this.alojamientos = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  toDetallesAlojamiento(id: number) {
    // Navegar a la página de detalles del alojamiento con el ID proporcionado
    
  }

  
}
