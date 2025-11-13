import { Component, Input, Signal, signal } from '@angular/core';

@Component({
  selector: 'app-star-rating',
  standalone: true,
  templateUrl: './star-rating.html',
  styleUrl: './star-rating.scss'
})
export class StarRatingComponent {
  @Input() calificacion: number = 0;
  stars: Signal<number[]> = signal([1,2,3,4,5]);
}

