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
import java.util.TimeZone


class HumidityGraph : Activity() {
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

    fun syncChart() {
        Log.d("ithread state", (application as MyApp).internetThread.state.toString())
        if ((application as MyApp).internetThread.state in listOf(
                Thread.State.WAITING,
                Thread.State.TIMED_WAITING,
                Thread.State.TERMINATED
            )
        ) {

            (application as MyApp).DHT_RH.removeIf { (it!!.y < 30) or (it.y > 80) }

            val line = LineDataSet((application as MyApp).DHT_RH, "DHT_RH")
            line.setColor(getColor(R.color.line_red))

            line.setDrawCircles(false)
            line.lineWidth = 3f

            chart.clear()
            chart.setData(LineData(line))
            Log.d("graph", "pressure")
        }
    }
}