package pe.fulbo.app

import androidx.compose.ui.window.ComposeUIViewController
import pe.fulbo.app.core.firebase.ConfigApp
import pe.fulbo.app.di.initKoin
import platform.UIKit.UIViewController

/**
 * Puntos de entrada que llama Swift (iosApp/iosApp/iOSApp.swift y ContentView.swift):
 *   IosEntryKt.doInitKoin(usarEmuladores: false, emuladorHost: "localhost")
 *   IosEntryKt.MainViewController()
 */
fun doInitKoin(usarEmuladores: Boolean, emuladorHost: String) {
    initKoin(ConfigApp(usarEmuladores = usarEmuladores, emuladorHost = emuladorHost))
}

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
