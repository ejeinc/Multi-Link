package com.eje_c.player

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.view.Surface
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener

class ExoPlayerImpl(
        private val context: Context,
        private val exoPlayer: SimpleExoPlayer) : Player, com.google.android.exoplayer2.Player.EventListener, VideoListener {

    private val userAgent = Util.getUserAgent(context, context.applicationInfo.name)
    private val dataSourceFactory = DefaultDataSourceFactory(context, userAgent)
    private val mediaSourceFactory = ExtractorMediaSource.Factory(dataSourceFactory)
    private var surface: Surface? = null
    private var _videoWidth: Int = 0
    private var _videoHeight: Int = 0

    init {
        // Register event listeners
        exoPlayer.addVideoListener(this)
        exoPlayer.addListener(this)
    }

    override val duration: Long
        get() = exoPlayer.duration

    override var currentPosition: Long
        set(value) = exoPlayer.seekTo(value)
        get() = exoPlayer.currentPosition

    override val isPlaying: Boolean
        get() = exoPlayer.playWhenReady && exoPlayer.playbackState != com.google.android.exoplayer2.Player.STATE_ENDED

    override var volume: Float
        get() = exoPlayer.volume
        set(value) {
            exoPlayer.volume = value
        }

    override var onRenderFirstFrame: (() -> Unit)? = null

    override var onCompletion: (() -> Unit)? = null

    override val videoWidth: Int
        get() = _videoWidth

    override val videoHeight: Int
        get() = _videoHeight

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun start() {
        exoPlayer.playWhenReady = true
    }

    override fun stop() = exoPlayer.stop()

    override fun load(uri: Uri) {
        val mediaSource = mediaSourceFactory.createMediaSource(uri)
        exoPlayer.prepare(mediaSource)
    }

    override fun release() {
        surface?.release()
        exoPlayer.release()
    }

    override fun setOutput(surfaceTexture: SurfaceTexture) {
        surface?.release()
        surface = Surface(surfaceTexture)
        exoPlayer.setVideoSurface(surface)
    }

    override fun setOutput(surface: Surface) {
        this.surface?.release()
        this.surface = surface
        exoPlayer.setVideoSurface(surface)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            com.google.android.exoplayer2.Player.STATE_ENDED -> onCompletion?.invoke()
        }
    }

    override fun onRenderedFirstFrame() {
        onRenderFirstFrame?.invoke()
    }

    override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        _videoWidth = width
        _videoHeight = height
    }

    /*
     * No-op events.
     */

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}
    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}
    override fun onPlayerError(error: ExoPlaybackException?) {}
    override fun onLoadingChanged(isLoading: Boolean) {}
    override fun onPositionDiscontinuity(reason: Int) {}
    override fun onRepeatModeChanged(repeatMode: Int) {}
    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}
    override fun onSeekProcessed() {}
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
}