package edu.app.productivity.data.db

import java.time.Instant
import java.util.Date
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object ActionHistoryEntityGenerator {

    fun generate(
        isWork: Boolean = Random.nextBoolean(),
        duration: Duration = (1..120).random().minutes,
        activityNameForWork: String = listOf(
            "Work",
            "Cooking",
            "Swimming",
            "Sport",
            "Edu",
            "Yoga"
        ).random(),
        completedAt: Date = Date.from(Instant.now() - (0..(45.days.inWholeSeconds)).random().seconds.toJavaDuration())
    ) = ActionHistoryEntity(
        completedAt,
        ActionEntityGenerator.generate(isWork, duration, activityNameForWork)
    )
}