package com.eje_c.multilink

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.eje_c.multilink.data.DeviceInfo
import com.eje_c.multilink.udp.MultiLinkUdpMessenger
import com.eje_c.player.ExoPlayerImpl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.audio.AudioProcessor
import com.google.android.exoplayer2.ext.gvr.GvrAudioProcessor
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import java.net.SocketAddress
import java.util.*

/**
 * アプリケーションのメインクラス。
 */
class MultiLinkApp(val context: Context) {

    private val gvrAudioProcessor = GvrAudioProcessor()
    val player: BasePlayer

    /**
     * trueの時かつUDP送信先がわかっている場合はヘッドトラッキング情報を毎フレーム送信する。
     */
//    var sendHeadTransform: Boolean = false

    init {
        check(Looper.getMainLooper() == Looper.myLooper(), { "Must be initialized in main thread!" })

        val renderersFactory = object : DefaultRenderersFactory(context) {
            override fun buildAudioProcessors(): Array<AudioProcessor> {
                return arrayOf(gvrAudioProcessor)
            }
        }
        val exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, DefaultTrackSelector())
        player = BasePlayer(ExoPlayerImpl(context, exoPlayer))

        // Set handler
        MultiLinkUdpMessenger.onReceivePing += ::respondWithMyDeviceInfo
        MultiLinkUdpMessenger.onReceiveControlMessage += player::updateState
    }

    /**
     * 毎フレーム更新時に呼ばれる。
     */
    fun updateHeadOrientation(x: Float, y: Float, z: Float, w: Float) {
        gvrAudioProcessor.updateOrientation(w, x, y, z)

        // ヘッドトラッキング情報の送信
//        if (sendHeadTransform) {
//            udpSender.send(x, y, z, w)
//        }
    }

    /**
     * 端末情報をコントローラーに送る。
     */
    private fun respondWithMyDeviceInfo(remote: SocketAddress) {

        // ランダムでディレイを入れてから送り返す
        Handler(Looper.getMainLooper()).postDelayed({
            MultiLinkUdpMessenger.sendDeviceInfo(DeviceInfo.get(context), remote)
        }, Random().nextInt(3000).toLong())
    }
}
