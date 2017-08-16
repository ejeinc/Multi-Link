package com.eje_c.multilink.udp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.IBinder
import android.util.Log

class UdpSocketService : Service() {

    private val tag = "UdpSocketService"
    private lateinit var lock: WifiManager.MulticastLock

    lateinit var udpSocket: UdpSocket

    inner class LocalBinder : Binder() {
        val service: UdpSocketService
            get() = this@UdpSocketService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate")

        // Enable multicast/broadcast
        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        lock = wifi.createMulticastLock("lock")
        lock.acquire()

        udpSocket = UdpSocket(port = MultiLinkUdpMessenger.broadcastPort)

    }

    override fun onDestroy() {
        Log.d(tag, "onDestroy")

        udpSocket.release()
        lock.release()

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(tag, "onBind")
        return LocalBinder()
    }

}