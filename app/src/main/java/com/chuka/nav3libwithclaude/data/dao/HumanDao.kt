package com.chuka.nav3libwithclaude.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chuka.nav3libwithclaude.data.entities.HumanEntity
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.HumanType
import kotlinx.coroutines.flow.Flow

@Dao
interface HumanDao {
    @Query("SELECT * FROM humans ORDER BY rank DESC")
    fun getAllHumans(): Flow<List<HumanEntity>>

    @Query("SELECT * FROM humans WHERE id = :id")
    fun getHumanById(id: Long): Flow<HumanEntity>

    @Query("SELECT * FROM humans WHERE gender = :type")
    fun getHumansByType(type: HumanType): Flow<List<HumanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHuman(human: HumanEntity): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHumans(humans: List<HumanEntity>): List<Long?>

    @Update
    suspend fun updateHuman(human: HumanEntity)

    @Update
    suspend fun updateHumans(humans: List<HumanEntity>)

    @Delete
    suspend fun deleteHuman(human: HumanEntity)

    @Query("DELETE FROM humans")
    suspend fun deleteAllHumans()

    @Query("SELECT * FROM humans WHERE age BETWEEN :minAge AND :maxAge")
    fun getHumansBetweenAgeRange(minAge: Int, maxAge: Int): Flow<List<HumanEntity>>

    @Query("SELECT COALESCE(MAX(rank), 0) FROM humans")
    suspend fun getHighestRank(): Int
}