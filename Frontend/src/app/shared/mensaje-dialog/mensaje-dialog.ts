import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

export type TipoMensaje = 'info' | 'exito' | 'error' | 'advertencia' | 'confirmacion';

export interface MensajeDialogData {
  tipo: TipoMensaje;
  titulo: string;
  mensaje: string;
  textoAceptar?: string;
  /** Si viene, se muestra el botón de cancelar (diálogo de confirmación). */
  textoCancelar?: string;
}

const ICONOS: Record<TipoMensaje, string> = {
  info: 'info',
  exito: 'check_circle',
  error: 'error',
  advertencia: 'warning',
  confirmacion: 'help',
};

@Component({
  selector: 'app-mensaje-dialog',
  imports: [MatDialogModule, MatButtonModule, MatIconModule],
  templateUrl: './mensaje-dialog.html',
  styleUrl: './mensaje-dialog.css',
})
export class MensajeDialog {
  protected readonly data = inject<MensajeDialogData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject<MatDialogRef<MensajeDialog, boolean>>(MatDialogRef);

  protected readonly icono = ICONOS[this.data.tipo];

  protected cerrar(resultado: boolean): void {
    this.dialogRef.close(resultado);
  }
}