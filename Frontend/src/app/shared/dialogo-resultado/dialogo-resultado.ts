import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

export interface DatosDialogoResultado {
  titulo: string;
  mensaje: string;
  textoPrincipal: string;
  textoSecundario?: string;
}

export type RespuestaDialogo = 'principal' | 'secundaria';

@Component({
  selector: 'app-dialogo-resultado',
  imports: [MatDialogModule],
  templateUrl: './dialogo-resultado.html',
  styleUrl: './dialogo-resultado.css',
})
export class DialogoResultado {
  protected readonly datos = inject<DatosDialogoResultado>(MAT_DIALOG_DATA);
}
