package com.chuka.nav3libwithclaude.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chuka.nav3libwithclaude.data.dao.HumanDao
import com.chuka.nav3libwithclaude.data.entities.HumanEntity

@Database(entities = [HumanEntity::class], version = 1)
abstract class HumanDatabase : RoomDatabase() {
    abstract fun humanDao(): HumanDao
}