package com.example.projektkoncowy

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

import java.time.Instant
import java.time.Instant.now
import java.util.TimeZone


class MainActivity : Activity() {
    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_myTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        setContentView(R.layout.graph_layout)

        chart = findViewById(R.id.chart)
        chart.description = null

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = 45f
        xAxis.setTextSize(10f)
        xAxis.textColor = Color.GRAY
        xAxis.setDrawGridLines(true)
        xAxis.textColor = Color.rgb(100, 100, 100)
        xAxis.setCenterAxisLabels(true)
        xAxis.setGranularity(10f)
        xAxis.valueFormatter = object: ValueFormatter(){
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val time = Instant.ofEpochSecond(value.toLong()+(application as MyApp).t0)
                    .atZone(TimeZone.getDefault().toZoneId())
                return "%d-%d %02d:%02d".format(time.dayOfMonth, time.monthValue, time.hour, time.minute)
            }
        }
        (application as MyApp).dataReadyCallback = Runnable{ syncChart() }
    }

    override fun onResume() {
        super.onResume()
        syncChart()
    }
    fun syncChart(){
        Log.d("ithread state", (application as MyApp).internetThread.state.toString())
        if((application as MyApp).internetThread.state in listOf(Thread.State.WAITING, Thread.State.TIMED_WAITING, Thread.State.TERMINATED)){

            (application as MyApp).DHT_T.removeIf { (it!!.y > 40) or (it.y < 15) }
            (application as MyApp).BMA_P.removeIf { it!!.y < 90000 }

            val lines = listOf(
                LineDataSet((application as MyApp).DHT_RH,"DHT_RH"),
                LineDataSet((application as MyApp).DHT_T,"DHT_T"),
                LineDataSet((application as MyApp).BMA_P,"BMA_P"),
                LineDataSet((application as MyApp).BMA_T,"BMA_T")
            )
            lines[0].setColor(getColor(R.color.line_blue))
            lines[1].setColor(getColor(R.color.line_red))
            lines[2].setColor(getColor(R.color.line_green))
            lines[3].setColor(getColor(R.color.line_orange))

            for (line in lines){
                line.setDrawCircles(false)
                line.lineWidth = 3f
            }

            chart.clear()
            chart.setData(LineData(lines[1], lines[3]))
        }
    }
}