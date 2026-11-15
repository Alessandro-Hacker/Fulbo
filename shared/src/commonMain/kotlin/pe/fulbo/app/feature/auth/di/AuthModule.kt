package pe.fulbo.app.feature.auth.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pe.fulbo.app.feature.auth.AuthViewModel
import pe.fulbo.app.feature.auth.data.FirebaseAuthRepository
import pe.fulbo.app.feature.auth.domain.AuthRepository

val authModule = module {
    single { FirebaseAuthRepository(auth = get(), firestore = get()) } bind AuthRepository::class
    viewModelOf(::AuthViewModel)
}
