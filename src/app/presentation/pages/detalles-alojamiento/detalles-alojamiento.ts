import { Component } from '@angular/core';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';

@Component({
  selector: 'app-detalles-alojamiento',
  imports: [HeaderComponent,FooterComponent],
  templateUrl: './detalles-alojamiento.html',
  styleUrl: './detalles-alojamiento.scss'
})
export class DetallesAlojamientoPageComponent {

}
