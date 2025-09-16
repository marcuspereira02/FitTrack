package com.marcuspereira.fittrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ActivityDao {

    @Transaction
    @Query("Select * From ActivityEntity")
    fun getActivitiesWithCategory(): List<ActivityWithCategory>

    @Transaction
    @Query("Select * From activityEntity where icon is :categoryIcon")
    fun getAllByCategoriesWithRelation(categoryIcon: Int): List<ActivityWithCategory>

    @Query("Select * From activityEntity where icon is :categoryIcon")
    fun getAllByCategories(categoryIcon: Int): List<ActivityEntity>

    @Transaction
    @Query("Select * From activityentity")
    fun getAll(): List<ActivityWithCategory>

    @Insert
    fun insert(activityEntity: ActivityEntity)

    @Delete
    fun delete(activityEntity: ActivityEntity)

    @Delete
    fun deleteAll(activityEntity: List<ActivityEntity>)

    @Update
    fun update(activityEntity: ActivityEntity)
}