package com.eje_c.multilink

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.Surface
import com.eje_c.multilink.data.ControlMessage
import com.eje_c.player.Player
import java.io.File

/**
 * 動画再生を行うシーン。
 */
class BasePlayer(private val mediaPlayer: Player) {

    companion object {
        private const val TAG = "PlayerScene"
        private const val SEEK_THRESHOLD = 1000 // コントロールメッセージのpositionプロパティと現在位置がこの値以上離れていたらシークする
    }

    private var currentPath: String? = null

    var onStartPlaying: () -> Unit = {}
    var onStopPlaying: () -> Unit = {}
    var onLoaded: (String) -> Unit = {}

    init {
        // 再生終了したら待機画面
        mediaPlayer.onCompletion = {
            hideScreen()
        }
    }

    /**
     * Set output surface from player.
     */
    fun setSurface(surface: Surface) {
        mediaPlayer.setOutput(surface)
    }

    /**
     * Returns true if video is stereo.
     */
    val isStereo: Boolean
        get() = mediaPlayer.videoWidth != 0
                && mediaPlayer.videoHeight != 0
                && mediaPlayer.videoWidth == mediaPlayer.videoHeight

    /**
     * プレイヤーの状態を更新する。
     */
    fun updateState(newControlMessage: ControlMessage) {
        Log.d(TAG, "updateState $newControlMessage")

        // 読み込んでいるパスと異なるパスを受け取ったら、読み込む
        var isNewVideo = false
        if (currentPath != newControlMessage.path) {
            try {
                load(newControlMessage.path)
                isNewVideo = true
            } catch (e: Exception) {
                Log.e(TAG, "Loading error", e)
                return
            }
        }

        // 現在の再生状態と異なるメッセージを受け取ったら、再生状態を更新する
        if (mediaPlayer.isPlaying != newControlMessage.playing) {
            if (newControlMessage.playing) {
                play(isNewVideo)
            } else {
                pause()
            }
        }

        // 現在位置より一定以上離れた再生位置を受け取ったら、シークする
        if ((Math.abs(mediaPlayer.currentPosition - newControlMessage.position) > SEEK_THRESHOLD)) {
            seekTo(newControlMessage.position)
        }
    }

    /**
     * シークする。
     */
    private fun seekTo(position: Long) {
        Log.d(TAG, "seek to $position ${mediaPlayer.currentPosition}")

        mediaPlayer.currentPosition = position
    }

    /**
     * 再生開始する。
     */
    private fun play(waitForFirstFrame: Boolean) {
        Log.d(TAG, "play $waitForFirstFrame")

        // 再生中でなければ再生開始
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        if (waitForFirstFrame) {

            // 動画の最初のフレームをレンダリングするタイミングで表示を切り替える
            mediaPlayer.onRenderFirstFrame = {
                showScreen()
                mediaPlayer.onRenderFirstFrame = null
            }

        } else {
            // すぐに表示を切り替える
            showScreen()
        }
    }

    /**
     * スクリーンを表示する。
     */
    private fun showScreen() {
        onStartPlaying()
    }

    /**
     * 待機表示にする。
     */
    private fun hideScreen() {
        onStopPlaying()
    }

    /**
     * 一時停止する。
     */
    private fun pause() {
        Log.d(TAG, "pause")

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }

        hideScreen()
    }

    /**
     * 指定した外部ストレージパスからデータを読み込む。
     */
    private fun load(path: String) {
        Log.d(TAG, "load $path")
        this.currentPath = path

        // 読み込む
        mediaPlayer.load(Uri.fromFile(File(Environment.getExternalStorageDirectory(), path)))

        // 最初のフレームをデコードしてすぐ再生できるようにする
        mediaPlayer.currentPosition = 0

        onLoaded(path)
    }
}