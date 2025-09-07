package com.chuka.nav3libwithclaude.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chuka.nav3libwithclaude.data.dao.HumanDao
import com.chuka.nav3libwithclaude.data.entities.HumanEntity

@Database(entities = [HumanEntity::class], version = 2)
abstract class HumanDatabase : RoomDatabase() {
    abstract fun humanDao(): HumanDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE humans ADD COLUMN rank INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}