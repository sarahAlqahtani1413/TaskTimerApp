package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.tasktimerapp.databinding.ActivityAddBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val taskName = binding.editTaskName.text.toString()
            val taskDetails = binding.editTaskDetails.text.toString()
            if (taskName.isNotEmpty() && taskDetails.isNotEmpty()) {
                val task = Task(taskName, taskDetails, 0)
                addTask(task)

            }
        }


    }

    private fun addTask(model: Task) {
        val task = hashMapOf(
            "taskName" to model.taskName,
            "taskDetails" to model.taskDetails,
            "timeSpent" to model.timeSpent
        )

        db.collection("Tasks")
            .add(task)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(this, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }


}