import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-ciudad-card',
  standalone: true,
  templateUrl: './ciudad-card.html',
  styleUrls: ['./ciudad-card.scss']
})
export class CiudadCardComponent {
  @Input() ciudad!: { nombre: string; imagenUrl: string };
}
