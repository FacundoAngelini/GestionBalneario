import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReservasLayout } from './reservas-layout';

describe('ReservasLayout', () => {
  let component: ReservasLayout;
  let fixture: ComponentFixture<ReservasLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservasLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(ReservasLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
