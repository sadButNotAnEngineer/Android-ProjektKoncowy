package com.example.projektkoncowy

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import android.text.TextWatcher


class SelectionScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_myTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        setContentView(R.layout.activity_selection_screen)

        findViewById<EditText>(R.id.duration)
            .setText((application as MyApp).readingsLen.toString())

        findViewById<Button>(R.id.button).setOnClickListener {
            var graphIntent = Intent(this, TemperatureGraph::class.java)
            val group : RadioGroup = findViewById(R.id.group)

            when (group.checkedRadioButtonId) {
                R.id.temperatureButton -> {
                    Log.d("selected", "Temperature")
                    graphIntent = Intent(this, TemperatureGraph::class.java)
                }

                R.id.humidityButton -> {
                    Log.d("selected", "Humidity")
                    graphIntent = Intent(this, HumidityGraph::class.java)
                }

                R.id.pressureButton -> {
                    Log.d("selected", "Pressure")
                    graphIntent = Intent(this, PressureGraph::class.java)
                }
            }


                (application as MyApp).readingsLen =
                    findViewById<EditText>(R.id.duration).text.toString().toInt()

            startActivity(graphIntent);
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<EditText>(R.id.duration)
            .setText((application as MyApp).readingsLen.toString())
    }
}