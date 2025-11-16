import { Component } from '@angular/core';
import { RegistrationFormComponent } from '../../organisms/registration-form/registration-form';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [RegistrationFormComponent, HeaderComponent, FooterComponent],
  templateUrl: './register-page.html',
  styleUrls: ['./register-page.scss']
})
export class RegisterPageComponent {}
