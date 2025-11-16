import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { InputFieldComponent } from '../../atoms/input-field/input-field';
import { LabelComponent } from '../../atoms/label/label';

@Component({
  selector: 'app-form-field',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, InputFieldComponent, LabelComponent],
  templateUrl: './form-field.html',
  styleUrls: ['./form-field.scss']
})
export class FormFieldComponent {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() type: string = 'text';
  @Input() control!: FormControl;
  @Input() required: boolean = false;

  get hasError(): boolean {
    return !!(this.control && this.control.invalid && this.control.touched);
  }

  get errorMessage(): string {
    if (!this.hasError) return '';
    if (this.control.errors?.['required']) return 'Campo requerido';
    if (this.control.errors?.['email']) return 'Correo inválido';
    if (this.control.errors?.['minlength']) return `Mínimo ${this.control.errors['minlength'].requiredLength} caracteres`;
    if (this.control.errors?.['maxlength']) return `Máximo ${this.control.errors['maxlength'].requiredLength} caracteres`;
    if (this.control.errors?.['pattern']) return 'Formato inválido';
    return 'Campo inválido';
  }
}
