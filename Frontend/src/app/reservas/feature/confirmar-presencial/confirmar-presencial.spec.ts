import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfirmarPresencial } from './confirmar-presencial';

describe('ConfirmarPresencial', () => {
  let component: ConfirmarPresencial;
  let fixture: ComponentFixture<ConfirmarPresencial>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmarPresencial],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmarPresencial);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
