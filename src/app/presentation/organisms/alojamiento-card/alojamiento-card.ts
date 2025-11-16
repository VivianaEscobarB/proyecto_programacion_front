import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Accommodation } from '../../../domain/entities/accommodation';
import { Router, RouterModule } from '@angular/router';
import { ImgFallbackDirective } from '../../atoms/img-fallback/img-fallback.directive';

@Component({
  selector: 'app-alojamiento-card',
  standalone: true,
  imports: [
    CommonModule, // ¡Muy importante!
    RouterModule,
    ImgFallbackDirective
    // ...otros componentes
  ],
  templateUrl: './alojamiento-card.html',
  styleUrls: ['./alojamiento-card.scss']
})
export class AlojamientoCardComponent {
  @Input() alojamiento!: Accommodation;
  @Output() clickAlojamiento = new EventEmitter<void>();

  constructor(private router: Router) {}

  onCardClick() {
    // Emitimos el evento (por si el padre lo usa)
    this.clickAlojamiento.emit();
    // Navegación al detalle si hay id
    if (this.alojamiento?.id != null) {
      this.router.navigate(['/alojamiento/detalles', this.alojamiento.id]);
    }
  }
}
