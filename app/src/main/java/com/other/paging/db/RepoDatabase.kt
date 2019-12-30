package com.other.paging.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.other.paging.model.Repo

@Database(entities = [Repo::class], version = 1, exportSchema = false)
abstract class RepoDatabase : RoomDatabase() {

    abstract fun reposDao(): RepoDao

    companion object {

        @Volatile
        private var INSTANCE: RepoDatabase? = null

        fun getInstance(context: Context): RepoDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        RepoDatabase::class.java, "Github.db")
                        .build()
    }
}
