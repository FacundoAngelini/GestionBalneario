export const ROUTE_SEGMENTS = {
  auth: 'auth',
  login: 'login',
  registro: 'registro',

  panel: 'panel',
  perfil: 'perfil',

  catalogo: 'catalogo',
  productos: 'productos',
  cargarProducto: 'cargar',
  reservas: 'reservas',
  /* nuevaReserva: 'nueva', */
  misReservas: 'mis-reservas',
  sinPago: 'sin-pago',
  gestionReservas: 'gestion',
  recursos: 'recursos',
  cargaMasiva: 'carga-masiva',
  precios: 'precios', 
  presencial: 'presencial',
  confirmar: 'confirmar',
  comprobante: 'comprobante',

  //empleados
  empleados: 'empleados',
  nuevo: 'nuevo',
  editar: 'editar',
  //clientes
  clientes: 'clientes',


  unauthorized: 'no-autorizado'
} as const;

export const MIN_PORCENTAJE = -90;
export const MAX_PORCENTAJE = 300;