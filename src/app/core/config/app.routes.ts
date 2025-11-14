import { Routes } from '@angular/router';
import { RegisterPageComponent } from '../../presentation/pages/register-page/register-page';
import { LoginPageComponent } from '../../presentation/pages/login-page/login-page';
import { InicioPageComponent } from '../../presentation/pages/inicio/inicio';
import { CuentaPageComponent } from '../../presentation/pages/cuenta/cuenta';
import { DetallesAlojamientoPageComponent } from '../../presentation/pages/detalles-alojamiento/detalles-alojamiento';
import { AccommodationsPageComponent } from '../../presentation/pages/accommodations/accommodations';

export const AppRoutes: Routes = [
  { path: 'inicio', component: InicioPageComponent },
  { path: 'cuenta', component: CuentaPageComponent },
  { path: 'alojamientos', component: AccommodationsPageComponent },
  { path: 'alojamiento/detalles', component: DetallesAlojamientoPageComponent},
  { path: 'registro', component: RegisterPageComponent },
  { path: 'login', component: LoginPageComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }, // ruta por defecto
  { path: '**', redirectTo: '/login' } // rutas desconocidas
];
