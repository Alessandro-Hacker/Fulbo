# Cómo colaborar en FULBO

## Flujo de trabajo

1. Toma una issue asignada a ti y muévela a "en progreso".
2. Actualiza `main` y crea tu rama desde ahí:
   ```
   git checkout main
   git pull
   git checkout -b feature/12-registro-dni
   ```
3. Haz commits pequeños y claros.
4. Sube tu rama y abre un Pull Request hacia `main`:
   ```
   git push -u origin feature/12-registro-dni
   ```
5. En la descripción del PR escribe `Closes #12` para que la issue se cierre al hacer merge.
6. Espera que el CI esté en verde y que al menos una persona apruebe.

## Nombres de ramas

| Tipo | Formato | Ejemplo |
|---|---|---|
| Funcionalidad | `feature/<issue>-<descripcion>` | `feature/15-explorar-canchas` |
| Corrección | `fix/<issue>-<descripcion>` | `fix/40-precio-redondeo` |
| Configuración | `chore/<descripcion>` | `chore/actualizar-dependencias` |

## Mensajes de commit

Formato: `tipo(area): descripción en presente`

```
feat(canchas): agregar filtros de búsqueda
fix(reservas): evitar doble reserva del mismo horario
test(auth): validar edad mínima de 16 años
chore(gradle): actualizar compose bom
```

## Antes de abrir el PR

- [ ] Compila: `./gradlew :androidApp:assembleDevDebug`
- [ ] Tests pasan: `./gradlew :shared:testDebugUnitTest`
- [ ] Sin advertencias nuevas de lint: `./gradlew :androidApp:lintDevDebug`
- [ ] Pantallas nuevas tienen `@Preview`
- [ ] Textos en `composeResources/values/strings.xml`, no escritos directo en el código
- [ ] Nada de claves, tokens ni `google-services.json` / `GoogleService-Info.plist` en el commit

## Datos sensibles

El DNI y los datos de pago son información personal protegida. Nunca los escribas en logs,
analítica ni en la caché local.
