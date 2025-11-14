import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './presentation/organisms/header/header/header';
import { FooterComponent } from './presentation/organisms/footer/footer/footer';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  template: `<router-outlet></router-outlet>`,
  styleUrls: ['./app.css']
})
export class App {}
