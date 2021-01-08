package com.example.wschat.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MessageItem::class], version = 1, exportSchema = true)
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: MessageDatabase? = null
        fun create(context: Context): MessageDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MessageDatabase::class.java,
                "data.db"
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()//会清空数据
                //.addMigrations(MIGRATION_1_2)
                .build()
    }
}