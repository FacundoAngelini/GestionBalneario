import { buscarPais } from '../constants/paises.constants';
export const esNumero = (n: unknown): n is number => typeof n === 'number' && Number.isFinite(n);

// Valida un entero dentro de un rango; devuelve el mensaje de error o null si esta bien
export const errorEntero = (
  valor: unknown,
  min: number,
  max: number,
  mensajeFuera: string,
  mensajeVacio: string,
): string | null => {
  if (!esNumero(valor)) return mensajeVacio;
  if (!Number.isInteger(valor)) return 'Ingresá un número entero';
  return valor < min || valor > max ? mensajeFuera : null;
};

const EMAIL = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/;

export const errorEmail = (email: string): string | null => {
  const v = email.trim();
  if (!v) return 'El email es obligatorio';
  return EMAIL.test(v) ? null : 'Formato inválido: en minúsculas y con dominio de 2 o 3 letras (ej: nombre@correo.com)';
};

export const errorTelefono = (prefijoPais: string, numero: string): string | null => {
  const n = numero.trim();
  if (!n) return 'El teléfono es obligatorio';
  if (!/^[0-9]+$/.test(n)) return 'Ingresá solo números, sin espacios, guiones ni el signo +';
  const pais = buscarPais(prefijoPais);
  if (!pais || (n.length >= pais.min && n.length <= pais.max)) return null;
  const digitos = pais.min === pais.max ? `${pais.min} dígitos` : `entre ${pais.min} y ${pais.max} dígitos`;
  return `En ${pais.nombre} el teléfono tiene ${digitos} (${pais.ayuda}). Ej: ${pais.ejemplo}`;
};

export const errorContrasenia = (c: string): string | null => {
  if (!c) return 'La contraseña es obligatoria';
  if (c.length < 8) return 'Debe tener al menos 8 caracteres';
  if (c.length > 20) return 'Debe tener como máximo 20 caracteres';
  if (!/[A-Z]/.test(c)) return 'Debe incluir al menos una mayúscula';
  if (!/[a-z]/.test(c)) return 'Debe incluir al menos una minúscula';
  if (!/\d/.test(c)) return 'Debe incluir al menos un número';
  if (!/[@$!%*?&]/.test(c)) return 'Debe incluir un símbolo: @ $ ! % * ? &';
  if (!/^[A-Za-z\d@$!%*?&]*$/.test(c)) return 'Solo se permiten letras, números y los símbolos @ $ ! % * ? &';
  return null;
};

const PESOS_CUIT = [5, 4, 3, 2, 7, 6, 5, 4, 3, 2];

// Mismo cálculo que el backend: 11 números y el último es el dígito verificador
export const errorCuit = (cuit: string): string | null => {
  const v = cuit.trim();
  if (!v) return 'El CUIT es obligatorio';
  if (!/^\d{11}$/.test(v)) return 'El CUIT son 11 números, sin guiones';
  const suma = PESOS_CUIT.reduce((acc, peso, i) => acc + Number(v[i]) * peso, 0);
  const resto = 11 - (suma % 11);
  const verificador = resto === 11 ? 0 : resto;
  return verificador === Number(v[10]) ? null : 'El CUIT no es válido: el dígito verificador no coincide';
};