import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CiudadCard } from './ciudad-card';

describe('CiudadCard', () => {
  let component: CiudadCard;
  let fixture: ComponentFixture<CiudadCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CiudadCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CiudadCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
