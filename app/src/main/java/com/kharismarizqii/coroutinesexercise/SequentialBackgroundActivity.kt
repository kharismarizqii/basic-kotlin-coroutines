package com.kharismarizqii.coroutinesexercise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sequential_background.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class SequentialBackgroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequential_background)

        btn_sequential.setOnClickListener {
            fakeApiRequest()
        }
    }

    private fun fakeApiRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1 = async {
                    println("debug: launching job1 ${Thread.currentThread().name}")
                    getResult1FromApi()
                }.await()

                val result2 = async {
                    println("debug: launching job2 ${Thread.currentThread().name}")
                    getResult2FromApi(result1)
                }.await()

                println("debug: got result2: $result2")
                setTextToMainThread(result2)
            }
            println("debug: total elapsed time $executionTime ms")
        }
    }

    private fun setNewText(text: String) {
        val newText = "${tv_sequential.text} \n$text"
        tv_sequential.text = newText
    }

    private suspend fun setTextToMainThread(text: String) {
        withContext(Main) {
            setNewText(text)
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "RETURN #1"
    }

    private suspend fun getResult2FromApi(result1: String): String {
        delay(1700)
        if (result1 == "RETURN #1") {
            return "RESULT #2"
        }
        throw CancellationException("Result no 1 was incorrect")
    }
}
