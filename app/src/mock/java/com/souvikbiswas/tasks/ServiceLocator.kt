package com.souvikbiswas.tasks

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.souvikbiswas.tasks.data.FakeTasksRemoteDataSource
import com.souvikbiswas.tasks.data.source.DefaultTasksRepository
import com.souvikbiswas.tasks.data.source.TasksRepository
import com.souvikbiswas.tasks.data.source.local.TasksLocalDataSource
import com.souvikbiswas.tasks.data.source.local.ToDoDatabase
import kotlinx.coroutines.runBlocking

/**
 * A Service Locator for the [TasksRepository]. This is the mock version, with a
 * [FakeTasksRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: ToDoDatabase? = null
    @Volatile var tasksRepository: TasksRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?:
                tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        database = Room.databaseBuilder(context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db")
            .build()

        return DefaultTasksRepository(
            FakeTasksRemoteDataSource,
            TasksLocalDataSource(database!!.taskDao())
        )
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                FakeTasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            tasksRepository = null
        }
    }
}
