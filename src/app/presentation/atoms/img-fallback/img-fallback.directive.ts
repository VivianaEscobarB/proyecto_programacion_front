import { Directive, Input, HostListener, ElementRef } from '@angular/core';

@Directive({
  selector: 'img[fallbackSrc]',
  standalone: true
})
export class ImgFallbackDirective {
  @Input() fallbackSrc: string = '/assets/images/deptos/finca_robles.png';
  private tried = false;

  constructor(private el: ElementRef<HTMLImageElement>) {}

  @HostListener('error') onError() {
    if (this.tried) return;
    this.tried = true;
    const img: HTMLImageElement = this.el.nativeElement;
    img.src = this.fallbackSrc;
  }
}

