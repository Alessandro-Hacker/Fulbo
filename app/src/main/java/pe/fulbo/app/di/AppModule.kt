package pe.fulbo.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pe.fulbo.app.BuildConfig
import pe.fulbo.app.core.database.CanchaDao
import pe.fulbo.app.core.database.FulboDatabase
import pe.fulbo.app.core.network.AuthInterceptor
import pe.fulbo.app.core.network.FulboApi
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides @Singleton
    fun provideOkHttp(auth: AuthInterceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(auth)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            }
        }
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides @Singleton
    fun provideFulboApi(retrofit: Retrofit): FulboApi = retrofit.create(FulboApi::class.java)

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FulboDatabase =
        Room.databaseBuilder(context, FulboDatabase::class.java, "fulbo.db").build()

    @Provides
    fun provideCanchaDao(db: FulboDatabase): CanchaDao = db.canchaDao()
}
