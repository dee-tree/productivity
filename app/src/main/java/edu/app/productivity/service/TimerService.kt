package edu.app.productivity.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import edu.app.productivity.MainActivity
import edu.app.productivity.R
import edu.app.productivity.data.TimerRepository
import edu.app.productivity.domain.Action
import edu.app.productivity.domain.TimerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class TimerService : LifecycleService() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var repository: TimerRepository

    private var internalState: TimerState = TimerState.TimerNotInitiated

    private val binder = TimerServiceBinder()

    private var timerJob: Job? = null

    var isBound: Boolean = false
    private set

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Log.d(TAG, "Timer service is bound")
        isBound = true
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound = false
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Start command handled: ${intent?.action}")
        if (intent != null) {
            when (intent.action) {
                TimerActions.START.name -> {
                    val duration = intent.getLongExtra(DURATION_EXTRA_FIELD, 0).milliseconds
                    internalState = TimerState.TimerRunning(duration)
                    Log.d(TAG, "Start timer for $duration")
                }

                TimerActions.PAUSE.name -> {
                    internalState = internalState.pause()
                }

                TimerActions.RESUME.name -> {
                    internalState = internalState.resume()
                }

                TimerActions.CANCEL.name -> {
                    internalState = internalState.cancel()
                }

                TimerActions.COMPLETE.name -> {
                    // come here when internalState is already `TimerState.Completed`
                }

                else -> return super.onStartCommand(intent, flags, startId)
            }

            updateService(TimerActions.valueOf(intent.action!!))
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateService(newState: TimerActions) {
        Log.d(TAG, "Update service: $internalState")

        lifecycleScope.launch {
            repository.updateState(internalState)
        }

        when (newState) {
            TimerActions.START -> {
                if (@Suppress("RestrictedApi") notificationBuilder.mActions.isEmpty()) {
                    notificationBuilder.addAction(0, "", null) // for pause and resume
                        .addAction(
                            0,
                            getString(R.string.timer_notification_action_cancel),
                            cancelPendingIntent()
                        )
                }
                setPauseResumeButton(isPause = true)
                startService()
                startTimer()
            }

            TimerActions.PAUSE -> {
                setPauseResumeButton(isPause = false)
                updateNotification(internalState)
                lifecycleScope.launch {
                    timerJob?.cancelAndJoin()
                }
            }

            TimerActions.RESUME -> {
                setPauseResumeButton(isPause = true)
                startTimer()
            }

            TimerActions.CANCEL -> {
                lifecycleScope.launch {
                    timerJob?.cancelAndJoin()
                }
                stopService()
            }

            TimerActions.COMPLETE -> {
                notificationBuilder.clearActions()
                updateNotification(internalState)
                stopForeground(STOP_FOREGROUND_DETACH)
                stopSelf()

                lifecycleScope.launch {
                    val actions = repository.actions.value

                    if (actions.size > 1) {
                        // action completed, but there are another planned actions
                        delay(1.seconds)
                        val newActions = actions.drop(1)
                        val currentAction = actions.first()
                        repository.createNewActions(newActions)

                        triggerAction(this@TimerService, TimerActions.START) {
                            setDuration(currentAction.duration)
                        }
                    } else {
                        // last action completed
                        repository.createNewActions(emptyList())

                        if (isBound) {
                            stopService()
                        }
                    }
                }
            }
        }
    }


    private fun startTimer() {
        timerJob?.cancel()
        timerJob = lifecycleScope.launch(Dispatchers.Default) {
            while (true) {
                if (this.isActive) {
                    delay(1.seconds)
                    if (internalState.isRunning) {
                        internalState = internalState.countdown()

                        if (internalState.isRunning) {
                            repository.updateState(internalState)
                            updateNotification(internalState)
                        } else {
                            triggerAction(this@TimerService, TimerActions.COMPLETE)
                        }
                    } else {
                        Log.d(TAG, "Exit from timer's ticks due to external control");
                        return@launch
                    }
                }
            }
        }
    }

    private fun updateNotification(state: TimerState) {
        if (state is TimerState.TimerNotInitiated) {
            notificationManager.cancel(NOTIFICATION_ID)
            return
        }

        val contentTitle = when (state) {
            is TimerState.TimerRunning -> {
                val action = repository.actions.value.first()
                val activityName = (action as? Action.Work)?.activityName

                when {
                    action is Action.Rest -> getString(R.string.timer_notification_running_rest_header)
                    activityName.isNullOrEmpty() -> getString(R.string.timer_notification_running_default_activity_header)
                    else -> activityName
                }
            }

            is TimerState.TimerPaused -> getString(R.string.timer_notification_paused_header)
            is TimerState.TimerCancelled -> getString(R.string.timer_notification_cancelled_header)
            is TimerState.TimerCompleted -> getString(R.string.timer_notification_completed_header)
            else -> "Hiding notification..."
        }

        val contentText = if (state != TimerState.TimerCompleted) getString(
            R.string.timer_notification_remaining_content,
            state.remaining.inWholeMinutes,
            state.remaining.inWholeSeconds % 60
        ) else getString(R.string.timer_notification_great_completed_content)

        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .build()
        )
    }

    private fun setPauseResumeButton(isPause: Boolean = true) {
        val intent = createActionIntent(
            this,
            if (isPause) TimerActions.PAUSE else TimerActions.RESUME
        )
        val pendingIntent = PendingIntent.getService(
            this,
            INTENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        @SuppressLint("RestrictedApi")
        notificationBuilder.mActions[0] = NotificationCompat.Action(
            if (isPause) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24,
            getString(
                if (isPause) R.string.timer_notification_action_pause
                else R.string.timer_notification_action_resume
            ),
            pendingIntent
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun startService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW,
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun stopService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun isCompleted() = internalState.isCompleted

    enum class TimerActions { START, PAUSE, RESUME, CANCEL, COMPLETE }

    inner class TimerServiceBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "timer_notification_id"
        const val NOTIFICATION_CHANNEL_NAME = "timer_notification"
        const val NOTIFICATION_ID = 10

        const val DURATION_EXTRA_FIELD = "duration_millis"
        const val ACTIVITY_NAME_EXTRA_FIELD = "action"

        const val TAG = "TimerService"
        const val INTENT_REQUEST_CODE = 10
        const val CONTENT_CLICK_REQUEST_CODE = 1

        inline fun createActionIntent(
            ctx: Context, action: TimerActions, withIntent: Intent.() -> Unit = {}
        ): Intent {
            val intent = Intent(ctx, TimerService::class.java)
            withIntent(intent)
            intent.action = action.name
            return intent
        }

        inline fun triggerAction(
            ctx: Context, action: TimerActions, withIntent: Intent.() -> Unit = {}
        ) {
            val intent = createActionIntent(ctx, action, withIntent)
            ctx.startService(intent)
        }

        private fun Context.pendingIntent(action: TimerActions): PendingIntent {
            val intent = createActionIntent(this, action)
            return PendingIntent.getService(
                this,
                INTENT_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun Context.pausePendingIntent() = pendingIntent(TimerActions.PAUSE)
        fun Context.resumePendingIntent() = pendingIntent(TimerActions.RESUME)

        fun Context.cancelPendingIntent() = pendingIntent(TimerActions.CANCEL)

        fun Context.clickContentPendingEvent(): PendingIntent {
            val intent = Intent(this, MainActivity::class.java)
            return PendingIntent.getActivity(
                this,
                CONTENT_CLICK_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}

fun Intent.setDuration(duration: Duration) = apply {
    putExtra(TimerService.DURATION_EXTRA_FIELD, duration.inWholeMilliseconds)
}

fun Intent.setActivityName(activity: String) = apply {
    putExtra(TimerService.ACTIVITY_NAME_EXTRA_FIELD, activity)
}
