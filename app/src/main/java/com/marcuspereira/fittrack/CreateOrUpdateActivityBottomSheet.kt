package com.marcuspereira.fittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class CreateOrUpdateActivityBottomSheet(
    private val categoryList: List<CategoryEntity>,
    private val activity: ActivityUiData? = null,
    private val onCreateClicked: (ActivityUiData) -> Unit,
    private val onUpdateClicked: (ActivityUiData) -> Unit,
    private val onDeleteClicked: (ActivityUiData) -> Unit
) : BottomSheetDialogFragment() {

    private var selectedCategory: Int? = null
    private val categoryAdapter = CategoryListAdapter()
    private val selectedCategoryColor =
        categoryList.find { it.icon == selectedCategory }?.color ?: 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_activity_bottom_sheet, container, false)

        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_category_activity_bottom_sheet)
        val titleBottomSheet =
            view.findViewById<TextView>(R.id.tv_activity_title_label_bottom_sheet)
        val tieTitleActivity = view.findViewById<TextView>(R.id.tie_activity_title)
        val tieDuration = view.findViewById<TextView>(R.id.tie_duration)
        val tieTextTwo = view.findViewById<TextView>(R.id.tie_text_two)
        var textTwoLabel = view.findViewById<TextView>(R.id.tv_two_label)
        val tilTwoLabel = view.findViewById<TextInputLayout>(R.id.til_text_two)

        val btnCancelOrDelete = view.findViewById<Button>(R.id.btn_cancel_activity_bottom_sheet)
        val btnCreateOrUpdate = view.findViewById<Button>(R.id.btn_create_activity_bottom_sheet)

        fun updateTextTwoLabel() {
            val stringWeight = getString(R.string.full_load)
            val stringDistance = getString(R.string.distance_traveled)

            textTwoLabel.text = when (selectedCategory) {
                R.drawable.ic_weight -> stringWeight
                else -> stringDistance
            }

            if (selectedCategory == R.drawable.ic_yoga) {
                textTwoLabel.isVisible = false
                tilTwoLabel.isVisible = false
                tieTextTwo.isVisible = false
            } else {
                textTwoLabel.isVisible = true
                tilTwoLabel.isVisible = true
                tieTextTwo.isVisible = true
            }
        }

        categoryAdapter.setOnClickListener { category ->
            val listTemp = categoryAdapter.currentList.map {
                it.copy(isSelected = it.icon == category.icon)
            }
            categoryAdapter.submitList(listTemp)
            selectedCategory = category.icon

            updateTextTwoLabel()
        }

        val categoryListTemp = if (activity != null) {
            categoryList.map {
                CategoryUiData(
                    icon = it.icon,
                    color = it.color,
                    isSelected = it.icon == activity.icon
                ).also { uiData ->
                    if (uiData.isSelected) selectedCategory = uiData.icon
                }
            }
        } else {
            categoryList.map {
                CategoryUiData(
                    icon = it.icon,
                    color = it.color,
                    isSelected = false
                )
            }
        }

        if (activity == null) {
            btnCancelOrDelete.setText(R.string.cancel)
            titleBottomSheet.setText(R.string.create_activity)
            btnCreateOrUpdate.setText(R.string.create)
        } else {
            btnCancelOrDelete.setText(R.string.delete)
            titleBottomSheet.setText(R.string.update_activity)
            btnCreateOrUpdate.setText(R.string.update)
            tieTitleActivity.setText(activity.titleCategory)
            tieDuration.setText(activity.textOne)
            tieTextTwo.setText(activity.textTwo)

            updateTextTwoLabel()
        }

        btnCreateOrUpdate.setOnClickListener {
            val activityTitleNull = getString(R.string.write_activity)
            val activityDurationNull = getString(R.string.write_duration)
            val categoryNull = getString(R.string.select_category)
            val distanceNull = getString(R.string.distance_empty)

            val activityTitle = tieTitleActivity.text.toString().trim()
            val activityDuration = tieDuration.text.toString().trim()
            val distanceActivity = tieTextTwo.text.toString().trim()

            when {
                selectedCategory == null -> {
                    Snackbar.make(btnCreateOrUpdate, categoryNull, Snackbar.LENGTH_LONG).show()
                }

                activityTitle.isEmpty() -> {
                    Snackbar.make(btnCreateOrUpdate, activityTitleNull, Snackbar.LENGTH_LONG).show()
                }

                activityDuration.isEmpty() -> {
                    Snackbar.make(btnCreateOrUpdate, activityDurationNull, Snackbar.LENGTH_LONG)
                        .show()
                }

                distanceActivity.isEmpty() && selectedCategory != R.drawable.ic_yoga -> {
                    Snackbar.make(btnCreateOrUpdate, distanceNull, Snackbar.LENGTH_LONG).show()
                }

                else -> {
                    val selectedCategoryColor =
                        categoryList.find { it.icon == selectedCategory }?.color ?: 0

                    if (activity == null) {
                        onCreateClicked.invoke(
                            ActivityUiData(
                                id = 0,
                                icon = requireNotNull(selectedCategory),
                                titleCategory = activityTitle,
                                textOne = activityDuration,
                                textTwo = distanceActivity,
                                color = selectedCategoryColor
                            )
                        )
                    } else {
                        onUpdateClicked.invoke(
                            ActivityUiData(
                                id = activity.id,
                                icon = requireNotNull(selectedCategory),
                                titleCategory = activityTitle,
                                textOne = activityDuration,
                                textTwo = distanceActivity,
                                color = activity.color
                            )
                        )
                    }
                    dismiss()
                }
            }
        }

        rvCategory.adapter = categoryAdapter
        categoryAdapter.submitList(categoryListTemp)

        btnCancelOrDelete.setOnClickListener {
            if (activity != null) {
                onDeleteClicked.invoke(activity)
                dismiss()
            } else {
                dismiss()
            }
        }

        return view
    }
}