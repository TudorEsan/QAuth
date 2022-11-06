package net.theluckycoder.qr.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.nycode.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.Json
import net.theluckycoder.qr.datastore.UserDataStore
import net.theluckycoder.qr.model.Tokens
import net.theluckycoder.qr.network.service.ReservationService
import net.theluckycoder.qr.network.service.RoomService
import net.theluckycoder.qr.network.service.UserService
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @OptIn(DelicateCoroutinesApi::class)
    @Provides
    @Named("auth")
    fun providesAuthInterceptor(
        userDataStore: UserDataStore,
    ): Interceptor {
        var tokens: Tokens? = null

        GlobalScope.launch {
            userDataStore.tokens.collectLatest {
                ensureActive()
                tokens = it
            }
        }

        return Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder().apply {
                    tokens?.let {
                        addHeader("Cookie", "token=${it.token}")
                    }
                }.build()
            )
        }
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        @Named("auth") authInterceptor: Interceptor,
    ): OkHttpClient = runBlocking {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
//            .addInterceptor(HttpLoggingInterceptor())
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Singleton
    @Provides
    fun provideRoomService(retrofit: Retrofit) = retrofit.create<RoomService>()

    @Singleton
    @Provides
    fun providesUserService(retrofit: Retrofit) = retrofit.create<UserService>()

    @Singleton
    @Provides
    fun providesReservationService(retrofit: Retrofit) = retrofit.create<ReservationService>()

    const val BASE_URL = "https://financeapp.tudoresan.ro/"
}
