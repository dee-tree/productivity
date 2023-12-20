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


@RunWith(AndroidJUnit4::class)
class ActionTemplateDaoTest : AppDatabaseTest() {
    private lateinit var templateDao: ActionsTemplateDao

    @Before
    override fun init() {
        super.init()
        templateDao = db.actionsTemplateDao()
    }

    companion object {
        const val MAX_TEMPLATES_COUNT = 128
    }

    @Test
    @Throws(IOException::class)
    fun testWriteAndRead() = runTest {
        val templatesCount = (0..MAX_TEMPLATES_COUNT).random()
        Log.d("TEST", "Going to create $templatesCount templates")
        val templates = (1..templatesCount).map { ActionTemplateGenerator.generate() }

        templateDao.insert(*templates.toTypedArray())

        templateDao.getAll().take(1).collect { dbActions ->
            assertEquals(templatesCount, dbActions.size)
            dbActions.forEach { assertTrue(it in templates) }
        }
    }

    @Test
    @Throws(IOException::class)
    fun testCreateAndDelete() = runTest {
        val templatesCount = (0..MAX_TEMPLATES_COUNT).random()
        Log.d("TEST", "Going to create $templatesCount templates")
        val templates = (1..templatesCount).map { ActionTemplateGenerator.generate() }

        templateDao.insert(*templates.toTypedArray())

        val fetchedActions = templateDao.getAll().take(1).single()
        assertEquals(templatesCount, fetchedActions.size)
        fetchedActions.forEach { assertTrue(it in templates) }

        fetchedActions.forEach {
            templateDao.delete(it)
            assertFalse(it in templateDao.getAll().take(1).single())
        }

        assertTrue(templateDao.getAll().take(1).single().isEmpty())

    }
}
