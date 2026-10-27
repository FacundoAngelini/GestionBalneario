import { Component, inject, signal } from '@angular/core';
import { FormField, form, submit } from '@angular/forms/signals';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import {
  formToReserva,
  reservaFormInicial,
  reservaSchema,
} from '../../data-access/reserva-model';
import { ReservaService } from '../../data-access/reserva-service';

@Component({
  imports: [FormField],
  selector: 'app-reserva-form',
  styleUrl: './reserva-form.css',
  templateUrl: './reserva-form.html',
})
export class ReservaForm {
  private reservaService = inject(ReservaService);
  private router = inject(Router);

  errorMsg = signal<string>('');
  successMsg = signal<string>('');

  reservaModel = signal(reservaFormInicial());
  reservaForm = form(this.reservaModel, reservaSchema);

  // El backend espera una lista de UUIDs; el input los recibe separados por coma.
  recursosTexto = signal<string>('');

  onRecursosChange(texto: string) {
    this.recursosTexto.set(texto);
    this.reservaModel.update((m) => ({
      ...m,
      recursosPublicIds: texto
        .split(',')
        .map((id) => id.trim())
        .filter((id) => id.length > 0),
    }));
  }

  enviar(event: Event) {
    event.preventDefault();
    this.errorMsg.set('');
    this.successMsg.set('');

    submit(this.reservaForm, async () => {
      try {
        await firstValueFrom(
          this.reservaService.crearReservaInicial(formToReserva(this.reservaModel())),
        );
        this.successMsg.set('Reserva creada correctamente');
      } catch (err: any) {
        this.errorMsg.set(
          err?.status === 409
            ? 'Los recursos ya están reservados para las fechas seleccionadas'
            : err?.status === 400
              ? 'Los datos de la reserva son inválidos'
              : 'No se pudo procesar la reserva. Intentá nuevamente',
        );
      }
    });
  }

  cancelar() {
    this.router.navigate(['/reservas']);
  }
}
