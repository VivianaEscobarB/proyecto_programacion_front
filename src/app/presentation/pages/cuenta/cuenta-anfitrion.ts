import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';
import { UserService } from '../../../core/services/user/user';
import { HostReservationsService, HostReservationItem } from '../../../core/services/reserva/host-reservations.service';

@Component({
  selector: 'cuenta-anfitrion',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FooterComponent],
  templateUrl: './cuenta-anfitrion.html',
  styleUrls: ['./cuenta.scss']
})
export class CuentaAnfitrionPageComponent implements OnInit {
  user: any = {};
  reservas: HostReservationItem[] = [];
  cargando = false;
  estadoFiltro: string = '';
  page = 0;
  size = 10;
  mensaje: string | null = null;

  constructor(private userService: UserService, private hostSvc: HostReservationsService) {}

  ngOnInit(): void {
    const userId = localStorage.getItem('userId');
    if (userId) {
      this.userService.getProfileById(userId).subscribe(p => this.user = p);
      this.loadReservations();
    }
  }

  private show(msg: string) {
    this.mensaje = msg;
    setTimeout(() => this.mensaje = null, 3500);
  }

  loadReservations() {
    this.cargando = true;
    this.hostSvc.listMineFiltered(this.estadoFiltro, this.page, this.size).subscribe({
      next: r => { this.reservas = r; this.cargando = false; },
      error: err => { this.cargando = false; this.show('Error cargando reservas'); }
    });
  }

  aprobar(id: number) {
    if (!confirm('¿Aprobar reserva #' + id + '?')) return;
    this.hostSvc.approve(id).subscribe({
      next: _ => { this.show('Reserva aprobada'); this.loadReservations(); },
      error: err => this.show('No se pudo aprobar')
    });
  }

  rechazar(id: number) {
    if (!confirm('¿Rechazar reserva #' + id + '?')) return;
    this.hostSvc.reject(id).subscribe({
      next: _ => { this.show('Reserva rechazada'); this.loadReservations(); },
      error: err => this.show('No se pudo rechazar')
    });
  }

  finalizar(id: number) {
    if (!confirm('¿Finalizar reserva #' + id + '?')) return;
    this.hostSvc.finalize(id).subscribe({
      next: _ => { this.show('Reserva finalizada'); this.loadReservations(); },
      error: err => this.show('No se pudo finalizar: ' + (err.error?.message || err.message))
    });
  }

  eliminar(id: number) {
    if (!confirm('¿Eliminar reserva #' + id + ' (no se podrá recuperar)?')) return;
    this.hostSvc.delete(id).subscribe({
      next: _ => { this.show('Reserva eliminada'); this.loadReservations(); },
      error: err => this.show('No se pudo eliminar: ' + (err.error?.message || err.message))
    });
  }

  cambiarFiltroEstado(estado: string) {
    this.estadoFiltro = estado;
    this.page = 0;
    this.loadReservations();
  }

  siguiente() { this.page++; this.loadReservations(); }
  anterior() { if (this.page>0) { this.page--; this.loadReservations(); } }
}
