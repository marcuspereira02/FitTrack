package com.marcuspereira.fittrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CategoryListAdapter :
    ListAdapter<CategoryUiData, CategoryListAdapter.CategoryViewHolder>(DIFF_CALLBACK) {

    private var onClick: ((CategoryUiData) -> Unit)? = null
    private var onLongClick: ((CategoryUiData) -> Unit)? = null

    fun setOnClickListener(onClick: (CategoryUiData) -> Unit) {
        this.onClick = onClick
    }

    fun setOnLongClickListener(onLongClick: (CategoryUiData) -> Unit) {
        this.onLongClick = onLongClick
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, onClick, onLongClick)
    }


    class CategoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val container = view.findViewById<View>(R.id.container)
        private val ivCategory = view.findViewById<ImageView>(R.id.iv_category)

        fun bind(
            category: CategoryUiData,
            onClick: ((CategoryUiData) -> Unit)?,
            onLongClick: ((CategoryUiData) -> Unit)?
        ) {
            container.isSelected = category.isSelected
            ivCategory.setImageResource(category.icon)

            view.setOnClickListener {
                onClick?.invoke(category)
            }

            view.setOnLongClickListener {
                onLongClick?.invoke(category)
                true
            }

        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryUiData>() {
            override fun areItemsTheSame(
                oldItem: CategoryUiData,
                newItem: CategoryUiData
            ): Boolean {
                return oldItem.icon == newItem.icon
            }

            override fun areContentsTheSame(
                oldItem: CategoryUiData,
                newItem: CategoryUiData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}

