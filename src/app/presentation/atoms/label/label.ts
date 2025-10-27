import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-label',
  standalone: true,
  imports: [CommonModule],
  template: `
    <label [for]="for" class="form-label">
      {{ text }}
      <span *ngIf="required" class="required-indicator">*</span>
    </label>
  `,
  styles: [`
    .form-label {
      display: inline-block;
      font-size: 0.875rem;
      font-weight: 500;
      color: #374151;
      margin-bottom: 0.25rem;
      
      .required-indicator {
        color: #EF4444;
        margin-left: 0.25rem;
      }
    }
    
    @media screen and (max-width: 768px) {
      .form-label {
        font-size: 0.9375rem;
      }
    }
  `]
})
export class LabelComponent {
  @Input() for: string = '';
  @Input() text: string = '';
  @Input() required: boolean = false;
}
