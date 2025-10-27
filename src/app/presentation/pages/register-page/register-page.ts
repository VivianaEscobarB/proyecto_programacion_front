import { Component } from '@angular/core';
import { RegistrationFormComponent } from '../../organisms/registration-form/registration-form';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [RegistrationFormComponent],
  template: `
    <div class="register-page">
      <app-registration-form></app-registration-form>
    </div>
  `,
  styles: [`
    .register-page {
      min-height: 100vh;
      background-color: #F5F6F8;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  `]
})
export class RegisterPageComponent {}
