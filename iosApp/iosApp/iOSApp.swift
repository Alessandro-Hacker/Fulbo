import SwiftUI
import FirebaseCore
import Shared

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
        // Pon `true` para usar los emuladores de Firebase en el simulador (firebase emulators:start).
        IosEntryKt.doInitKoin(usarEmuladores: false, emuladorHost: "localhost")
    }

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}
