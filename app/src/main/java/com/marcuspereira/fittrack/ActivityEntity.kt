package com.marcuspereira.fittrack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["icon"],
            childColumns = ["icon"]
        )
    ]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("icon")
    val icon: Int,
    @ColumnInfo("textOne")
    val textOne: String,
    @ColumnInfo("textTwo")
    val textTwo: String,
    @ColumnInfo("color")
    val color: Int
)
