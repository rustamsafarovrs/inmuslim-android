package tj.rsdevteam.inmuslim.analytics.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tj.rsdevteam.inmuslim.analytics.AnalyticsTracker
import tj.rsdevteam.inmuslim.analytics.FirebaseAnalyticsTracker
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
public abstract class AnalyticsModule {

    @Binds
    @Singleton
    public abstract fun bindAnalyticsTracker(tracker: FirebaseAnalyticsTracker): AnalyticsTracker
}
