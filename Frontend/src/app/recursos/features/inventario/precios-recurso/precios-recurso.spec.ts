import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PreciosRecurso } from './precios-recurso';

describe('PreciosRecurso', () => {
  let component: PreciosRecurso;
  let fixture: ComponentFixture<PreciosRecurso>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreciosRecurso],
    }).compileComponents();

    fixture = TestBed.createComponent(PreciosRecurso);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
