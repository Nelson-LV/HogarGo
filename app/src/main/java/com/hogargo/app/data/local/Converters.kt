package com.hogargo.app.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromEpochDay(epochDay: Long?): LocalDate? = epochDay?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun fromCategoryName(name: String?): ExpenseCategory? = name?.let { ExpenseCategory.valueOf(it) }

    @TypeConverter
    fun toCategoryName(category: ExpenseCategory?): String? = category?.name
}
