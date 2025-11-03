import { Routes } from '@angular/router';
import { RegisterPageComponent } from '../../presentation/pages/register-page/register-page';
import { LoginPageComponent } from '../../presentation/pages/login-page/login-page';
import { InicioPageComponent } from '../../presentation/pages/inicio/inicio';

export const AppRoutes: Routes = [
  { path: 'inicio', component: InicioPageComponent },
  { path: 'registro', component: RegisterPageComponent },
  { path: 'login', component: LoginPageComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }, // ruta por defecto
  { path: '**', redirectTo: '/login' } // rutas desconocidas
];
