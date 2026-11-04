import { inject, Service } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { firstValueFrom } from 'rxjs';
import { MensajeDialog, MensajeDialogData, TipoMensaje } from './mensaje-dialog';

export type Eleccion = 'principal' | 'secundaria' | 'cerrado';

@Service()
export class Dialogos {
  private readonly dialog = inject(MatDialog);

  async confirmar(titulo: string, mensaje: string, textoAceptar = 'Confirmar'): Promise<boolean> {
    const resultado = await this.abrir({ tipo: 'confirmacion', titulo, mensaje, textoAceptar, textoCancelar: 'Cancelar' });
    return resultado === true;
  }

  async avisar(tipo: Exclude<TipoMensaje, 'confirmacion'>, titulo: string, mensaje: string): Promise<void> {
    await this.abrir({ tipo, titulo, mensaje });
  }

  async elegir(
    tipo: Exclude<TipoMensaje, 'confirmacion'>,
    titulo: string,
    mensaje: string,
    textoPrincipal: string,
    textoSecundario: string,
  ): Promise<Eleccion> {
    const resultado = await this.abrir({
      tipo,
      titulo,
      mensaje,
      textoAceptar: textoPrincipal,
      textoCancelar: textoSecundario,
    });
    return resultado === true ? 'principal' : resultado === false ? 'secundaria' : 'cerrado';
  }

  private async abrir(data: MensajeDialogData): Promise<boolean | undefined> {
    const referencia = this.dialog.open<MensajeDialog, MensajeDialogData, boolean>(MensajeDialog, {
      data,
      width: 'min(440px, calc(100vw - 2rem))',
      maxWidth: 'calc(100vw - 2rem)',
    });
    return await firstValueFrom(referencia.afterClosed());
  }
}