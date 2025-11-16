import { Component, OnInit } from '@angular/core';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';
import { UserService } from '../../../core/services/user/user';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth/auth';
import { ReservaService } from '../../../core/services/reserva/reserva.service';

@Component({
  selector: 'cuenta',
  standalone: true,
  templateUrl: './cuenta.html',
  styleUrls: ['./cuenta.scss'],
  imports: [HeaderComponent, FooterComponent, CommonModule, FormsModule]
})
export class CuentaPageComponent implements OnInit {
  userPhotoUrl = localStorage.getItem('userImage') || '/assets/images/user_image_default.png';
  userRole = localStorage.getItem('userRole') || 'Rol';
  userFullName = '';
  newPhotoUrl = '';
  selectedFile: File | null = null;
  userData = {
    fechaNacimiento: 'N/D',
    email: localStorage.getItem('userEmail') || 'N/D',
    telefono: 'N/D',
    direccion: 'N/D'
  };

  // Estado de edición para inputs
  edit = {
    fechaNacimientoISO: '',
    telefono: '',
    direccion: ''
  };

  misReservas: any[] = [];
  cargandoReservas = false;

  constructor(private userService: UserService, private authService: AuthService, private reservaService: ReservaService) {}

  ngOnInit() {
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.userService.getProfileById(userId).subscribe({
        next: (profile) => {
          this.userFullName = [profile.firstName, profile.lastName].filter(Boolean).join(' ').trim();
          this.userData.fechaNacimiento = profile.dayOfBirth ? this.formatDate(profile.dayOfBirth) : 'N/D';
          this.userData.telefono = profile.phoneNumber || 'N/D';
          this.userData.direccion = profile.homeAddress || 'N/D';
          this.userData.email = profile.email || this.userData.email;

          // Inicializar campos de edición con valores actuales (en formato apropiado)
          this.edit.fechaNacimientoISO = profile.dayOfBirth || '';
          this.edit.telefono = profile.phoneNumber || '';
          this.edit.direccion = profile.homeAddress || '';

          if (profile.urlAccountPhoto) {
            this.userPhotoUrl = profile.urlAccountPhoto;
            localStorage.setItem('userImage', this.userPhotoUrl);
            this.authService.setUserImage(this.userPhotoUrl);
          }
          if (profile.roles && profile.roles.length > 0) {
            this.userRole = profile.roles[0];
            localStorage.setItem('userRole', this.userRole);
          }
        },
        error: (err) => { console.error('Error al cargar perfil:', err); }
      });
      this.cargarMisReservas();
    }
  }

  cargarMisReservas() {
    this.cargandoReservas = true;
    this.reservaService.listarMisReservas().subscribe({
      next: data => {
        // Enriquecer mostrando título y total si vienen en DTO; si no, se deja id
        this.misReservas = data.map(r => ({
          id: r.id,
          alojamientoTitulo: r.accommodationTitle || ('Alojamiento ' + r.accommodationId),
          checkIn: r.checkIn,
            checkOut: r.checkOut,
          countRoommates: r.countRoommates,
          stateName: r.stateName,
          estimatedTotal: r.estimatedTotal || 0
        }));
        this.cargandoReservas = false;
      },
      error: _ => { this.cargandoReservas = false; }
    });
  }

  cancelarReserva(id: number) {
    if (!confirm('¿Cancelar esta reserva?')) return;
    this.reservaService.cancelarReserva(id).subscribe({
      next: () => {
        this.misReservas = this.misReservas.map(r => r.id === id ? { ...r, stateName: 'Cancelada' } : r);
        alert('Reserva cancelada');
      },
      error: err => alert('No se pudo cancelar: ' + (err.error?.message || err.message))
    });
  }

  eliminarReserva(id: number) {
    if (!confirm('¿Eliminar definitivamente esta reserva?')) return;
    this.reservaService.eliminarReserva(id).subscribe({
      next: () => {
        this.misReservas = this.misReservas.filter(r => r.id !== id);
        alert('Reserva eliminada');
      },
      error: err => alert('No se pudo eliminar: ' + (err.error?.message || err.message))
    });
  }

  guardarFoto() {
    const userId = localStorage.getItem('userId');
    if (!userId) { alert('Debes iniciar sesión'); return; }
    const url = (this.newPhotoUrl || '').trim();
    if (!url) { alert('Ingresa la URL de la nueva foto'); return; }
    this.userService.updatePhoto(userId, url).subscribe({
      next: (res) => {
        const nuevaUrl = res?.urlAccountPhoto || url;
        this.userPhotoUrl = nuevaUrl;
        localStorage.setItem('userImage', nuevaUrl);
        this.authService.setUserImage(nuevaUrl);
        alert('Foto actualizada');
      },
      error: (err) => {
        alert('No se pudo actualizar la foto: ' + (err.error?.error || err.error?.message || err.message));
      }
    });
  }

  onFileSelected(ev: Event) {
    const input = ev.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  subirFoto() {
    const userId = localStorage.getItem('userId');
    if (!userId || !this.selectedFile) { return; }
    this.userService.uploadPhotoFile(userId, this.selectedFile).subscribe({
      next: (res: any) => {
        const nuevaUrl = res?.urlAccountPhoto;
        if (nuevaUrl) {
          this.userPhotoUrl = nuevaUrl;
          localStorage.setItem('userImage', nuevaUrl);
          this.authService.setUserImage(nuevaUrl);
        }
        this.selectedFile = null;
        alert('Foto actualizada');
      },
      error: (err) => alert('No se pudo subir la foto: ' + (err.error?.error || err.message))
    });
  }

  updateUserData() {
    const userId = localStorage.getItem('userId');
    if (!userId) { alert('Debes iniciar sesión'); return; }
    const payload: any = {};
    if (this.edit.telefono !== undefined) payload.phoneNumber = this.edit.telefono.trim();
    if (this.edit.direccion !== undefined) payload.homeAddress = this.edit.direccion.trim();
    if ((this.edit.fechaNacimientoISO || '').trim()) payload.dayOfBirth = this.edit.fechaNacimientoISO.trim();

    this.userService.updateProfile(userId, payload).subscribe({
      next: (res) => {
        // Refrescar UI con lo devuelto por el backend
        const d = res?.dayOfBirth;
        this.userData.fechaNacimiento = d ? this.formatDate(typeof d === 'string' ? d : '') : (this.edit.fechaNacimientoISO ? this.formatDate(this.edit.fechaNacimientoISO) : 'N/D');
        this.userData.telefono = res?.phoneNumber || this.edit.telefono || 'N/D';
        this.userData.direccion = res?.homeAddress || this.edit.direccion || 'N/D';
        alert('Datos actualizados');
      },
      error: (err) => {
        alert('No se pudo actualizar: ' + (err.error?.error || err.error?.message || err.message));
      }
    });
  }

  private formatDate(value: string | Date): string {
    // Esperamos LocalDate en formato 'YYYY-MM-DD' o Date
    const d = typeof value === 'string' ? value : value?.toISOString().slice(0, 10);
    if (!d) return 'N/D';
    const [y, m, day] = d.split('-');
    if (!y || !m || !day) return d;
    return `${day.padStart(2,'0')}/${m.padStart(2,'0')}/${y}`;
  }
}
