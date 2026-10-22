export interface JwtPayload {
  sub: string;
  exp: number; 
  authorities?: string[];
}

export function leerPayload(token: string): JwtPayload | null {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64)) as JwtPayload;
  } catch {
    return null;
  }
}

export function tokenVencido(token: string): boolean {
  const payload = leerPayload(token);
  return !payload || payload.exp * 1000 <= Date.now();
}

export function extraerRoles(token: string | null): string[] {
  if (!token) return [];
  const authorities = leerPayload(token)?.authorities ?? [];
  return authorities.filter((a) => a.startsWith('ROLE_'));
}

/* esta clase es fundamental para ver que roles tiene el usuario, para los guards y el menu deiddan que mostrar
ademas, si el token vencio no tratata alguien logueado con la sesion termianda.  */
