package edu.app.productivity.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
abstract class AppDatabaseTest {
    private lateinit var _db: AppDatabase

    val db: AppDatabase
        get() = _db

    @Before
    open fun init() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        _db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java).build()
    }

    @After
    @Throws(IOException::class)
    open fun destroy() {
        _db.close()
    }
}