package pe.fulbo.app.feature.canchas.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pe.fulbo.app.feature.canchas.CanchasViewModel
import pe.fulbo.app.feature.canchas.data.CanchasRepository
import pe.fulbo.app.feature.canchas.data.FakeCanchasRepository

val canchasModule = module {
    // Cambiar a FirestoreCanchasRepository cuando la colección `canchas` esté lista
    single { FakeCanchasRepository() } bind CanchasRepository::class
    viewModelOf(::CanchasViewModel)
}
