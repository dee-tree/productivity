package edu.app.productivity.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.lang.Integer.max
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TimerStateTest {

    @Test
    fun testPause() {
        val initialTime = 30.minutes
        val running = TimerState.TimerRunning(initialTime)
        val paused = running.pause()

        assertTrue(paused is TimerState.TimerPaused)
        assertTrue(paused.isPaused)
        assertFalse(paused.isRunning)
        assertEquals(initialTime, paused.remaining)

        runBlocking {
            launch { delay(1.seconds) }
        }

        assertTrue(System.currentTimeMillis() - paused.pausedAt >= 1000)
        assertEquals(initialTime, paused.remaining)

        val resumed = paused.resume()
        assertTrue(resumed is TimerState.TimerRunning)
        assertTrue(resumed.isRunning)
        assertFalse(resumed.isPaused)

        assertThrows<IllegalStateException> { paused.countdown() }
    }

    @Test
    fun testCancel() {
        val initialTime = 30.minutes
        val running = TimerState.TimerRunning(initialTime)
        val paused = running.pause()

        val cancelled = paused.cancel()

        assertTrue(cancelled is TimerState.TimerCancelled)
        assertTrue(cancelled.isCancelled)
        assertFalse(cancelled.isRunning)

        runBlocking {
            launch { delay(1.seconds) }
        }

        assertEquals(initialTime, cancelled.remaining)

        val restored = cancelled.restore()
        assertTrue(restored is TimerState.TimerRunning)
        assertTrue(restored.isRunning)
        assertFalse(restored.isCancelled)

        assertThrows<IllegalStateException> { cancelled.resume() }
        assertThrows<IllegalStateException> { cancelled.countdown() }
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 5, 10, 30, 0, 1000])
    fun testRunning(initialSeconds: Int) {
        assumeTrue(initialSeconds >= 0)
        val initialTime = initialSeconds.seconds
        var running: TimerState = TimerState.TimerRunning(initialTime)

        assertEquals(initialTime, running.remaining)
        assertTrue(running.isRunning)

        repeat(max(initialSeconds, 1)) { secsCounted ->
            assertTrue(running is TimerState.TimerRunning)
            assertTrue(running.isRunning)
            assertFalse(running.isPaused)
            assertFalse(running.isCancelled)
            assertFalse(running.isCompleted)
            assertDoesNotThrow {
                running = running.countdown()
            }

            assertEquals(
                max(0, initialSeconds - secsCounted - 1),
                running.remaining.inWholeSeconds.toInt()
            )
        }

        val completed = running

        assertTrue(completed is TimerState.TimerCompleted)
        assertTrue(completed.isCompleted)
        assertFalse(completed.isRunning)
        assertFalse(completed.isPaused)
        assertFalse(completed.isCancelled)

        assertEquals(0, completed.remaining.inWholeNanoseconds)

        assertThrows<IllegalStateException> { completed.countdown() }
        assertThrows<IllegalStateException> { completed.pause() }
        assertDoesNotThrow { completed.cancel() }
    }

    @Test
    fun testNotInitiated() {
        val tmr = TimerState.TimerNotInitiated

        assertThrows<IllegalStateException> { tmr.countdown() }
        assertThrows<IllegalStateException> { tmr.pause() }
        assertThrows<IllegalStateException> { tmr.resume() }
    }
}
