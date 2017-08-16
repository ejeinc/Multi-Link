package com.eje_c.player

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface

class MediaPlayerImpl(
        private val context: Context,
        private val mp: MediaPlayer) : Player, MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener {

    private var surface: Surface? = null

    init {
        // Add event listeners
        mp.setOnInfoListener(this)
        mp.setOnCompletionListener(this)
    }

    override val duration: Long
        get() = mp.duration.toLong()

    override var currentPosition: Long
        set(value) = mp.seekTo(value.toInt())
        get() = mp.currentPosition.toLong()

    override val isPlaying: Boolean
        get() = mp.isPlaying

    override var volume: Float = 1.0f
        set(value) {
            field = value
            mp.setVolume(value, value)
        }

    override var onRenderFirstFrame: (() -> Unit)? = null
        set(value) {
            field = value
            if (value != null) {

                mp.setOnInfoListener { mediaPlayer, what, extra ->

                    when (what) {
                        MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                            value()
                            return@setOnInfoListener true
                        }
                        else -> {
                            return@setOnInfoListener false
                        }
                    }

                }

            } else {
                mp.setOnInfoListener(null)
            }
        }

    override var onCompletion: (() -> Unit)? = null

    override val videoWidth: Int
        get() = mp.videoWidth

    override val videoHeight: Int
        get() = mp.videoHeight

    override fun pause() = mp.pause()

    override fun start() = mp.start()

    override fun stop() = mp.stop()

    override fun load(uri: Uri) {
        mp.reset()
        mp.setDataSource(context, uri)
        mp.prepare()
    }

    override fun release() {
        surface?.release()
        mp.release()
    }

    override fun setOutput(surfaceTexture: SurfaceTexture) {
        surface?.release()
        surface = Surface(surfaceTexture)
        mp.setSurface(surface)
    }

    override fun setOutput(surface: Surface) {
        this.surface?.release()
        this.surface = surface
        mp.setSurface(surface)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        onCompletion?.invoke()
    }

    override fun onInfo(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {

        when (what) {
            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                onRenderFirstFrame?.invoke()
                return true
            }
        }

        return false
    }
}
