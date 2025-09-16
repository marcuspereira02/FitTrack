package com.marcuspereira.fittrack

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ActivityListAdapter :
    ListAdapter<ActivityUiData, ActivityListAdapter.ActivityViewHolder>(DIFF_CALLBACK) {

    private lateinit var callback: (ActivityUiData) -> Unit

    fun setOnClickListener(onClick: (ActivityUiData) -> Unit) {
        callback = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = getItem(position)
        holder.bind(activity, callback)
    }

    class ActivityViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val ivCategory = view.findViewById<ImageView>(R.id.iv_category_activity)
        private val tvCategory = view.findViewById<TextView>(R.id.tv_category)
        private val tvDuration = view.findViewById<TextView>(R.id.tv_distance_activity)
        private val tvDescriptionTwo = view.findViewById<TextView>(R.id.tv_time_activity)
        private val categoryColor = view.findViewById<View>(R.id.view_ribbon)


        fun bind(activity: ActivityUiData, callback: (ActivityUiData) -> Unit) {

            val duration = activity.textOne
            val distance = activity.textTwo
            val background = categoryColor.background

            ivCategory.setImageResource(activity.icon)
            tvCategory.text = activity.titleCategory
            tvDuration.text = "$duration min"

            if(background is GradientDrawable){
                background.setColor(activity.color)
            }

            if (activity.icon == R.drawable.ic_yoga){
                tvDescriptionTwo.isVisible = false
            }

            if (distance.isNotEmpty()) {
                if (activity.icon == R.drawable.ic_weight) {
                    tvDescriptionTwo.text = "  -  $distance kg"
                } else {
                    tvDescriptionTwo.text = "  -  $distance km"
                }
            }

            view.setOnClickListener {
                callback.invoke(activity)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ActivityUiData>() {
            override fun areItemsTheSame(
                oldItem: ActivityUiData,
                newItem: ActivityUiData
            ): Boolean {
                return oldItem.textOne == newItem.textOne
            }

            override fun areContentsTheSame(
                oldItem: ActivityUiData,
                newItem: ActivityUiData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
