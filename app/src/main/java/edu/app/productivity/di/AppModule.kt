package edu.app.productivity.di

import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.app.productivity.data.ActionHistoryRepository
import edu.app.productivity.data.ActionTemplateRepository
import edu.app.productivity.data.DataStoreManager
import edu.app.productivity.data.PreferencesRepository
import edu.app.productivity.data.TimerRepository
import edu.app.productivity.data.db.ActionJsonAdapter
import edu.app.productivity.data.db.AppDatabase
import edu.app.productivity.data.db.DurationJsonAdapter
import edu.app.productivity.domain.Action
import javax.inject.Singleton
import kotlin.time.Duration

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
    fun provideActionTemplateRepository(db: AppDatabase) = ActionTemplateRepository(db)

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
    ).fallbackToDestructiveMigrationFrom(1, 2).build()

    @Provides
    @Singleton
    fun provideGson() = GsonBuilder()
        .registerTypeAdapter(Action.Work::class.java, ActionJsonAdapter<Action.Work>())
        .registerTypeAdapter(Action.Rest::class.java, ActionJsonAdapter<Action.Rest>())
        .registerTypeAdapter(
            Action.NotInitiatedAction::class.java,
            ActionJsonAdapter<Action.NotInitiatedAction>()
        )
        .registerTypeAdapter(Action::class.java, ActionJsonAdapter<Action>())
        .registerTypeAdapter(Duration::class.java, DurationJsonAdapter())
        .create()
}
