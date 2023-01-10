package com.example.tasktimerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.tasktimerapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@SuppressLint("NotifyDataSetChanged")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val db = Firebase.firestore
    private var listTasks = mutableListOf<Task>()
    private var total = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getTasks()

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    private fun getTasks() {
        total = 0L
        db.collection("Tasks")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")

                    val task = Task(
                        document.getString("taskName").toString(),
                        document.getString("taskDetails").toString(),
                        document.getLong("timeSpent")!!.toLong(),
                    )
                    listTasks.add(task)
                    total += task.timeSpent
                }
                binding.recTasks.adapter = TaskAdapter(this, listTasks) {
                    delete(it.taskName)
                }
                binding.txtTotalTime.text = "Total: ${getFormattedStopWatch(total)}"
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun delete(taskName: String) {
        total = 0L
        db.collection("Tasks")
            .whereEqualTo("taskName", taskName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val id = document.id
                    db.collection("Tasks")
                        .document(id)
                        .delete()
                        .addOnSuccessListener {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                listTasks.removeIf { it.taskName == taskName }
                                binding.recTasks.adapter?.notifyDataSetChanged()
                            }

                            for (task in listTasks) {
                                total += task.timeSpent
                                binding.txtTotalTime.text = "Total: ${getFormattedStopWatch(total)}"
                            }

                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                        }
                }
            }
    }

}