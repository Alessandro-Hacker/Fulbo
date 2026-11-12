package pe.fulbo.app.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import pe.fulbo.app.core.firebase.ConfigApp
import pe.fulbo.app.core.firebase.conectarEmuladores
import pe.fulbo.app.core.firebase.firebaseModule
import pe.fulbo.app.feature.canchas.di.canchasModule

/**
 * Junta los módulos de cada feature. Al crear un feature nuevo, agrega aquí su `xxxModule`.
 *
 * Android: `initKoin(config) { androidContext(this@FulboApp) }` en FulboApp.onCreate()
 * iOS: `IosEntryKt.doInitKoin(...)` en iOSApp.swift
 */
fun initKoin(config: ConfigApp = ConfigApp(), extra: KoinAppDeclaration = {}): KoinApplication {
    // Los emuladores se configuran antes de que cualquier repositorio use Firebase.
    if (config.usarEmuladores) conectarEmuladores(config.emuladorHost)

    return startKoin {
        extra()
        modules(
            firebaseModule,
            canchasModule,
            // authModule, partidosModule, … (se agregan con cada feature)
        )
    }
}
