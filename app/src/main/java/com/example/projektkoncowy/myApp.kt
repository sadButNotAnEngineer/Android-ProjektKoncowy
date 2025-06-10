package com.example.projektkoncowy

import android.app.Application
import android.util.Log
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Runnable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ConnectException
import java.net.Socket
import java.time.Instant.now


class MyApp: Application() {
    private lateinit var socket: Socket
    @Suppress("PropertyName")
    val DHT_RH = mutableListOf<Entry?>()
    @Suppress("PropertyName")
    val DHT_T = mutableListOf<Entry?>()
    @Suppress("PropertyName")
    val BMA_P = mutableListOf<Entry?>()
    @Suppress("PropertyName")
    val BMA_T = mutableListOf<Entry?>()

    var readingsLen: Int = 24
    var t0: Long = 0

    var dataReadyCallback: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        internetThread.start()
    }
    val internetThread = Thread {
        try {
            socket = Socket("145.239.88.95", 20000)

//            Timer("connectionCheck").schedule( object : TimerTask() {
//                override fun run() {
//                    Log.d("socket", "isConnected: ${socket.isConnected} isClosed: ${socket.isClosed}")
//                }
//            },0L,1000L)

            socket.outputStream.write( "get $readingsLen".toByteArray())
            val reader = BufferedReader(InputStreamReader(socket.inputStream))
            var line = reader.readLine()

            DHT_RH.clear()
            BMA_P.clear()
            DHT_T.clear()
            BMA_T.clear()

            t0 = now().epochSecond

            while (!line.isEmpty()) {
                // data ordnung UNIX_TIMESTAMP(time), DHT_RH, DHT_T, BMA_P, BMA_T
                val temp = line.split(',')
                val time = (temp[0].toInt() - t0).toFloat()
                DHT_RH.add(Entry(time,temp[1].toFloat()))
                DHT_T.add(Entry(time,temp[2].toFloat()))
                BMA_P.add(Entry(time,temp[3].toFloat()))
                BMA_T.add(Entry(time,temp[4].toFloat()))
                line = reader.readLine()
            }

        } catch (e: ConnectException){
            Log.d("socket", "connect exception: $e")
            Thread.currentThread().join()
        } catch(e: Exception) {
            Log.d("socket", "exception: $e")
        }
        android.os.Handler(mainLooper).postDelayed({dataReadyCallback?.run()},50L)
        Thread.sleep(10000)
    }

}