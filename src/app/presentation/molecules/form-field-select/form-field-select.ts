import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { SelectDropdownComponent, SelectOption } from '../../atoms/select-dropdown/select-dropdown';
import { LabelComponent } from '../../atoms/label/label';

@Component({
  selector: 'app-form-field-select',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SelectDropdownComponent, LabelComponent],
  templateUrl: './form-field-select.html',
  styleUrls: ['./form-field-select.scss']
})
export class FormFieldSelectComponent {
  @Input() label: string = '';
  @Input() options: SelectOption[] = [];
  @Input() control!: FormControl;
  @Input() required: boolean = false;

  get hasError(): boolean {
    return !!(this.control && this.control.invalid && this.control.touched);
  }
  get errorMessage(): string {
    if (!this.hasError) return '';
    if (this.control.errors?.['required']) return 'Selecciona una opción';
    return 'Campo inválido';
  }
}
