package com.eje_c.player

import android.graphics.SurfaceTexture
import android.net.Uri
import android.view.Surface

/**
 * Interface for media player.
 */
interface Player {

    /**
     * Gets the duration of the content.
     */
    val duration: Long

    /**
     * Gets the current playback position.
     */
    var currentPosition: Long

    /**
     * Checks whether the content is playing.
     */
    val isPlaying: Boolean

    /**
     * Audio output volume.
     */
    var volume: Float

    /**
     * Called when a frame is rendered for the first time.
     */
    var onRenderFirstFrame: (() -> Unit)?

    /**
     * Callback to be invoked when the end of a media source has been reached during playback.
     */
    var onCompletion: (() -> Unit)?

    val videoWidth: Int
    val videoHeight: Int

    /**
     * Pauses playback.
     */
    fun pause()

    /**
     * Starts or resumes playback.
     */
    fun start()

    /**
     * Stops playback after playback has been started or paused.
     */
    fun stop()

    /**
     * Load data from uri.
     */
    fun load(uri: Uri)

    /**
     * Releases resources associated with this object.
     */
    fun release()

    /**
     * Sets the output to be used as the sink for the video portion of the media.
     */
    fun setOutput(surfaceTexture: SurfaceTexture)

    /**
     * Sets the output to be used as the sink for the video portion of the media.
     */
    fun setOutput(surface: Surface)

}