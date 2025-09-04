package com.chuka.nav3libwithclaude.domain.models

import android.os.Parcelable
import com.chuka.nav3libwithclaude.data.entities.HumanEntity
import com.chuka.nav3libwithclaude.domain.models.HumanType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Human(
    val id: Long? = null,
    val name: String? = null,
    val age: Int? = null,
    val gender: HumanType? = null
) : Parcelable {
    fun toHumanEntity(): HumanEntity {
        return HumanEntity(
            id = id,
            name = name,
            age = age,
            gender = gender
        )
    }
}
