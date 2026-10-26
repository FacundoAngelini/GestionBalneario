import { inject, Service } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { firstValueFrom } from 'rxjs';
import { MensajeDialog, MensajeDialogData, TipoMensaje } from './mensaje-dialog';

/** Abre las alertas modales de la app. Un solo lugar para que todas se vean y se comporten igual. */
@Service()
export class Dialogos {
  private readonly dialog = inject(MatDialog);

  /** Pide confirmación. Devuelve true solo si la persona acepta. */
  confirmar(titulo: string, mensaje: string, textoAceptar = 'Confirmar'): Promise<boolean> {
    return this.abrir({ tipo: 'confirmacion', titulo, mensaje, textoAceptar, textoCancelar: 'Cancelar' });
  }

  /** Muestra un aviso con un solo botón. */
  async avisar(tipo: Exclude<TipoMensaje, 'confirmacion'>, titulo: string, mensaje: string): Promise<void> {
    await this.abrir({ tipo, titulo, mensaje });
  }

  private async abrir(data: MensajeDialogData): Promise<boolean> {
    const referencia = this.dialog.open<MensajeDialog, MensajeDialogData, boolean>(MensajeDialog, {
      data,
      width: 'min(440px, calc(100vw - 2rem))',
      maxWidth: 'calc(100vw - 2rem)',
    });
    return (await firstValueFrom(referencia.afterClosed())) === true;
  }
}