import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PresencialCliente } from './presencial-cliente';

describe('PresencialCliente', () => {
  let component: PresencialCliente;
  let fixture: ComponentFixture<PresencialCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PresencialCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(PresencialCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
