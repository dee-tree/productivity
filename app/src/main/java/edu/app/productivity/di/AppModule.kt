package edu.app.productivity.di

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.app.productivity.data.DataStoreManager
import edu.app.productivity.data.PreferencesRepository
import edu.app.productivity.data.TimerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTimerRepository() = TimerRepository()

    @Provides
    @Singleton
    fun providePreferencesRepository(dataStoreManager: DataStoreManager) =
        PreferencesRepository(dataStoreManager)

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext ctx: Context) = DataStoreManager(ctx)
}
