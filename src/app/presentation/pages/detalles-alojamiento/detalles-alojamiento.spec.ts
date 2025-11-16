import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetallesAlojamientoPageComponent } from './detalles-alojamiento';

describe('DetallesAlojamiento', () => {
  let component: DetallesAlojamientoPageComponent;
  let fixture: ComponentFixture<DetallesAlojamientoPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetallesAlojamientoPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetallesAlojamientoPageComponent);

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
