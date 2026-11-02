import {
  maxLength,
  minLength,
  pattern,
  PathKind,
  requiredError,
  SchemaPath,
  SchemaPathRules,
  validate,
} from '@angular/forms/signals';
import { PAISES } from '../constants/paises.constants';

/** Equivale al @NotBlank de Spring: no vacío y no solo espacios. */
export function noBlanco<TPathKind extends PathKind = PathKind.Root>(
  path: SchemaPath<string, SchemaPathRules.Supported, TPathKind>,
  message: string,
) {
  validate(path, ({ value }) => (value().trim() === '' ? requiredError({ message }) : undefined));
}

export function telefonoSegunPais<TPathKind extends PathKind = PathKind.Root>(
  telefono: SchemaPath<string, SchemaPathRules.Supported, TPathKind>,
  pais: SchemaPath<string, SchemaPathRules.Supported, TPathKind>,
) {
  noBlanco(telefono, 'El teléfono es obligatorio');
  pattern(telefono, /^[0-9]*$/, { message: 'Ingresá solo números, sin espacios, guiones ni el signo +' });
  validate(telefono, ({ value, valueOf }) => {
    const numero = value();
    const elegido = PAISES.find((p) => p.prefijo === valueOf(pais));
    if (!elegido || numero === '' || !/^[0-9]+$/.test(numero)) return undefined; // otras reglas ya lo cubren
    if (numero.length >= elegido.min && numero.length <= elegido.max) return undefined;
    const digitos = elegido.min === elegido.max ? `${elegido.min} dígitos` : `entre ${elegido.min} y ${elegido.max} dígitos`;
    return {
      kind: 'largo',
      message: `En ${elegido.nombre} el teléfono tiene ${digitos} (${elegido.ayuda}). Ej: ${elegido.ejemplo}`,
    };
  });
}

export function contraseniaSegura<TPathKind extends PathKind = PathKind.Root>(
  path: SchemaPath<string, SchemaPathRules.Supported, TPathKind>,
) {
  noBlanco(path, 'La contraseña es obligatoria');
  minLength(path, 8, { message: 'Debe tener al menos 8 caracteres' });
  maxLength(path, 20, { message: 'Debe tener como máximo 20 caracteres' });
  pattern(path, /[A-Z]/, { message: 'Debe incluir al menos una mayúscula' });
  pattern(path, /[a-z]/, { message: 'Debe incluir al menos una minúscula' });
  pattern(path, /\d/, { message: 'Debe incluir al menos un número' });
  pattern(path, /[@$!%*?&]/, { message: 'Debe incluir un símbolo: @ $ ! % * ? &' });
  pattern(path, /^[A-Za-z\d@$!%*?&]*$/, {
    message: 'Solo se permiten letras, números y los símbolos @ $ ! % * ? &',
  });
}