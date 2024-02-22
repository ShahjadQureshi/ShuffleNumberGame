package com.example.shufflenumber

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.math.abs
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private var buttons = arrayOf<Button>()
    private lateinit var b: Button
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var tView: TextView
    lateinit var pauseButton:Button
    lateinit var loadButton: Button

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("tableLayoutApp", Context.MODE_PRIVATE)

        tView = findViewById<TextView>(R.id.timerView)
        pauseButton = findViewById<Button>(R.id.PauseButton)
        loadButton = findViewById<Button>(R.id.LoadButton)

        buttons = arrayOf<Button>(
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8),
            findViewById(R.id.btnEmpty)
        )

        b = buttons[8]
        countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val elapsedSeconds = diff / secondsInMilli
                tView.text = "$elapsedSeconds"
            }

            override fun onFinish() {
                b.text = "Looser"
            }
        }

        pauseButton.setOnClickListener(){
            countDownTimer.cancel()
            saveGameState()

        }

        loadButton.setOnClickListener(){
            restoreGameState()
            countDownTimer.start()
        }

    }


    fun clickStart(v: View) {
        var random = Random
        var digits = (1..8).toList().shuffled(random)
        for (i in 0..7) {
            buttons[i].text = digits[i].toString()
        }
        b = buttons[8]
        b.text = ""
        countDownTimer.start()
    }


    fun clickButton(v: View) {

        val clickedButton = v as Button

        val cb = intArrayOf(0, 0)
        val eb = intArrayOf(0, 0)
        clickedButton.getLocationInWindow(cb)
        b.getLocationInWindow(eb)
//        var s=cb[0].toString()+","+cb[1].toString()+"\n"+eb[0].toString()+","+eb[1].toString()
//        b.text = s
        if ((cb[0] == eb[0] && abs(cb[1] - eb[1]) == b.height) || (cb[1] == eb[1] && abs(cb[0] - eb[0]) == b.width)) {
            b.text = clickedButton.text
            clickedButton.text = ""
            b = clickedButton
        }

        var i=0
        while(i <= 7){
            if(buttons[i].text != (i+1).toString())
                break;
            i++
        }

        if(i == 8) {
            b.text = "Winner"
            countDownTimer.cancel()
        }

    }

    private  fun saveGameState(){
        // val editor = getSharedPreferences("tableLayoutApp", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val buttonValues = buttons.map {it.text.toString()}.toTypedArray()
        editor.putString("ClickedButtonValue", buttonValues.joinToString(","))
        editor.putString("Timer", tView.text.toString())
        editor.apply()
    }

    private fun restoreGameState() {
        // val prefs = getSharedPreferences("tableLayoutApp", Context.MODE_PRIVATE)
        val buttonValues = prefs.getString("ClickedButtonValue", "")?.split(",")?.toTypedArray()
        val timerValue = prefs.getString("Timer", "0")

        if (!buttonValues.isNullOrEmpty()) {
            for (i in buttons.indices) {
                buttons[i].text = buttonValues[i]
            }
        }

        countDownTimer = object : CountDownTimer(timerValue!!.toLong()*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val elapsedSeconds = diff / secondsInMilli
                tView.text = "$elapsedSeconds"
            }

            override fun onFinish() {
                b.text = "Looser"
            }
        }

    }

}
