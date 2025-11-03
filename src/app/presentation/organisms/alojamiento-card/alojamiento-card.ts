import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-alojamiento-card',
  standalone: true,
  imports: [
    CommonModule, // ¡Muy importante!
    // ...otros componentes
  ],
  templateUrl: './alojamiento-card.html',
  styleUrls: ['./alojamiento-card.scss']
})
export class AlojamientoCardComponent {
  @Input() alojamiento!: { nombre: string; imagenUrl: string; precio: string; estrellas: number };
}
