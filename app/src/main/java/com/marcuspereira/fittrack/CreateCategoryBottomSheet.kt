package com.marcuspereira.fittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class CreateCategoryBottomSheet(val onCreatedClicked: (icon: Int, color: Int, isSelected: Boolean) -> Unit) :
    BottomSheetDialogFragment() {

    private var colorSelected: Int? = null
    private var iconSelected: Int? = null

    private val categoryAdapter = CategoryListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_category_bottom_sheet, container, false)

        val context = requireContext()

        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_category_bottom_sheet)
        val btnCreate = view.findViewById<Button>(R.id.btn_create_bottom_sheet)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel_bottom_sheet)
        val circleBlue = view.findViewById<View>(R.id.circle_blue)
        val circleGreen = view.findViewById<View>(R.id.circle_green)
        val circleMaroon = view.findViewById<View>(R.id.circle_maroon)
        val circleOrange = view.findViewById<View>(R.id.circle_orange)
        val circlePurple = view.findViewById<View>(R.id.circle_purple)
        val circleRed = view.findViewById<View>(R.id.circle_red)
        val circleYellow = view.findViewById<View>(R.id.circle_yellow)
        val circlePink = view.findViewById<View>(R.id.circle_pink)

        val colorViews = listOf(
            circlePink,
            circleYellow,
            circleBlue,
            circleRed,
            circleGreen,
            circleOrange,
            circleMaroon,
            circlePurple
        )

        rvCategory.adapter = categoryAdapter
        categoryAdapter.submitList(listOfCategory)

        categoryAdapter.setOnClickListener { category ->
            val listTemp = listOfCategory.map {
                it.copy(isSelected = it.icon == category.icon)
            }

            categoryAdapter.submitList(listTemp)
            iconSelected = category.icon
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        circleBlue.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.blue_category)
            updateColorSelection(it, colorViews)
        }

        circleGreen.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.green)
            updateColorSelection(it, colorViews)
        }

        circleMaroon.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.maroon)
            updateColorSelection(it, colorViews)
        }

        circleRed.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.red)
            updateColorSelection(it, colorViews)
        }

        circleOrange.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.orange)
            updateColorSelection(it, colorViews)
        }

        circlePurple.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.purple)
            updateColorSelection(it, colorViews)
        }

        circleYellow.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.yellow)
            updateColorSelection(it, colorViews)
        }

        circlePink.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.pink)
            updateColorSelection(it, colorViews)
        }

        btnCreate.setOnClickListener {

            val messageColor = getString(R.string.select_color)
            val messageIcon = getString(R.string.select_icon)

            val icon = iconSelected
            val color = colorSelected
            val isSelected = false

            if (icon == null) {
                Snackbar.make(btnCreate, messageIcon, Snackbar.LENGTH_LONG).show()
            } else {
                if (color == null) {
                    Snackbar.make(btnCreate, messageColor, Snackbar.LENGTH_LONG).show()
                } else {
                    onCreatedClicked(icon, color, isSelected)
                    dismiss()
                }
            }

        }

        return view
    }

    private fun updateColorSelection(selected: View, colorViews: List<View>) {
        colorViews.forEach { view ->
            view.isSelected = (view == selected)
        }
    }

    private val listOfCategory = listOf(
        CategoryUiData(
            icon = R.drawable.ic_run,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_swimming,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_weight,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_bike,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_hiking,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_yoga,
            color = 0,
            isSelected = false
        )
    )


}