package pe.fulbo.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import pe.fulbo.app.core.firebase.ConfigApp
import pe.fulbo.app.di.initKoin

class FulboApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase lo inicializa solo el plugin google-services; aquí solo se arranca Koin.
        initKoin(
            config = ConfigApp(
                usarEmuladores = BuildConfig.USAR_EMULADORES,
                emuladorHost = BuildConfig.EMULADOR_HOST,
            ),
        ) {
            androidContext(this@FulboApp)
        }
    }
}
