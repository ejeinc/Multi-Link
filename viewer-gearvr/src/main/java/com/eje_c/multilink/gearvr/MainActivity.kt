package com.eje_c.multilink.gearvr

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.eje_c.multilink.udp.MultiLinkUdpMessenger
import com.eje_c.multilink.udp.UdpSocketService
import org.gearvrf.GVRActivity

class MainActivity : GVRActivity() {
    private val TAG = "MainActivity"

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            Log.d(TAG, "onServiceConnected: ")

            val udpSocket = (binder as UdpSocketService.LocalBinder).service.udpSocket
            MultiLinkUdpMessenger.initialize(udpSocket)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(TAG, "onServiceDisconnected: ")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activity開始と同時にUDP受信サービスを開始。終了時に停止する。
        bindService(Intent(this, UdpSocketService::class.java), conn, BIND_AUTO_CREATE)

        main = App(this)
    }

    override fun onDestroy() {
        unbindService(conn)
        super.onDestroy()
    }
}
