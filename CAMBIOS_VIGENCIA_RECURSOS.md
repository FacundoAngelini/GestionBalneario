# Cambios: Validación de Vigencia de Recursos en Reservas

## Descripción del cambio
Se ha implementado una validación para que los recursos con vigencia limitada (rango de fechas) **no aparezcan en la lista de reservables** cuando el cliente intenta reservarlos parcialmente fuera de su período de vigencia.

## Problema que se resuelve
Anteriormente, si un recurso tenía una vigencia (precio) definida entre, por ejemplo, el 01/01/2026 y el 15/01/2026, el sistema permitía que aparezca como disponible incluso si el cliente intentaba hacer una reserva que se extendiera más allá del 15/01/2026.

Ahora, el sistema valida que **el precio del recurso cubra TODO el período solicitado por el cliente**. Si alguna parte de la reserva cae fuera del rango de vigencia, el recurso no aparecerá en la lista de disponibles.

## Cambios técnicos realizados

### Archivo modificado:
- `Backend/src/main/java/com/Gestion/MiBalnearioGestion/Recursos/Repositorios/RecursoRepositorio.java`

### Cambio en la query `encontrarDisponiblesEnRango`:

**Antes:**
```sql
SELECT r FROM RecursoEntity r 
WHERE r.esReservable = true 
AND r.id NOT IN (...)  -- [verificación de conflictos de reservas]
```

**Después:**
```sql
SELECT r FROM RecursoEntity r 
WHERE r.esReservable = true 
AND EXISTS (SELECT 1 FROM r.precioRecurso p 
  WHERE p.fechaVigencia <= :fechaInicio 
  AND p.fechaCaducada >= :fechaFin) 
AND r.id NOT IN (...)  -- [verificación de conflictos de reservas]
```

### Lógica de la validación:
La cláusula `EXISTS` verifica que exista al menos **un precio vigente** del recurso cuyas fechas cubran completamente el período de reserva:
- `p.fechaVigencia <= :fechaInicio`: La vigencia del precio comienza antes o en la fecha de inicio de la reserva
- `p.fechaCaducada >= :fechaFin`: La vigencia del precio finaliza en o después de la fecha de fin de la reserva

## Endpoints afectados
- `GET /api/recursos/disponibles?fechaInicio=...&fechaFin=...` → Ahora respeta la vigencia de precios
- `GET /api/recursos/catalogo?fechaInicio=...&fechaFin=...` → Ahora respeta la vigencia de precios

## Métodos del servicio afectados
- `RecursoServicio.listarDisponiblesParaElCliente(fechaInicio, fechaFin)`
- `RecursoServicio.listarCatalogo(fechaInicio, fechaFin, tipo)`

Ambos usan la query `encontrarDisponiblesEnRango` que ya contiene la validación.

## Comportamiento esperado

### Ejemplo 1: Recurso disponible ✅
- **Vigencia del precio:** 01/01/2026 - 31/01/2026
- **Solicitud de reserva:** 10/01/2026 - 20/01/2026
- **Resultado:** Recurso APARECE en la lista (todo el período está cubierto)

### Ejemplo 2: Recurso NO disponible ❌
- **Vigencia del precio:** 01/01/2026 - 15/01/2026
- **Solicitud de reserva:** 10/01/2026 - 25/01/2026
- **Resultado:** Recurso NO APARECE (la reserva se extiende más allá de la vigencia)

### Ejemplo 3: Recurso NO disponible ❌
- **Vigencia del precio:** 15/01/2026 - 31/01/2026
- **Solicitud de reserva:** 10/01/2026 - 20/01/2026
- **Resultado:** Recurso NO APARECE (la reserva comienza antes de la vigencia)

## Testing recomendado
1. Crear un recurso con un precio vigente desde 01/01/2026 hasta 15/01/2026
2. Intentar obtener recursos disponibles para:
   - Período 10/01/2026 - 12/01/2026 (DEBE APARECER)
   - Período 10/01/2026 - 20/01/2026 (NO DEBE APARECER)
   - Período 20/01/2026 - 25/01/2026 (NO DEBE APARECER)

## Notas importantes
- La validación se aplica a **todos los precios** del recurso. Si el recurso tiene múltiples precios en diferentes períodos, se valida contra cada uno.
- La funcionalidad de **crear reservas** (ReservaServicio.crearReservaInicial) ya validaba esto correctamente mediante `obtenerPrecioVigente()`. Este cambio simplemente **asegura consistencia en la lista de disponibles**.
- No afecta recursos sin precios configurados (aunque esto sería una inconsistencia en la configuración).
