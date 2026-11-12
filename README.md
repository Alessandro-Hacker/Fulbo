# FULBO ⚽

App (Android e iOS) para encontrar canchas, armar partidos y equipos balanceados en Perú.
Para jugadores de 16 años en adelante y para locales que alquilan canchas.

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin 2.2 (Kotlin Multiplatform) |
| UI | Compose Multiplatform + Material 3 (tema oscuro), una sola UI para Android e iOS |
| Arquitectura | MVVM + Clean (data / domain / ui por feature) |
| Inyección de dependencias | Koin |
| Navegación | Navigation Compose (versión KMP de JetBrains), rutas type-safe |
| Backend | Firebase: Auth, Firestore, Cloud Functions, Messaging (SDK de GitLive) |
| Asincronía | Coroutines + Flow |
| Fechas | kotlinx-datetime |

El backend (reglas de seguridad, índices, Cloud Functions) vive en [`firebase/`](firebase/README.md).

## Requisitos

- Android Studio Ladybug (2024.2) o más reciente, con el plugin *Kotlin Multiplatform*
- JDK 21 (el proyecto lo descarga solo; ver `gradle/gradle-daemon-jvm.properties`)
- Android SDK 35
- Para compilar iOS: una **Mac con Xcode** (Apple no permite compilar iOS desde Windows).
  En Windows todo el código compartido se puede escribir y probar con Android.

## Cómo correr el proyecto

1. Clona el repositorio y ábrelo en Android Studio (**File → Open**).
2. Espera el *Gradle Sync*.
3. En **Build Variants** elige `devDebug` del módulo `androidApp` y presiona ▶️.

Desde terminal: `./gradlew :androidApp:assembleDevDebug` (en Windows: `gradlew.bat ...`).

### Conectar Firebase (una vez por entorno)

1. Crea los proyectos `fulbo-dev` y `fulbo-prod` y registra las apps (guía, secciones 2.1 a 2.4).
2. Descarga los `google-services.json` y ponlos en `androidApp/src/dev/` y `androidApp/src/prod/`.
   No se suben a git. Sin ellos la app compila pero no puede usar Firebase.
3. En iOS: `GoogleService-Info.plist`, ver [`iosApp/README.md`](iosApp/README.md).

Para usar los emuladores de Firebase en `dev` (sin gastar nada): levántalos con
`firebase emulators:start` y compila con `-Pfulbo.emuladores=true`
(o agrega `fulbo.emuladores=true` en `~/.gradle/gradle.properties`).

### Variantes

| Variante | Proyecto Firebase | Application ID |
|---|---|---|
| `dev` | `fulbo-dev` (o emuladores) | `pe.fulbo.app.dev` |
| `prod` | `fulbo-prod` | `pe.fulbo.app` |

## Estructura

```
Fulbo/
├── shared/         código común (~90 % de la app)
│   └── src/
│       ├── commonMain/     modelos, repositorios, ViewModels, pantallas
│       ├── androidMain/    lo que solo existe en Android (GPS, AdMob…)
│       ├── iosMain/        lo que solo existe en iOS (MainViewController, doInitKoin)
│       └── commonTest/     tests (RegistroValidatorTest)
├── androidApp/     MainActivity, flavors dev/prod, google-services.json
├── iosApp/         arranque en Swift (el .xcodeproj se crea en una Mac)
└── firebase/       reglas, índices y Cloud Functions
```

Dentro de `shared/src/commonMain/kotlin/pe/fulbo/app/`:

```
├── App.kt
├── core/
│   ├── designsystem/   tema (colores, tipografía, espaciado) y componentes reutilizables
│   ├── model/          enums compartidos: Posicion, Nivel, Formato, Plan, EstadoPartido…
│   ├── firebase/       FirebaseModule (Koin), conectarEmuladores, nombres de colecciones y funciones
│   └── util/           Resultado, AppError
├── di/                 Koin.kt: initKoin() junta los módulos de cada feature
├── navigation/         rutas type-safe y NavHost con barra inferior
└── feature/
    ├── auth/           bienvenida, registro, login, verificar correo, completar perfil
    ├── canchas/        ← EJEMPLO COMPLETO del patrón a seguir
    ├── partidos/       buscar, detalle, unirse, publicar, mis partidos
    ├── reputacion/     calificar, ver reputación
    ├── equipo/         equipo fijo, alineación (Premium)
    ├── premium/        suscripción
    └── perfil/         mi perfil, ajustes, borrar cuenta
```

### Patrón de cada feature (copiar de `feature/canchas`)

```
feature/<nombre>/
├── domain/      modelos de dominio (Kotlin puro, sin Firebase) e interfaz del repositorio
├── data/        DTO (igual al documento de Firestore) + implementación con GitLive
├── di/          módulo Koin (agregarlo en di/Koin.kt)
├── <X>ViewModel.kt   expone StateFlow<UiState>, recibe eventos
└── <X>Screen.kt      Route (conecta ViewModel) + Screen (UI pura con @Preview)
```

Reglas:
- La UI **nunca** llama a Firebase; solo al ViewModel. El ViewModel nunca importa `dev.gitlive`.
- El ViewModel expone **un** `StateFlow<UiState>` inmutable.
- Los repositorios devuelven `Resultado`, no lanzan excepciones.
- Lo que cambia, escribe o cuenta cupos, reputación, plan o dinero pasa por una Cloud Function:
  la app pide, el servidor decide.
- Textos en `shared/src/commonMain/composeResources/values/strings.xml`, colores desde
  `FulboColors` / `MaterialTheme`.
- En `commonMain` no se puede usar `java.*`, `String.format` ni `android.*`. Lo específico de
  cada plataforma va como interfaz en `commonMain` e implementación en `androidMain` / `iosMain`.

## Trabajo con issues

El backlog está en [Issues](https://github.com/FrankSernaqueRuiz/Fulbo/issues), agrupado en milestones
M0 a M8. Lee [CONTRIBUTING.md](CONTRIBUTING.md) antes de tu primer PR.

## Tests

```
./gradlew :shared:testDebugUnitTest
```
Ejemplo: `RegistroValidatorTest` (DNI, edad mínima 16, correo).
