package edu.app.productivity.data.db

import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

object ActionEntityGenerator {
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
        ).random()
    ) = ActionEntity(
        !isWork,
        duration.inWholeMilliseconds,
        if (isWork) activityNameForWork else null
    )
}