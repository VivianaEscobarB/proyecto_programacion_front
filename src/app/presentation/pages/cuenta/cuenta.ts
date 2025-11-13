import { Component, OnInit } from '@angular/core';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';
import { UserService } from '../../../core/services/user/user';

@Component({
  selector: 'cuenta',
  standalone: true,
  templateUrl: './cuenta.html',
  styleUrls: ['./cuenta.scss'],
  imports: [HeaderComponent, FooterComponent]
})
export class CuentaPageComponent implements OnInit {
  userPhotoUrl = localStorage.getItem('userImage') || '/assets/images/user_image_default.png';
  userRole = localStorage.getItem('userRole') || 'Rol';
  userFullName = '';
  userData = {
    fechaNacimiento: '',
    email: localStorage.getItem('userEmail') || '',
    telefono: '',
    direccion: ''
  };

  constructor(private userService: UserService) {}

  ngOnInit() {
  const userId = localStorage.getItem('userId'); 
  if (userId) {
    this.userService.getProfileById(userId).subscribe({
      next: (profile) => {
        this.userFullName = `${profile.firstName} ${profile.lastName}`;
        this.userData.fechaNacimiento = profile.dayOfBirth || '';
        this.userData.telefono = profile.phoneNumber || '';
        this.userData.direccion = profile.homeAddress || '';
        if (profile.urlAccountPhoto) {
          this.userPhotoUrl = profile.urlAccountPhoto;
        }
        if (profile.roles && profile.roles.length > 0) {
          this.userRole = profile.roles[0];
        }
      },
      error: (err) => {
        console.error('Error al cargar perfil:', err);
      }
    });
  }
}

}
