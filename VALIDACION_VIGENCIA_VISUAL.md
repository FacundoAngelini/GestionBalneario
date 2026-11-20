# Validación de Vigencia de Recursos - Resumen Visual

## Cambio Implementado

Se ha actualizado la consulta `encontrarDisponiblesEnRango()` en `RecursoRepositorio.java` para validar que un recurso solo aparezca en la lista de disponibles si su vigencia de precios **cubre completamente el período solicitado** por el cliente.

---

## Diagrama: Flujo de Validación

```
Cliente solicita reserva:
   fechaInicio: 10/01/2026
   fechaFin: 20/01/2026
         |
         v
Sistema consulta: obtenerRecursosDisponibles(10/01/2026, 20/01/2026)
         |
         v
RecursoServicio.listarDisponiblesParaElCliente()
         |
         v
RecursoRepositorio.encontrarDisponiblesEnRango(10/01/2026, 20/01/2026)
         |
         v
   ┌─────────────────────────────────────────────────────────┐
   │  NUEVA VALIDACIÓN: Vigencia de Precios                 │
   │                                                         │
   │  ¿Existe al menos UN precio del recurso cuya           │
   │  fechaVigencia <= 10/01/2026 AND                       │
   │  fechaCaducada >= 20/01/2026?                          │
   │                                                         │
   │  Validación SQL:                                       │
   │  EXISTS (SELECT 1 FROM precioRecurso p                 │
   │    WHERE p.fechaVigencia <= :fechaInicio              │
   │    AND p.fechaCaducada >= :fechaFin)                 │
   └─────────────────────────────────────────────────────────┘
         |
         ├─── SI ✅  → El recurso APARECE en la lista
         |
         └─── NO ❌  → El recurso NO APARECE en la lista
```

---

## Ejemplos de Comportamiento

### Escenario 1: Vigencia cubre completamente la reserva ✅

```
Vigencia del precio:   |=====================|
                       01/01            31/01
                      
Solicitud de reserva:        |======|
                             10/01  20/01

Resultado: ✅ APARECE en la lista
```

### Escenario 2: Vigencia termina antes que la reserva ❌

```
Vigencia del precio:   |=========|
                       01/01    15/01
                      
Solicitud de reserva:        |===========|
                             10/01    25/01
                                    ^ Se sale de vigencia

Resultado: ❌ NO APARECE en la lista
```

### Escenario 3: Vigencia comienza después que la reserva ❌

```
Vigencia del precio:                |==========|
                                    25/01    31/01
                      
Solicitud de reserva:        |======|
                             10/01  20/01
                             ^ Se sale de vigencia

Resultado: ❌ NO APARECE en la lista
```

### Escenario 4: Múltiples precios, uno es válido ✅

```
Precio 1 (Diciembre):  |=========|
                       01/12   31/12
                      
Precio 2 (Enero):              |=============|
                               01/01     31/01
                      
Solicitud de reserva:               |======|
                                   10/01  20/01

Resultado: ✅ APARECE en la lista (porque Precio 2 es válido)
```

---

## Cambio en SQL

### ANTES (Sin validación de vigencia):
```sql
SELECT r FROM RecursoEntity r 
WHERE r.esReservable = true 
AND r.id NOT IN (...)  -- [verificación de conflictos de reservas]
```
❌ Permitía que recursos con vigencia vencida aparecieran en la lista

---

### DESPUÉS (Con validación de vigencia):
```sql
SELECT r FROM RecursoEntity r 
WHERE r.esReservable = true 
AND EXISTS (SELECT 1 FROM r.precioRecurso p 
  WHERE p.fechaVigencia <= :fechaInicio 
  AND p.fechaCaducada >= :fechaFin) 
AND r.id NOT IN (...)  -- [verificación de conflictos de reservas]
```
✅ Garantiza que recursos con vigencia válida aparezcan en la lista

---

## Endpoints Afectados

- `GET /api/recursos/disponibles?fechaInicio=2026-01-10&fechaFin=2026-01-20`
- `GET /api/recursos/catalogo?fechaInicio=2026-01-10&fechaFin=2026-01-20&tipo=CARPA`

---

## Coherencia del Sistema

Este cambio **asegura coherencia** entre:

1. **Lista de disponibles** (RecursoRepositorio.encontrarDisponiblesEnRango)
   - Ahora valida vigencia ✅

2. **Creación de reservas** (ReservaServicio.crearReservaInicial)
   - Ya validaba vigencia mediante `obtenerPrecioVigente()` ✅

---

## Impacto en la Experiencia del Usuario

### Antes:
1. Cliente busca disponibles → Ve recurso X con vigencia vencida
2. Cliente intenta reservar → Recibe error "No existe tarifa vigente"
3. Cliente confundido ❌

### Después:
1. Cliente busca disponibles → No ve recurso X (ya filtrado)
2. Cliente no intenta reservar (no está disponible)
3. Cliente entiende fácilmente ✅

---

## Notas Técnicas

- La cláusula `EXISTS` es eficiente y ejecuta rápidamente
- Se valida contra **todos los precios** del recurso (hay múltiples para diferentes períodos)
- La validación se aplica a nivel de base de datos (más eficiente que en Java)
- No afecta el rendimiento significativamente (la query ya es compleja)
