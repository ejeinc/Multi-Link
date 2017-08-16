package com.eje_c.multilink.controller

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.eje_c.multilink.controller.db.DeviceEntity
import com.eje_c.multilink.controller.db.VideoEntity
import com.eje_c.multilink.data.DeviceInfo
import com.eje_c.multilink.udp.MultiLinkUdpMessenger
import com.eje_c.multilink.udp.UdpSocketService
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Application's main entry point.
 */
class MainActivity : AppCompatActivity() {

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {

            val udpSocket = (binder as UdpSocketService.LocalBinder).service.udpSocket
            MultiLinkUdpMessenger.initialize(udpSocket)
            MultiLinkUdpMessenger.onReceivePingResponse += this@MainActivity::processDeviceInfo
            MultiLinkUdpMessenger.ping()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {

            // Clear references
            MultiLinkUdpMessenger.release()
        }
    }

    /**
     * Process {type: Message.TYPE_PING} message from remote.
     */
    private fun processDeviceInfo(deviceInfo: DeviceInfo) {

        val now = SystemClock.uptimeMillis()

        // Save device info to local DB
        val deviceEntity = DeviceEntity().apply {
            imei = deviceInfo.imei
            name = deviceInfo.name
            updatedAt = now
        }
        App.db.deviceDao.create(deviceEntity)

        // Save video info to local DB
        deviceInfo.videos.let { videos ->

            val videoEntities = videos.map { video ->
                VideoEntity().apply {
                    deviceImei = deviceInfo.imei
                    path = video.path
                    name = video.name
                    length = video.length
                    updatedAt = now
                }
            }

            App.db.videoDao.create(videoEntities)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start UDP service
        bindService(Intent(applicationContext, UdpSocketService::class.java), conn, Context.BIND_AUTO_CREATE)

        setSupportActionBar(toolbar)

        container.adapter = SectionsPagerAdapter(supportFragmentManager)

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    override fun onDestroy() {

        // Stop UDP service
        unbindService(conn)

        super.onDestroy()
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> DevicesFragment_.builder().build()
                1 -> VideosFragment_.builder().build()
                else -> throw IllegalStateException("No more tabs")
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
