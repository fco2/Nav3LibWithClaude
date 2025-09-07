package com.chuka.nav3libwithclaude.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.HumanType
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(tableName = "humans")
@Parcelize
data class HumanEntity(
    @PrimaryKey val id: Long? = null,
    val name: String? = null,
    val age: Int? = null,
    val gender: HumanType? = null,
    val rank: Int = 0
) : Parcelable {
    fun toHuman(): Human {
        return Human(
            id = id,
            name = name,
            age = age,
            gender = gender,
            rank = rank
        )
    }
}