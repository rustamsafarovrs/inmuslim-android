package tj.rsdevteam.inmuslim.di.modules

import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import tj.rsdevteam.inmuslim.core.config.BuildType
import tj.rsdevteam.inmuslim.core.config.BuildVars
import tj.rsdevteam.inmuslim.data.api.Api
import tj.rsdevteam.inmuslim.data.constants.Constants
import tj.rsdevteam.inmuslim.data.repositories.ErrorHandler
import java.util.concurrent.TimeUnit

/**
 * Created by Rustam Safarov on 8/13/23.
 * github.com/rustamsafarovrs
 */

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildVars.BUILD_TYPE == BuildType.TEST) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    fun provideOkhttp(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(Constants.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(ResultCallAdapterFactory.create())
            .client(client)
            .baseUrl(BuildVars.BASE_URL)
            .build()
    }

    @Provides
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandler()
    }

    @Provides
    fun provideApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }
}
