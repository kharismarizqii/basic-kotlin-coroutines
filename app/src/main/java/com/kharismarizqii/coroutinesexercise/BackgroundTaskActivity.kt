package com.kharismarizqii.coroutinesexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_background_task.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class BackgroundTaskActivity : AppCompatActivity() {

    private val RETURN_1 = "Return 1"
    private val RETURN_2 = "Return 2"

    //execute both at the same time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_task)

        btn_background.setOnClickListener {
            setNewText("Clicked!")

            CoroutineScope(IO).launch {
              fakeApiRequest()
            }
        }
    }

    private fun fakeApiRequest() {
        val startTime = System.currentTimeMillis()
        val parentJob = CoroutineScope(IO).launch {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("Launching job1 in thread ${Thread.currentThread().name}")
                    setTextOnMainThread(getResult1FromApi())
                }
                println("debug: completed job1 in $time1 ms")
            }
            //on is not waiting for the other one
            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("Launching job2 in thread ${Thread.currentThread().name}")
                    setTextOnMainThread(getResult2FromApi())
                }
                println("debug: completed job2 in $time2 ms")
            }
        }
        parentJob.invokeOnCompletion {
            println("debug: the entire job completed in ${System.currentTimeMillis() - startTime}")
        }
    }

    private suspend fun setTextOnMainThread(text: String){
        withContext(Main){
            setNewText(text)
        }
    }

    private fun setNewText(text: String){
        val newText = tv_background.text.toString() + "\n${text}"
        tv_background.text = newText
    }

    private suspend fun getResult1FromApi(): String{
        logThread("getResult1FromApi")
        delay(1000)
        return RETURN_1
    }

    private suspend fun getResult2FromApi(): String{
        logThread("getResult2FromApi")
        delay(1700)
        return RETURN_2
    }

    private fun logThread(methodName: String){
        println("DEBUG: $methodName : ${Thread.currentThread().name}")
    }
}
