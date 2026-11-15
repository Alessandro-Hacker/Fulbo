package pe.fulbo.app.feature.perfil.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pe.fulbo.app.feature.perfil.PerfilViewModel
import pe.fulbo.app.feature.perfil.data.FirestorePerfilRepository
import pe.fulbo.app.feature.perfil.domain.PerfilRepository

val perfilModule = module {
    single { FirestorePerfilRepository(auth = get(), firestore = get()) } bind PerfilRepository::class
    viewModelOf(::PerfilViewModel)
}
