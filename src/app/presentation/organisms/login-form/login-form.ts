import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth/auth';
import { FormFieldComponent } from '../../molecules/form-field/form-field';
import { ButtonComponent } from '../../atoms/button/button';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormFieldComponent,
    ButtonComponent
  ],
  templateUrl: './login-form.html',
  styleUrls: ['./login-form.scss']
})
export class LoginFormComponent {
  loginForm: FormGroup;
  isSubmitting = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    // Inicializa aquí después de inyectar fb
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  getControl(name: string): FormControl {
    return this.loginForm.get(name) as FormControl;
  }

  submitForm(): void {
  if (this.loginForm.invalid) {
    this.loginForm.markAllAsTouched();
    return;
  }
  this.isSubmitting = true;
  this.error = null;
  this.authService.login(this.loginForm.value).subscribe({
    next: (response) => {
    localStorage.setItem('token', response.token);
    localStorage.setItem('userId', response.userId);
    localStorage.setItem('userEmail', response.email);
    localStorage.setItem('userRole', response.roles[0]);
    localStorage.setItem('userImage', response.urlAccountPhoto.replace('/view', '/preview')); // Ajuste Google Drive
    // Navegar a inicio, o lo que desees

    
      console.log('Respuesta de la API:', response); // Aquí se muestra en la consola
      this.isSubmitting = false;
      // Redirecciona o realiza alguna acción adicional si quieres
      this.router.navigate(['/inicio']); // Redirige al dashboard
    },
    error: (err) => {
      console.log('Error en la API:', err);  // También en consola
      this.error = err.error?.message ?? 'Error de autenticación';
      this.isSubmitting = false;
    }
  });
}


  goToRegistro(): void {
    this.router.navigate(['/registro']);
  }
}
