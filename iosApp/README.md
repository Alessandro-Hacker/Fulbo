# iosApp

Aquí vive el arranque de iOS. Solo hay código Swift; toda la app está en `:shared`.

El proyecto de Xcode (`iosApp.xcodeproj`) **no se puede generar en Windows**, así que se crea
una sola vez en una Mac y se sube a git. Los archivos `.swift` de esta carpeta ya están listos.

## Crear el proyecto Xcode (una vez, en la Mac)

1. Abre Xcode → **File → New → Project → iOS → App**.
   - Product Name: `iosApp` · Interface: SwiftUI · Language: Swift
   - Organization Identifier de modo que el Bundle ID sea `pe.fulbo.app`
   - Guárdalo dentro de esta carpeta (`Fulbo/iosApp/`). Xcode crea `iosApp/iosApp.xcodeproj`.
2. Borra los `iOSApp.swift` y `ContentView.swift` que genera Xcode y agrega los de esta carpeta.
3. Enlaza el framework de Kotlin con una fase de compilación. En el target `iosApp` →
   **Build Phases → + → New Run Script Phase** (arrástrala antes de *Compile Sources*):
   ```
   cd "$SRCROOT/.."
   ./gradlew :shared:embedAndSignAppleFrameworkForXcode
   ```
   En **Build Settings** pon `User Script Sandboxing = No`.
   En **Framework Search Paths** agrega
   `$(SRCROOT)/../shared/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)`
   y en **Other Linker Flags** agrega `$(inherited) -framework Shared`.
4. **File → Add Package Dependencies** → `https://github.com/firebase/firebase-ios-sdk` → marca
   `FirebaseAuth`, `FirebaseFirestore`, `FirebaseFunctions` y `FirebaseMessaging`
   (el SDK de GitLive llama a estas librerías nativas, por eso deben estar enlazadas).
5. Descarga `GoogleService-Info.plist` desde la consola de Firebase y arrástralo a la carpeta
   `iosApp` de Xcode ("Copy items if needed"). **No se sube a git** (ya está en `.gitignore`).
6. En **Signing & Capabilities** agrega *Push Notifications* y *Background Modes → Remote notifications*.
7. En `Info.plist` agrega `NSLocationWhenInUseUsageDescription` = "Para mostrarte partidos cerca".

## Dev / prod en iOS

Crea dos *schemes* o configuraciones (Debug = `fulbo-dev`, Release = `fulbo-prod`) y copia el
`GoogleService-Info.plist` que corresponda en cada una. Una app iOS real necesita un plist por
proyecto de Firebase.
