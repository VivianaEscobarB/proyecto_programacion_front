import { Component } from '@angular/core';
import { DeptoCardComponent } from '../../organisms/depto-card/depto-card';
import { CiudadCardComponent } from '../../organisms/ciudad-card/ciudad-card';
import { AlojamientoCardComponent } from '../../organisms/alojamiento-card/alojamiento-card';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';
import { CommonModule } from '@angular/common';

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
export class InicioPageComponent {
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

  alojamientos = [
    {
      nombre: 'Casa en San Andres',
      imagenUrl: '/assets/images/deptos/casa_con_plantas.jpg',
      precio: '$450.000 COP x día',
      estrellas: 5
    },
    {
      nombre: 'Casa en Medellín',
      imagenUrl: '/assets/images/deptos/cocina_casa.jpg',
      precio: '$540.000 COP x día',
      estrellas: 4
    }
  ];
}
