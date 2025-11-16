import { Component } from '@angular/core';
import { AuthService } from '../../../../core/services/auth/auth';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ImgFallbackDirective } from '../../../atoms/img-fallback/img-fallback.directive';


@Component({
  selector: 'app-header',
  standalone: true,
  templateUrl: './header.html',
  styleUrls: ['./header.scss'],
  imports: [CommonModule, RouterModule, ImgFallbackDirective]
})
export class HeaderComponent {
  isLoggedIn = false;
  user: { email: string | null; role: string | null; imageUrl: string | null; userId: string | null } = {
    email: null,
    role: null,
    imageUrl: null,
    userId: null
  };

  constructor(private authService: AuthService, private router: Router) {}

  toLogin() {
    this.router.navigate(['/login']); // Redirige a la página de login
  }

  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.user = this.authService.getCurrentUser();
    this.authService.user$.subscribe(u => { this.user = u; });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']); // Redirige tras cerrar sesión
  }

  toAccount() {
    this.router.navigate(['/cuenta']); // Redirige a la página de cuenta
  }

  toMain() {
    this.router.navigate(['/inicio']); // Redirige a la página principal
  }

  toAccommodations() {
    this.router.navigate(['/alojamientos']); // Redirige a la página de alojamientos
  }

  toAboutUs() {
    this.router.navigate(['/nosotros']); // Redirige a la página de nosotros
  }

  toHostAccount() {
    this.router.navigate(['/cuenta/anfitrion']);
  }


}
