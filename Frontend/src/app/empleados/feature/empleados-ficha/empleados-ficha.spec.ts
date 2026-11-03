import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmpleadosFicha } from './empleados-ficha';

describe('EmpleadosFicha', () => {
  let component: EmpleadosFicha;
  let fixture: ComponentFixture<EmpleadosFicha>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpleadosFicha],
    }).compileComponents();

    fixture = TestBed.createComponent(EmpleadosFicha);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
