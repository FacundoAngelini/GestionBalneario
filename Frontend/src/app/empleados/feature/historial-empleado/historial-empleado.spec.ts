import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HistorialEmpleado } from './historial-empleado';

describe('HistorialEmpleado', () => {
  let component: HistorialEmpleado;
  let fixture: ComponentFixture<HistorialEmpleado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistorialEmpleado],
    }).compileComponents();

    fixture = TestBed.createComponent(HistorialEmpleado);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
