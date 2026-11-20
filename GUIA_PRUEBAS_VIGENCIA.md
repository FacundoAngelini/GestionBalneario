# Guía de Prueba: Validación de Vigencia de Recursos

## Preparación del Entorno de Prueba

### Paso 1: Crear un Recurso de Prueba

1. Acceder a la panel de administración
2. Crear una nueva CARPA o SOMBRILLA
   - Nombre: `CARPA_PRUEBA_VIGENCIA`
   - Sector: Cualquiera
   - Estado: Activo/Reservable ✅

### Paso 2: Configurar Precios con Vigencia Limitada

Crear exactamente estos precios para el recurso:

#### Precio 1 (Período Enero):
- Modalidad: DIA
- Precio: $500
- Fecha Vigencia: `01/01/2026`
- Fecha Caducada: `31/01/2026`

#### Precio 2 (Período Febrero):
- Modalidad: DIA
- Precio: $600
- Fecha Vigencia: `01/02/2026`
- Fecha Caducada: `28/02/2026`

---

## Casos de Prueba

### TEST 1: Reserva completamente dentro del período vigente ✅

**Setup:**
- Fecha de inicio: `10/01/2026`
- Fecha de fin: `20/01/2026`

**Ejecución:**
```bash
GET /api/recursos/disponibles?fechaInicio=2026-01-10&fechaFin=2026-01-20
```

**Resultado esperado:**
- ✅ La carpa `CARPA_PRUEBA_VIGENCIA` **DEBE APARECER** en la lista
- Razón: El Precio 1 (01/01 - 31/01) cubre completamente el período (10/01 - 20/01)

---

### TEST 2: Reserva que se extiende más allá de la vigencia ❌

**Setup:**
- Fecha de inicio: `10/01/2026`
- Fecha de fin: `05/02/2026` ← Se sale del rango de vigencia de Precio 1

**Ejecución:**
```bash
GET /api/recursos/disponibles?fechaInicio=2026-01-10&fechaFin=2026-02-05
```

**Resultado esperado:**
- ❌ La carpa `CARPA_PRUEBA_VIGENCIA` **NO DEBE APARECER** en la lista
- Razón: 
  - Precio 1 (01/01 - 31/01) no cubre hasta 05/02
  - Precio 2 (01/02 - 28/02) no comienza hasta 01/02 (la reserva empieza el 10/01)
  - Ningún precio cubre completamente el período

---

### TEST 3: Reserva que comienza después de que termina la vigencia ❌

**Setup:**
- Fecha de inicio: `05/02/2026`
- Fecha de fin: `10/02/2026`

**Ejecución:**
```bash
GET /api/recursos/disponibles?fechaInicio=2026-02-05&fechaFin=2026-02-10
```

**Resultado esperado:**
- ✅ La carpa `CARPA_PRUEBA_VIGENCIA` **DEBE APARECER** en la lista
- Razón: El Precio 2 (01/02 - 28/02) cubre completamente el período (05/02 - 10/02)

---

### TEST 4: Reserva antes de que comience la vigencia ❌

**Setup:**
- Fecha de inicio: `25/12/2025`
- Fecha de fin: `30/12/2025`

**Ejecución:**
```bash
GET /api/recursos/disponibles?fechaInicio=2025-12-25&fechaFin=2025-12-30
```

**Resultado esperado:**
- ❌ La carpa `CARPA_PRUEBA_VIGENCIA` **NO DEBE APARECER** en la lista
- Razón: El Precio 1 comienza el 01/01/2026, pero la reserva es en Diciembre 2025

---

### TEST 5: Caso límite - Coincide exactamente con vigencia ✅

**Setup:**
- Fecha de inicio: `01/01/2026`
- Fecha de fin: `31/01/2026`

**Ejecución:**
```bash
GET /api/recursos/disponibles?fechaInicio=2026-01-01&fechaFin=2026-01-31
```

**Resultado esperado:**
- ✅ La carpa `CARPA_PRUEBA_VIGENCIA` **DEBE APARECER** en la lista
- Razón: El Precio 1 (01/01 - 31/01) coincide exactamente

---

### TEST 6: Validación en el catálogo ✅

**Setup:**
- Fecha de inicio: `10/01/2026`
- Fecha de fin: `20/01/2026`

**Ejecución:**
```bash
GET /api/recursos/catalogo?fechaInicio=2026-01-10&fechaFin=2026-01-20&tipo=CARPA
```

**Resultado esperado:**
- ✅ La carpa `CARPA_PRUEBA_VIGENCIA` **DEBE APARECER** en el catálogo
- El catálogo usa la misma query de disponibles

---

## Verificación en Base de Datos

Para verificar que la query se ejecute correctamente, revisar los logs:

```sql
-- La query ejecutada en la base de datos será:
SELECT r.* 
FROM Recurso r 
WHERE r.recurso_activo = true 
AND EXISTS (
  SELECT 1 
  FROM precio_recursos p 
  WHERE p.recurso_id = r.id 
  AND p.fecha_vigencia <= '2026-01-10' 
  AND p.fecha_caducada >= '2026-01-20'
) 
AND r.id NOT IN (...)  -- [verificación de conflictos]
```

---

## Checklist de Validación

- [ ] TEST 1: Reserva dentro de vigencia → Aparece ✅
- [ ] TEST 2: Reserva fuera de vigencia (después) → No aparece ❌
- [ ] TEST 3: Reserva en período siguiente → Aparece ✅
- [ ] TEST 4: Reserva antes de vigencia → No aparece ❌
- [ ] TEST 5: Reserva coincide exactamente → Aparece ✅
- [ ] TEST 6: Catálogo respeta vigencia → Funciona ✅

---

## Notas Importantes

1. **Consistencia**: Si un cliente logra crear una reserva (endpoint POST), los datos serán válidos porque `ReservaServicio` también valida vigencia en `obtenerPrecioVigente()`.

2. **Cambios realizados**: Solo se modificó la query en `RecursoRepositorio.encontrarDisponiblesEnRango()`. No hay cambios en la lógica de creación de reservas.

3. **Rendimiento**: La cláusula `EXISTS` es muy eficiente. No debería haber impacto notable en performance.

4. **Edge cases**:
   - Recursos sin precios no aparecerán (bueno, significa que no están configurados)
   - Múltiples precios: Se valida que AL MENOS UNO cubra el período

---

## Reverting Changes (Si es necesario)

```bash
# Para descartar los cambios:
git checkout Backend/src/main/java/com/Gestion/MiBalnearioGestion/Recursos/Repositorios/RecursoRepositorio.java

# Para restaurar los cambios de nuevo:
git switch -
```
