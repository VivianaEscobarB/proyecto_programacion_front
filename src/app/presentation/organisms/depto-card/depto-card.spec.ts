import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeptoCard } from './depto-card';

describe('DeptoCard', () => {
  let component: DeptoCard;
  let fixture: ComponentFixture<DeptoCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeptoCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeptoCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
