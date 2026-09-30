package com.hogargo.app.data.local

import androidx.room.TypeConverter
import com.hogargo.app.data.TaskCategory

class Converters {
    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String = category.name

    @TypeConverter
    fun toTaskCategory(value: String): TaskCategory =
        runCatching { TaskCategory.valueOf(value) }.getOrDefault(TaskCategory.GENERAL)
}
