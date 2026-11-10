# FULBO ⚽

App Android para encontrar canchas, armar partidos y equipos balanceados en Perú.
Para jugadores de 16 años en adelante y para locales que alquilan canchas.

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin 2.1 |
| UI | Jetpack Compose + Material 3 (tema oscuro) |
| Arquitectura | MVVM + Clean (data / domain / ui por feature) |
| Inyección de dependencias | Hilt |
| Navegación | Navigation Compose type-safe |
| Red | Retrofit + OkHttp + kotlinx.serialization |
| Datos locales | Room (caché) y DataStore (sesión) |
| Asincronía | Coroutines + Flow |

## Requisitos

- Android Studio Ladybug (2024.2) o más reciente
- JDK 17 (viene incluido con Android Studio)
- Emulador o celular con Android 8.0 (API 26) o superior

## Cómo correr el proyecto

1. Clona el repositorio:
   ```
   git clone https://github.com/FrankSernaqueRuiz/Fulbo.git
   ```
2. En Android Studio: **File → Open** y elige la carpeta `Fulbo`.
3. Espera a que termine el *Gradle Sync* (la primera vez tarda unos minutos).
4. Arriba, en **Build Variants**, elige `devDebug`.
5. Presiona ▶️ Run.

Desde terminal: `./gradlew assembleDevDebug` (en Windows: `gradlew.bat assembleDevDebug`).

### Variantes

| Variante | Backend | Uso |
|---|---|---|
| `dev` | `http://10.0.2.2:8080/api/` (tu PC visto desde el emulador) | Desarrollo |
| `prod` | `https://api.fulbo.pe/api/` | Producción |

Las URLs se cambian en `app/build.gradle.kts`. Mientras no exista backend, la pantalla
de Canchas usa datos falsos (`FakeCanchasRepository`).

## Estructura

```
app/src/main/java/pe/fulbo/app/
├── FulboApp.kt, MainActivity.kt
├── core/
│   ├── designsystem/   tema (colores, tipografía, espaciado) y componentes reutilizables
│   ├── model/          enums compartidos: Posicion, Nivel, Formato, Plan…
│   ├── network/        FulboApi, ApiResult, manejo de errores, AuthInterceptor
│   ├── database/       Room (caché)
│   └── datastore/      SessionStore (token)
├── di/                 módulos Hilt globales
├── navigation/         rutas y NavHost con barra inferior
└── feature/
    ├── auth/           bienvenida, registro, login  (M1)
    ├── canchas/        ← EJEMPLO COMPLETO del patrón a seguir (M2)
    └── …               partidos, reservas, equipo, liga, perfil, premium
```

### Patrón de cada feature (copiar de `feature/canchas`)

```
feature/<nombre>/
├── domain/      modelos de dominio y reglas de negocio (Kotlin puro, testeable)
├── data/        Repository (interfaz + implementación con API/Room)
├── di/          @Binds del repository
├── <X>ViewModel.kt   expone StateFlow<UiState>, recibe eventos
└── <X>Screen.kt      Route (conecta ViewModel) + Screen (UI pura con @Preview)
```

Reglas:
- La UI **nunca** llama a Retrofit o Room; solo al ViewModel.
- El ViewModel expone **un** `StateFlow<UiState>` inmutable.
- Los repositorios devuelven `ApiResult`, no lanzan excepciones.
- Textos en `res/values/strings.xml`, colores desde `FulboColors` / `MaterialTheme`.

## Trabajo con issues

El backlog está en [Issues](https://github.com/FrankSernaqueRuiz/Fulbo/issues), agrupado en milestones
M0 a M8. Lee [CONTRIBUTING.md](CONTRIBUTING.md) antes de tu primer PR.

## Tests

```
./gradlew testDevDebugUnitTest
```
Ejemplo: `RegistroValidatorTest` (DNI, edad mínima 16, correo).
