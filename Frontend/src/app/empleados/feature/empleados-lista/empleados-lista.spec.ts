import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmpleadosLista } from './empleados-lista';

describe('EmpleadosLista', () => {
  let component: EmpleadosLista;
  let fixture: ComponentFixture<EmpleadosLista>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpleadosLista],
    }).compileComponents();

    fixture = TestBed.createComponent(EmpleadosLista);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
