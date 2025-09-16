package com.marcuspereira.fittrack

import androidx.room.Embedded
import androidx.room.Relation

data class ActivityWithCategory(
    @Embedded val activityEntity: ActivityEntity,
    @Relation(
        parentColumn = "icon",
        entityColumn = "icon"
    )
    val categoryEntity: CategoryEntity
)
