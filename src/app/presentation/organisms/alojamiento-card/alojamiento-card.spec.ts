import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlojamientoCard } from './alojamiento-card';

describe('AlojamientoCard', () => {
  let component: AlojamientoCard;
  let fixture: ComponentFixture<AlojamientoCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlojamientoCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlojamientoCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
