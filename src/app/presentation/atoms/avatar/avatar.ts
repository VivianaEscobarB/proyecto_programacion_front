import { Component, Input } from '@angular/core';

export { };

@Component({
  selector: 'app-avatar',
  standalone: true,
  templateUrl: './avatar.html',
  styleUrl: './avatar.scss'
})
export class AvatarComponent {
  @Input() src: string = '';
}
