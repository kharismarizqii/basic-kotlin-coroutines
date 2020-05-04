package com.kharismarizqii.coroutinesexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var RETURN_1 = "First Api Result"
    private var RETURN_2 = "Second Api Result"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_crt.setOnClickListener{
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }
    }

    private suspend fun setTextString(text: String){
        setText(tv_text.text.toString() + "\n${text}")
    }

    private suspend fun setText(text: String){
        withContext(Main){
            tv_text.text = text.toString()
        }
    }
    private suspend fun fakeApiRequest() {
        val result1 = getResult1FromApi()
        setTextString(result1)
        val result2 = getResult2FromApi()
        setTextString(result2)
    }


    private suspend fun getResult1FromApi(): String{
        logThread("getResult1FromApi")
        delay(1000)
        return RETURN_1
    }

    private suspend fun getResult2FromApi(): String{
        logThread("getResult2FromApi")
        delay(1000)
        return RETURN_2
    }

    private fun logThread(methodName: String){
        println("DEBUG: $methodName : ${Thread.currentThread().name}")
    }
}
