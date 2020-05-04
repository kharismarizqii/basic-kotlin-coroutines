package com.kharismarizqii.coroutinesexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_coroutines_job.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class CoroutinesJobActivity : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_BAR = 0
    private val JOB_TIME = 4000 //ms
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutines_job)

        job_btn.setOnClickListener {
            if (!::job.isInitialized) //lateinit
            {
                initJob()
            }
            job_pb.startJobOrCancel(job)
        }
    }

    fun ProgressBar.startJobOrCancel(job: Job){
        if (this.progress > 0){
            println("$job is already active, Cancelling...")
            resetJob()
        }else{
            job_btn.setText("Cancel Job #1")
            CoroutineScope(IO +job).launch{
                println("coroutine $this is activated with this job $job")

                for (i in PROGRESS_BAR .. PROGRESS_MAX){
                    delay((JOB_TIME/PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTV("Job Is Complete")
            }
            //if u want to cancel spesific job just
            //job.cancel we dont have to shut down the entire coroutine
        }
    }

    private fun updateJobCompleteTV(text: String){
        GlobalScope.launch(Main){
            job_complete_text.text = text
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted){
            job.cancel(CancellationException("resetting job"))
        }
        initJob()
        //once you cancel the job u can't reuse the job so must create a new one
    }

    fun initJob(){
        job_btn.setText("StartJob #1")
        updateJobCompleteTV("")
        job = Job()
        //handle when the job is done
        job.invokeOnCompletion {
            it?.message.let{
                var msg = it
                if (msg.isNullOrBlank()){
                    msg = "Unknown cancellation error"
                }
                println("$job was cancelled. Reason: $msg")
                showToasted(msg)
            }
        }
        job_pb.max = PROGRESS_MAX
        job_pb.progress = PROGRESS_BAR
    }

    fun showToasted(text: String){
        GlobalScope.launch(Main) {
            Toast.makeText(this@CoroutinesJobActivity, text, Toast.LENGTH_SHORT).show()
        }
    }
}
