import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors, FormControl } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { FormFieldComponent } from '../../molecules/form-field/form-field';
import { FormFieldSelectComponent } from '../../molecules/form-field-select/form-field-select';
import { FormFieldDate} from '../../molecules/form-field-date/form-field-date';
import { ButtonComponent } from '../../atoms/button/button';

@Component({
  selector: 'app-registration-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormFieldComponent,
    FormFieldSelectComponent,
    FormFieldDate,
    ButtonComponent
  ],
  templateUrl: './registration-form.html',
  styleUrls: ['./registration-form.scss']
})
export class RegistrationFormComponent implements OnInit {
  registrationForm!: FormGroup;
  isSubmitting = false;

  paises = [
    { value: 'colombia', label: 'Colombia' },
    { value: 'mexico', label: 'México' },
    { value: 'argentina', label: 'Argentina' },
    { value: 'chile', label: 'Chile' }
  ];
  nacionalidades = [
    { value: 'colombiana', label: 'Colombiana' },
    { value: 'mexicana', label: 'Mexicana' },
    { value: 'argentina', label: 'Argentina' }
  ];
  departamentos = [
    { value: 'antioquia', label: 'Antioquia' },
    { value: 'cundinamarca', label: 'Cundinamarca' }
  ];
  ciudades = [
    { value: 'medellin', label: 'Medellín' },
    { value: 'bogota', label: 'Bogotá' }
  ];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.registrationForm = this.fb.group({
      nombres: ['', [Validators.required, Validators.minLength(2)]],
      apellidos: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      identificacion: ['', [Validators.required]],
      nacionalidad: ['', [Validators.required]],
      fechaNacimiento: ['', [Validators.required, this.ageValidator]],
      telefono: ['', [Validators.required]],
      pais: ['', [Validators.required]],
      departamento: ['', [Validators.required]],
      ciudad: ['', [Validators.required]],
      direccion: ['', [Validators.required, Validators.minLength(6)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  getControl(name: string): FormControl {
    return this.registrationForm.get(name) as FormControl;
  }

  passwordMatchValidator(form: AbstractControl): ValidationErrors | null {
    const pwd = form.get('password')?.value;
    const confirm = form.get('confirmPassword')?.value;
    return pwd === confirm ? null : { passwordMismatch: true };
  }

  ageValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    const birthDate = new Date(control.value);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const m = today.getMonth() - birthDate.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) age--;
    return age >= 18 ? null : { underage: true };
  }

  submitForm(): void {
    if (this.registrationForm.invalid) {
      this.registrationForm.markAllAsTouched();
      return;
    }
    this.isSubmitting = true;
    setTimeout(() => {
      alert('Registro exitoso!');
      this.isSubmitting = false;
    }, 1500);
  }
}
