package com.example.tasktimerapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktimerapp.databinding.CustomTaskBinding
import java.util.concurrent.TimeUnit

class TaskAdapter(
    private val activity: Activity,
    private val list: MutableList<Task>,
    private val onClick: (task: Task) -> Unit
) :
    RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {


    inner class MyViewHolder(b: CustomTaskBinding) :
        RecyclerView.ViewHolder(b.root) {
        val binding: CustomTaskBinding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val b: CustomTaskBinding =
            CustomTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(b)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = list[position]
        holder.binding.apply {
            txtTaskName.text = data.taskName
            txtTaskDetails.text = data.taskDetails
            txtTaskTimer.text = getFormattedStopWatch(data.timeSpent)
            imgCancel.setOnClickListener {
                onClick(data)

            }
            root.setOnClickListener {
                val intent = Intent(activity, TaskDetailsActivity::class.java)
                intent.putExtra("Task", data)
                activity.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}