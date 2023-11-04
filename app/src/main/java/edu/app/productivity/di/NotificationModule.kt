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
import edu.app.productivity.service.TimerService

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(@ApplicationContext context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, TimerService.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Stopwatch")
            .setContentText("00:00:00")
            //.setSmallIcon(R.drawable.ic_baseline_timer_24) // TODO: set app icon
            .setOngoing(true)
//            .addAction(0, "", ServiceHelper.stopPendingIntent(context))
//            .addAction(0, "Cancel", ServiceHelper.cancelPendingIntent(context))
//            .setContentIntent(ServiceHelper.clickPendingIntent(context))

    }

    @ServiceScoped
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context) = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}