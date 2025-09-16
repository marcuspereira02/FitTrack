package com.marcuspereira.fittrack

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.room.Room
import com.marcuspereira.fittrack.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            FitTrackDataBase::class.java,
            "FitTrackDataBase"
        ).build()
    }

    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()
    }
    private val activityDao: ActivityDao by lazy {
        db.getActivityDao()
    }

    private val categoryAdapter = CategoryListAdapter()
    private var categoriesEntity = listOf<CategoryEntity>()
    private val activityAdapter = ActivityListAdapter()

    private var categories = listOf<CategoryUiData>()
    private var activities = listOf<ActivityUiData>()

    private var categorySelected: CategoryEntity = CategoryEntity(
        icon = R.drawable.ic_all_list,
        color = 0,
        isSelected = true
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnCategoryEmpty.setOnClickListener {
            showCreateCategoryBottomSheet()
        }

        binding.rbTotalActivityTime.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                updateMetrics()
            }
        }

        binding.rbTotalDistance.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                updateMetrics()
            }
        }

        binding.rbCompletedActivities.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                updateMetrics()
            }
        }

        binding.rvCategory.adapter = categoryAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDatabase()
        }

        binding.rvListActivity.adapter = activityAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getActivitiesFromDatabase()
        }

        binding.fabCreateActivity.setOnClickListener {
            showCreateActivityBottomSheet()
        }

        categoryAdapter.setOnClickListener { selected ->

            if (selected.icon == R.drawable.ic_plus) {
                showCreateCategoryBottomSheet()
            } else {

                val categoryTemp = categories.map {
                    when {
                        it.icon == selected.icon && it.isSelected -> it.copy(isSelected = true)
                        it.icon == selected.icon && !it.isSelected -> it.copy(isSelected = true)
                        it.icon != selected.icon && it.isSelected -> it.copy(isSelected = false)
                        else -> it
                    }
                }

                if (selected.icon != R.drawable.ic_all_list) {

                    filterActivityByCategoryIcon(selected.icon)

                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        getActivitiesFromDatabase()
                    }
                }
                categorySelected =
                    CategoryEntity(
                        icon = selected.icon,
                        color = selected.color,
                        isSelected = selected.isSelected
                    )
                categoryAdapter.submitList(categoryTemp)

                updateMetrics()
            }

        }

        categoryAdapter.setOnLongClickListener { categoryToBeDelete ->

            if (categoryToBeDelete.icon != R.drawable.ic_plus && categoryToBeDelete.icon != R.drawable.ic_all_list) {

                showInfoBottomSheet {
                    val categoryEntityToBeDelete = CategoryEntity(
                        icon = categoryToBeDelete.icon,
                        color = categoryToBeDelete.color,
                        isSelected = categoryToBeDelete.isSelected
                    )
                    deleteCategory(categoryEntityToBeDelete)
                }
            }
        }

        activityAdapter.setOnClickListener { activityUiData ->
            showCreateActivityBottomSheet(activityUiData)
        }

    }

    private fun updateMetrics() {

        GlobalScope.launch(Dispatchers.IO) {
            var label = ""
            var metrics = ""

            when {
                binding.rbTotalActivityTime.isChecked -> {

                    val value = getFullTime(categorySelected)
                    label = getString(R.string.total_time)
                    metrics = "$value min"
                }

                binding.rbTotalDistance.isChecked -> {
                    val length = getString(R.string.length)

                    val value = getFullDistance(categorySelected)
                    label = getString(R.string.total_distance)
                    metrics = "$value $length"
                }

                binding.rbCompletedActivities.isChecked -> {
                    val value = getTotalActivities(categorySelected)
                    if (value > 1) {
                        label = getString(R.string.activities_registered)
                    } else {
                        label = getString(R.string.registered_activity)
                    }
                    metrics = "$value"
                }
            }
            launch(Dispatchers.Main) {
                binding.tvMetricsLabel.text = label
                binding.tvMetrics.text = metrics
            }
        }
    }

    private fun getFullTime(categoryEntity: CategoryEntity): Int {

        val valueTotal: Int

        if (categoryEntity.icon == R.drawable.ic_all_list) {
            valueTotal = activities.sumOf { it.textOne.toInt() }
        } else {
            val activitiesEntity = activityDao.getAllByCategories(categoryEntity.icon)

            val activitiesUiData = activitiesEntity.map {
                ActivityUiData(
                    id = it.id,
                    titleCategory = it.title,
                    textOne = it.textOne,
                    textTwo = it.textTwo,
                    color = it.color,
                    icon = it.icon
                )
            }

            valueTotal = activitiesUiData.sumOf { it.textOne.toInt() }

        }
        return valueTotal
    }

    private fun getFullDistance(categoryEntity: CategoryEntity): Int {

        val valueTotal: Int

        if (categoryEntity.icon == R.drawable.ic_all_list) {

            valueTotal = activities.filter { it.icon != R.drawable.ic_weight }
                .sumOf { it.textTwo.toIntOrNull() ?: 0 }
        } else if (categoryEntity.icon == R.drawable.ic_weight && categoryEntity.icon == R.drawable.ic_yoga) {

            valueTotal = 0

        } else {
            val activitiesEntity = activityDao.getAllByCategories(categoryEntity.icon)

            val activitiesUiData = activitiesEntity.map {
                ActivityUiData(
                    id = it.id,
                    titleCategory = it.title,
                    textOne = it.textOne,
                    textTwo = it.textTwo,
                    color = it.color,
                    icon = it.icon
                )

            }
            valueTotal = activitiesUiData.sumOf { it.textTwo.toIntOrNull() ?: 0 }
        }
        return valueTotal
    }

    private fun getTotalActivities(categoryEntity: CategoryEntity): Int {

        val valueTotal: Int

        if (categoryEntity.icon == R.drawable.ic_all_list) {
            valueTotal = activities.size
        } else {
            val activitiesEntity = activityDao.getAllByCategories(categoryEntity.icon)
            valueTotal = activitiesEntity.size
        }
        return valueTotal
    }


    private fun getCategoriesFromDatabase() {

        val categoriesFromDataBase = categoryDao.getAll()
        categoriesEntity = categoriesFromDataBase

        GlobalScope.launch(Dispatchers.Main) {

            if (categoriesEntity.isEmpty()) {
                binding.bgInfo.isVisible = false
                binding.tvMetricsLabel.isVisible = false
                binding.tvMetrics.isVisible = false
                binding.rgMetrics.isVisible = false
                binding.tvCategoryActivityLabel.isVisible = false
                binding.rvCategory.isVisible = false
                binding.tvActivityLabel.isVisible = false
                binding.viewBackgroundActivity.isVisible = false
                binding.rvListActivity.isVisible = false
                binding.fabCreateActivity.isVisible = false
                binding.llEmpty.isVisible = true
            } else {
                binding.bgInfo.isVisible = true
                binding.tvMetricsLabel.isVisible = true
                binding.tvMetrics.isVisible = true
                binding.rgMetrics.isVisible = true
                binding.tvCategoryActivityLabel.isVisible = true
                binding.rvCategory.isVisible = true
                binding.tvActivityLabel.isVisible = true
                binding.viewBackgroundActivity.isVisible = true
                binding.rvListActivity.isVisible = true
                binding.fabCreateActivity.isVisible = true
                binding.llEmpty.isVisible = false
            }

        }


        val categoriesUiData = categoriesEntity.map {
            CategoryUiData(
                icon = it.icon,
                color = it.color,
                isSelected = it.isSelected
            )
        }.toMutableList()

        categoriesUiData.add(
            CategoryUiData(
                icon = R.drawable.ic_plus,
                color = 0,
                isSelected = false
            )
        )

        val listTemp = mutableListOf(
            CategoryUiData(
                icon = R.drawable.ic_all_list,
                color = 0,
                isSelected = true
            )
        )

        listTemp.addAll(categoriesUiData)

        GlobalScope.launch(Dispatchers.Main) {
            categories = listTemp
            categoryAdapter.submitList(categories)
        }

    }

    private fun getActivitiesFromDatabase() {
        val activitiesFromDb = activityDao.getActivitiesWithCategory()

        val activitiesEntities = activitiesFromDb

        val activitiesUiData = activitiesEntities.map {
            ActivityUiData(
                id = it.activityEntity.id,
                icon = it.activityEntity.icon,
                titleCategory = it.activityEntity.title,
                textOne = it.activityEntity.textOne,
                textTwo = it.activityEntity.textTwo,
                color = it.categoryEntity.color
            )
        }

        GlobalScope.launch(Dispatchers.Main) {
            activities = activitiesUiData
            activityAdapter.submitList(activitiesUiData)
            updateMetrics()
        }

    }

    private fun filterActivityByCategoryIcon(category: Int) {
        GlobalScope.launch(Dispatchers.IO) {

            val activitiesFromDb = if (categorySelected.icon == R.drawable.ic_all_list) {
                activityDao.getActivitiesWithCategory()
            } else {
                activityDao.getAllByCategoriesWithRelation(category)
            }

            val activityUiData: List<ActivityUiData> = activitiesFromDb.map {
                ActivityUiData(
                    id = it.activityEntity.id,
                    icon = it.activityEntity.icon,
                    titleCategory = it.activityEntity.title,
                    textOne = it.activityEntity.textOne,
                    textTwo = it.activityEntity.textTwo,
                    color = it.categoryEntity.color
                )
            }

            GlobalScope.launch(Dispatchers.Main) {
                activityAdapter.submitList(activityUiData)
            }

        }
    }

    private fun insertActivity(activityEntity: ActivityEntity) {

        GlobalScope.launch(Dispatchers.IO) {
            activityDao.insert(activityEntity)
            getActivitiesFromDatabase()
        }
    }

    private fun updateActivity(activityEntity: ActivityEntity) {

        GlobalScope.launch(Dispatchers.IO) {
            activityDao.update(activityEntity)
            filterActivityByCategoryIcon(categorySelected.icon)
            getActivitiesFromDatabase()
        }
    }

    private fun deleteActivity(activityEntity: ActivityEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            activityDao.delete(activityEntity)
            filterActivityByCategoryIcon(categorySelected.icon)
            getActivitiesFromDatabase()
        }
    }

    private fun insertCategory(categoryEntity: CategoryEntity) {

        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insert(categoryEntity)
            getCategoriesFromDatabase()
            filterActivityByCategoryIcon(categorySelected.icon)
            getActivitiesFromDatabase()
        }
    }

    private fun deleteCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            val activitiesToBeDeleted = activityDao.getAllByCategories(categoryEntity.icon)
            activityDao.deleteAll(activitiesToBeDeleted)
            categoryDao.delete(categoryEntity)
            getCategoriesFromDatabase()
            getActivitiesFromDatabase()
        }
    }

    private fun showCreateCategoryBottomSheet() {
        val createBottomSheet = CreateCategoryBottomSheet { icon, color, isSelected ->

            val categoryEntity = CategoryEntity(
                icon = icon,
                color = color,
                isSelected = isSelected
            )
            insertCategory(categoryEntity)

        }
        createBottomSheet.show(supportFragmentManager, "create_category")
    }

    private fun showCreateActivityBottomSheet(activityUiData: ActivityUiData? = null) {
        val createBottomSheet = CreateOrUpdateActivityBottomSheet(
            categoryList = categoriesEntity,
            activity = activityUiData,
            onCreateClicked = { activityToBeInsert ->
                val activityEntityInsert = ActivityEntity(
                    icon = activityToBeInsert.icon,
                    title = activityToBeInsert.titleCategory,
                    textOne = activityToBeInsert.textOne,
                    textTwo = activityToBeInsert.textTwo,
                    color = activityToBeInsert.color
                )
                filterActivityByCategoryIcon(activityEntityInsert.icon)
                insertActivity(activityEntityInsert)
            },
            onUpdateClicked = { activityToBeUpdated ->
                val activityEntityUpdated = ActivityEntity(
                    id = activityToBeUpdated.id,
                    icon = activityToBeUpdated.icon,
                    title = activityToBeUpdated.titleCategory,
                    textOne = activityToBeUpdated.textOne,
                    textTwo = activityToBeUpdated.textTwo,
                    color = activityToBeUpdated.color
                )
                updateActivity(activityEntityUpdated)
            },
            onDeleteClicked = { activityToBeDeleted ->
                val activityEntityDeleted = ActivityEntity(
                    id = activityToBeDeleted.id,
                    icon = activityToBeDeleted.icon,
                    title = activityToBeDeleted.titleCategory,
                    textOne = activityToBeDeleted.textOne,
                    textTwo = activityToBeDeleted.textTwo,
                    color = activityToBeDeleted.color
                )
                deleteActivity(activityEntityDeleted)
            }
        )
        createBottomSheet.show(supportFragmentManager, "create_activity")
    }

    private fun showInfoBottomSheet(onClick: () -> Unit) {
        val createBottomSheet = InfoBottomSheet(
            onClick
        )
        createBottomSheet.show(supportFragmentManager, "infoBottomSheet")
    }
}