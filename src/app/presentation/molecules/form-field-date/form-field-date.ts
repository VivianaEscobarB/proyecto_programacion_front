import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { DatePickerComponent } from '../../atoms/date-picker/date-picker';
import { LabelComponent } from '../../atoms/label/label';

@Component({
  selector: 'app-form-field-date',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePickerComponent, LabelComponent],
  templateUrl: './form-field-date.html',
  styleUrls: ['./form-field-date.css']
})
export class FormFieldDate {
  @Input() label: string = '';
  @Input() control!: FormControl;
  @Input() required: boolean = false;

  get hasError(): boolean {
    return !!(this.control && this.control.invalid && this.control.touched);
  }
  get errorMessage(): string {
    if (!this.hasError) return '';
    if (this.control.errors?.['required']) return 'Campo requerido';
    if (this.control.errors?.['underage']) return 'Debes ser mayor de edad';
    return 'Fecha inválida';
  }
}
