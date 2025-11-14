import { Component } from '@angular/core';
import {HeaderComponent} from '../../organisms/header/header/header';
import {FooterComponent} from '../../organisms/footer/footer/footer';

@Component({
  selector: 'app-nosotros',
  templateUrl: './nosotros.html',
  styleUrls: ['./nosotros.scss'],
  imports: [
    HeaderComponent,
    FooterComponent
  ]
})
export class NosotrosPageComponent {
  historia: string = `Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.`;

  mision: string = `Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.`;

  vision: string = `Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.`;

  imagenHistoria: string = 'assets/images/nosotros/historia.png';
  imagenMisionVision: string = 'assets/images/nosotros/misionyvision.png';
}
