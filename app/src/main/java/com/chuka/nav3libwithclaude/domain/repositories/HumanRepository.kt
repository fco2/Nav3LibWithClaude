package com.chuka.nav3libwithclaude.domain.repositories

import com.chuka.nav3libwithclaude.data.dao.HumanDao
import com.chuka.nav3libwithclaude.data.entities.HumanEntity
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.HumanType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface HumanRepository {
    fun getAllHumans(): Flow<List<Human>>

    fun getHumansByType(type: HumanType): Flow<List<Human>>

    suspend fun insertHuman(human: Human): Long?

    suspend fun insertHumans(humans: List<Human>): List<Long>

    suspend fun deleteHuman(human: Human)

    suspend fun deleteAllHumans()

    fun getHumanById(id: Long): Flow<Human>

    fun getHumansBetweenAgeRange(age: Int): Flow<List<Human>>

    suspend fun getHighestRank(): Int
}

class HumanRepositoryImpl @Inject constructor(private val humanDao: HumanDao) : HumanRepository{
    override fun getAllHumans(): Flow<List<Human>> {
        return humanDao.getAllHumans().map { humans ->
            humans.map { it.toHuman() }
        }
    }

    override fun getHumansByType(type: HumanType): Flow<List<Human>> {
        return humanDao.getHumansByType(type).map { humans ->
            humans.map { it.toHuman() }
        }
    }

    override suspend fun insertHuman(human: Human): Long? {
        val nextRank = getHighestRank() + 1
        val humanWithRank = human.copy(rank = nextRank)
        return humanDao.insertHuman(humanWithRank.toHumanEntity())
    }

    override suspend fun insertHumans(humans: List<Human>): List<Long>{
        var currentRank = getHighestRank()
        val humansWithRank = humans.map { human ->
            human.copy(rank = ++currentRank)
        }
        return humanDao.insertHumans(humansWithRank.map { it.toHumanEntity() })
            .mapNotNull { it?.toLong() }
    }

    override suspend fun deleteHuman(human: Human) {
        humanDao.deleteHuman(human.toHumanEntity())
    }

    override suspend fun deleteAllHumans() {
        humanDao.deleteAllHumans()
    }

    override fun getHumanById(id: Long): Flow<Human> {
        return humanDao.getHumanById(id).map { it.toHuman() }
    }

    override fun getHumansBetweenAgeRange(
        age: Int
    ): Flow<List<Human>> {
        val minAge = age - 2
        val maxAge = age + 2
        return humanDao.getHumansBetweenAgeRange(minAge, maxAge).map { humans ->
            humans.map { it.toHuman() }
        }
    }

    override suspend fun getHighestRank(): Int {
        return humanDao.getHighestRank()
    }
}