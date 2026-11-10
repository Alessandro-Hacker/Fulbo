package pe.fulbo.app.feature.canchas.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pe.fulbo.app.feature.canchas.data.CanchasRepository
import pe.fulbo.app.feature.canchas.data.FakeCanchasRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class CanchasModule {
    // Cambiar a CanchasRepositoryImpl cuando el backend esté listo
    @Binds
    abstract fun bindCanchasRepository(impl: FakeCanchasRepository): CanchasRepository
}
