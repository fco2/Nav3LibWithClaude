package com.chuka.nav3libwithclaude.data

import androidx.room.TypeConverter
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.HumanType

class Converters {
    @TypeConverter
    fun fromHumanType(value: HumanType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toHumanType(value: String?): HumanType? {
        return value?.let { HumanType.valueOf(it) }
    }
}