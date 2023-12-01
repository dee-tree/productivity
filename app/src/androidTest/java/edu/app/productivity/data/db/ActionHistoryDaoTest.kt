package edu.app.productivity.data.db

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date


@RunWith(AndroidJUnit4::class)
class ActionHistoryDaoTest : AppDatabaseTest() {
    private lateinit var actionDao: ActionHistoryDao

    @Before
    override fun init() {
        super.init()
        actionDao = db.actionHistoryDao()
    }

    companion object {
        const val MAX_ACTIONS_COUNT = 128
    }

    @Test
    @Throws(IOException::class)
    fun testWriteAndRead() = runTest {
        val actionsSize = (0..MAX_ACTIONS_COUNT).random()
        Log.d("TEST", "Going to create $actionsSize actions")
        val actions = (1..actionsSize).map { ActionHistoryEntityGenerator.generate() }

        actionDao.insert(*actions.toTypedArray())

        actionDao.getAll().take(1).collect { dbActions ->
            assertEquals(actionsSize, dbActions.size)
            dbActions.forEach { assertTrue(it in actions) }
        }
    }

    @Test
    @Throws(IOException::class)
    fun testWriteAndReadLast() = runTest {
        val actionsSize = (0..MAX_ACTIONS_COUNT).random()
        Log.d("TEST", "Going to create $actionsSize actions")
        val actions = (1..actionsSize).map { ActionHistoryEntityGenerator.generate() }

        actionDao.insert(*actions.toTypedArray())

        val lastDays = (1..(MAX_ACTIONS_COUNT + (0..1).random())).random()

        actionDao.getAll(forLastDays = lastDays).take(1).collect { dbActions ->
            val minDate = Date.from(Instant.now().minus(lastDays.toLong(), ChronoUnit.DAYS))

            val filteredActions = actions.filter { it.completedAt >= minDate }

            assertEquals(filteredActions.size, dbActions.size)
            dbActions.forEach { assertTrue(it in filteredActions) }

            Log.d("TEST", "Fetched ${filteredActions.size} actions for last $lastDays days")
        }
    }

    @Test
    @Throws(IOException::class)
    fun testCreateAndDelete() = runTest {
        val actionsSize = (0..MAX_ACTIONS_COUNT).random()
        Log.d("TEST", "Going to create $actionsSize actions")
        val actions = (1..actionsSize).map { ActionHistoryEntityGenerator.generate() }

        actionDao.insert(*actions.toTypedArray())

        val fetchedActions = actionDao.getAll().take(1).single()
        assertEquals(actionsSize, fetchedActions.size)
        fetchedActions.forEach { assertTrue(it in actions) }

        fetchedActions.forEach {
            actionDao.delete(it)
            assertFalse(it in actionDao.getAll().take(1).single())
        }

        assertTrue(actionDao.getAll().take(1).single().isEmpty())

    }
}
