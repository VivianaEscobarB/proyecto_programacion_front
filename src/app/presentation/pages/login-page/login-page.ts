import { Component } from '@angular/core';
import { LoginFormComponent } from '../../organisms/login-form/login-form';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [LoginFormComponent,HeaderComponent,FooterComponent],
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.scss']
})
export class LoginPageComponent {}
