package tj.rsdevteam.inmuslim.feature.tasbih.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepository
import tj.rsdevteam.inmuslim.feature.tasbih.data.repositories.TasbihRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTasbih(impl: TasbihRepositoryImpl): TasbihRepository
}
