import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-depto-card',
  standalone: true,
  templateUrl: './depto-card.html',
  styleUrls: ['./depto-card.scss']
})
export class DeptoCardComponent {
  @Input() depto!: { nombre: string; imagenUrl: string };
}
