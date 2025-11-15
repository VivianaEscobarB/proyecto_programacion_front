import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors, FormControl } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { FormFieldComponent } from '../../molecules/form-field/form-field';
import { FormFieldSelectComponent } from '../../molecules/form-field-select/form-field-select';
import { FormFieldDate} from '../../molecules/form-field-date/form-field-date';
import { ButtonComponent } from '../../atoms/button/button';
import { AuthService } from '../../../core/services/auth/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormFieldComponent,
    FormFieldSelectComponent,
    FormFieldDate,
    ButtonComponent,
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

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

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
    const form = this.registrationForm.value;
    // Construir payload según RegisterUserCommand
    const payload = {
      firstName: form.nombres,
      lastName: form.apellidos,
      email: form.email,
      password: form.password,
      phoneNumber: form.telefono,
      identification: form.identificacion,
      dateOfBirth: form.fechaNacimiento,
      nationality: form.nacionalidad,
      country: form.pais,
      department: form.departamento,
      city: form.ciudad,
      address: form.direccion,
      roles: ['CLIENTE']
    };
    this.authService.register(payload).subscribe({
      next: () => {
        alert('Registro exitoso!');
        // Login automático
        this.authService.login({ email: payload.email, password: payload.password }).subscribe({
          next: (response) => {
            localStorage.setItem('token', response.token);
            localStorage.setItem('userId', response.userId);
            localStorage.setItem('userEmail', response.email);
            localStorage.setItem('userRole', response.roles?.[0] || 'CLIENTE');
            const url = response.urlAccountPhoto ? response.urlAccountPhoto.replace('/view', '/preview') : '/assets/images/user_image_default.png';
            localStorage.setItem('userImage', url);
            this.authService.setUserImage(url);
            this.isSubmitting = false;
            this.router.navigate(['/inicio']);
          },
          error: (err) => {
            console.error('Login post-registro falló', err);
            this.isSubmitting = false;
            this.router.navigate(['/login']);
          }
        });
      },
      error: (err) => {
        console.error('Error en registro', err);
        alert(err.error?.error || err.error?.message || 'Error en el registro');
        this.isSubmitting = false;
      }
    });
  }
}
