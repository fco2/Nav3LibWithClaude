package com.chuka.nav3libwithclaude.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chuka.nav3libwithclaude.data.entities.HumanEntity
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.HumanType
import kotlinx.coroutines.flow.Flow

@Dao
interface HumanDao {
    @Query("SELECT * FROM humans")
    fun getAllHumans(): Flow<List<HumanEntity>>

    @Query("SELECT * FROM humans WHERE id = :id")
    fun getHumanById(id: Long): Flow<HumanEntity>

    @Query("SELECT * FROM humans WHERE gender = :type")
    fun getHumansByType(type: HumanType): Flow<List<HumanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHuman(human: HumanEntity): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHumans(humans: List<HumanEntity>): List<Long?>

    @Delete
    suspend fun deleteHuman(human: HumanEntity)

    @Query("DELETE FROM humans")
    suspend fun deleteAllHumans()
}