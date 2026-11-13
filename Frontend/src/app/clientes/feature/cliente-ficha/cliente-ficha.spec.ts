import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClienteFicha } from './cliente-ficha';

describe('ClienteFicha', () => {
  let component: ClienteFicha;
  let fixture: ComponentFixture<ClienteFicha>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteFicha],
    }).compileComponents();

    fixture = TestBed.createComponent(ClienteFicha);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
