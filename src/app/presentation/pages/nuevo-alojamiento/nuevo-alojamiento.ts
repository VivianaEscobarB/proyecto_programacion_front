import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-nuevo-alojamiento',
  templateUrl: './nuevo-alojamiento.html',
  imports: [
    FormsModule
  ],
  styleUrls: ['./nuevo-alojamiento.scss']
})
export class NuevoAlojamientoComponent {
  // Agregar lógica para manejar el formulario
  guardarAlojamiento() {
    console.log('Alojamiento guardado');
  }
}
