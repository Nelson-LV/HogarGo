package com.hogargo.app.data.local

import androidx.room.TypeConverter
import com.hogargo.app.data.TaskCategory
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

    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String = category.name

    @TypeConverter
    fun toTaskCategory(value: String): TaskCategory =
        runCatching { TaskCategory.valueOf(value) }.getOrDefault(TaskCategory.GENERAL)
}
