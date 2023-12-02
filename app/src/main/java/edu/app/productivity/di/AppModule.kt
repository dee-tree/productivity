package edu.app.productivity.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.app.productivity.data.ActionHistoryRepository
import edu.app.productivity.data.DataStoreManager
import edu.app.productivity.data.PreferencesRepository
import edu.app.productivity.data.TimerRepository
import edu.app.productivity.data.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTimerRepository(db: AppDatabase) = TimerRepository(db)

    @Provides
    @Singleton
    fun provideActionHistoryRepository(
        db: AppDatabase,
        dataStoreManager: DataStoreManager
    ) = ActionHistoryRepository(db, dataStoreManager)

    @Provides
    @Singleton
    fun providePreferencesRepository(dataStoreManager: DataStoreManager, db: AppDatabase) =
        PreferencesRepository(dataStoreManager, db)

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext ctx: Context) = DataStoreManager(ctx)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext ctx: Context
    ) = Room.databaseBuilder(
        ctx,
        AppDatabase::class.java,
        "productivity_app_db"
    ).build()
}
