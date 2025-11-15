import { Component, OnInit } from '@angular/core';
import { AccommodationService } from '../../../core/services/accommodation/accommodation';
import { Accommodation } from '../../../domain/entities/accommodation';
import { HeaderComponent } from '../../organisms/header/header/header';
import { FooterComponent } from '../../organisms/footer/footer/footer';
import { CommonModule } from '@angular/common';
import { AlojamientoCardComponent } from '../../organisms/alojamiento-card/alojamiento-card';
import { SearchBarComponent } from '../../atoms/search-bar/search-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-alojamientos',
  standalone: true,
  templateUrl: './accommodations.html',
  styleUrls: ['./accommodations.scss'],
  imports: [HeaderComponent, FooterComponent, CommonModule, AlojamientoCardComponent, SearchBarComponent]
})
export class AccommodationsPageComponent implements OnInit {

  loading = true;
  alojamientos: Accommodation[] = [];
  error?: string;
  page = 0;
  pageSize = 8;
  total = 0;
  get totalPages() { return Math.max(1, Math.ceil(this.total / this.pageSize)); }

  constructor(private accomodationService: AccommodationService, private router: Router) {}

  ngOnInit() {
    this.loadPage();
  }

  private loadPage() {
    this.loading = true;
    this.accomodationService.getAlojamientosPaginados(this.page, this.pageSize).subscribe({
      next: (res) => {
        this.alojamientos = res.content;
        this.total = res.totalElements;
        this.loading = false;
      },
      error: (err) => {
        console.error('[Accommodations] Error cargando alojamientos', err);
        this.error = 'No se pudieron cargar los alojamientos';
        this.loading = false;
      }
    });
  }

  prevPage() { if (this.page > 0) { this.page--; this.loadPage(); } }
  nextPage() { if (this.page + 1 < this.totalPages) { this.page++; this.loadPage(); } }

  navigateToDetails(id: number) {
    const aloj = this.alojamientos.find(a => a.id === id);
    this.router.navigate([`/alojamiento/detalles/${id}`], { state: { alojamiento: aloj } });
  }

}
