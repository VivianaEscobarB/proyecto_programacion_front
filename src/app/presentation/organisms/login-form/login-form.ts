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
      next: () => {
        // AuthService ya persistió datos y actualizó BehaviorSubject
        this.isSubmitting = false;
        this.router.navigate(['/inicio']);
      },
      error: (err) => {
        const msg = err?.error?.message || err?.error?.error || err?.message || 'Error de autenticación';
        this.error = msg;
        this.isSubmitting = false;
      }
    });
  }


  goToRegistro(): void {
    this.router.navigate(['/registro']);
  }
}
