package edu.app.productivity.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import edu.app.productivity.R
import edu.app.productivity.service.TimerService
import edu.app.productivity.service.TimerService.Companion.cancelPendingIntent
import edu.app.productivity.service.TimerService.Companion.clickContentPendingEvent

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(@ApplicationContext context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, TimerService.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText("")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .addAction(0, "", null) // for pause and resume
            .addAction(
                0,
                context.getString(R.string.timer_notification_action_cancel),
                context.cancelPendingIntent()
            )
            .setContentIntent(context.clickContentPendingEvent())
    }

    @ServiceScoped
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context) = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}