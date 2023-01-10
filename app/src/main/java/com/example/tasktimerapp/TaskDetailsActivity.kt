package com.example.tasktimerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.tasktimerapp.databinding.ActivityTaskDetailsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class TaskDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailsBinding

    private val mInterval = 1 // 1 second in this case
    private var mHandler: Handler? = null
    private var timeInSeconds = 0L
    private var startButtonClicked = false

    private var taskName = ""

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStopWatch()

        val data: Task = intent.getSerializableExtra("Task") as Task
        taskName = data.taskName
        binding.txtTaskName.text = taskName
        binding.txtTaskDetails.text = data.taskDetails
        binding.txtTaskTimer.text = reformat(data.timeSpent)


        binding.btnStart.setOnClickListener {
            startOrStopButtonClicked()
        }

        binding.btnReset.setOnClickListener {
            resetTimerView()
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

    private fun reformat(ms: Long): String {
        var milliseconds = ms * 1000
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }


    private fun initStopWatch() {
        binding.txtTaskTimer.text = "00:00:00"
    }

    private fun startOrStopButtonClicked() {
        if (!startButtonClicked) {
            startTimer()
            startTimerView()
        } else {
            stopTimer()
            stopTimerView()
        }

    }

    private fun startTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()
    }

    private fun startTimerView() {
        binding.btnStart.text = "stop"
        startButtonClicked = true
    }

    private fun stopTimer() {
        mHandler?.removeCallbacks(mStatusChecker)
    }

    private fun stopTimerView() {
        binding.btnStart.text = "resume"
        update(taskName,timeInSeconds)
        startButtonClicked = false
    }

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                timeInSeconds += 1
                binding.txtTaskTimer.text = getFormattedStopWatch(timeInSeconds)

            } finally {

                mHandler!!.postDelayed(this, mInterval.toLong())
            }
        }
    }

    private fun resetTimerView() {
        timeInSeconds = 0
        startButtonClicked = false
        binding.btnStart.text = "start"
        update(taskName,timeInSeconds)
        initStopWatch()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }


    private fun update(taskName: String, newTime: Long) {
        db.collection("Tasks")
            .whereEqualTo("taskName", taskName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val id = document.id
                    updateTime(newTime, id)
                }
            }
    }

    private fun updateTime(newTime: Long, id: String) {
        db.collection("Tasks")
            .document(id)
            .update("timeSpent", newTime)
            .addOnSuccessListener {
                Log.w("TAG", "Updated Successfully for $id")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }
}

