package com.example.todolist.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.model.ToDoList
import com.example.todolist.R
import com.example.todolist.common.constants.Constants
import com.example.todolist.databinding.ItemTodoListBinding
import com.example.todolist.listeners.ClickOnTaskListener

class ListViewAdapter(
    private val context: Context, private val list: ArrayList<ToDoList>,
    private var clickOnTaskListener: ClickOnTaskListener
) :
    RecyclerView.Adapter<ListViewAdapter.ListViewHolder>() {

    class ListViewHolder(val binding: ItemTodoListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        return ListViewHolder(ItemTodoListBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.tvDate.text = list[position].date
        holder.binding.tvDay.text = list[position].day
        holder.binding.tvDescription.text = list[position].description
        holder.binding.tvTitle.text = list[position].title
        holder.binding.tvType.text = list[position].type

        holder.binding.layoutParent.setOnClickListener {
            clickOnTaskListener.onTaskSelected(list[position])
        }
        val drawable: Drawable? = ContextCompat.getDrawable(
            holder.binding.root.context,
            R.drawable.rounded_list_view
        )
        val drawable2: GradientDrawable = ContextCompat.getDrawable(
            context,
            R.drawable.borderd_bg
        ) as GradientDrawable

        when (list[position].priority) {
            Constants.LOW -> {
                drawable?.setTint(ContextCompat.getColor(context, R.color.low))
                drawable2.setStroke(1, ContextCompat.getColor(context, R.color.low))
            }

            Constants.MEDIUM -> {
                drawable?.setTint(ContextCompat.getColor(context, R.color.medium))
                drawable2.setStroke(1, ContextCompat.getColor(context, R.color.medium))
            }

            Constants.HIGH -> {
                drawable?.setTint(ContextCompat.getColor(context, R.color.high))
                drawable2.setStroke(1, ContextCompat.getColor(context, R.color.high))
            }
        }
        if (list[position].type.isNullOrEmpty()) {
            drawable2.setStroke(0,Color.TRANSPARENT)
            holder.binding.tvType.background = null
        }
        holder.binding.tvType.background = drawable2
        holder.binding.cnDateView.background = drawable
    }

    override fun getItemCount(): Int {
        return list.size
    }
}