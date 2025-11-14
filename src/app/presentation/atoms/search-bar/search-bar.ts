import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-bar.html',
  styleUrls: ['./search-bar.scss']
})
export class SearchBarComponent {
  query: string = '';
  @Output() search = new EventEmitter<string>();
  emitSearch() {
    this.search.emit(this.query);
  }
}
