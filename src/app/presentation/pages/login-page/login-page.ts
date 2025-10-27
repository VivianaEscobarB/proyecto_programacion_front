import { Component } from '@angular/core';
import { LoginFormComponent } from '../../organisms/login-form/login-form';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [LoginFormComponent],
  template: `
    <div class="login-page">
      <app-login-form></app-login-form>
    </div>
  `,
  styles: [`
    .login-page {
      min-height: 100vh;
      background-color: #F5F6F8;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  `]
})
export class LoginPageComponent {}
