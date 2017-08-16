package com.eje_c.multilink.gearvr

import com.eje_c.multilink.BasePlayer
import org.meganekkovr.Entity
import org.meganekkovr.Scene
import org.meganekkovr.SurfaceRendererComponent

/**
 * 動画再生を行うシーン。
 */
class PlayerScene : Scene() {

    private lateinit var waiting: Entity
    private lateinit var screen: Entity

    var player: BasePlayer? = null
        set(player) {
            field = player

            if (player != null) {

                player.onStartPlaying = {
                    screen.isVisible = true
                    waiting.isVisible = false
                }

                player.onStopPlaying = {
                    screen.isVisible = false
                    waiting.isVisible = true
                }

                player.onLoaded = {

                    if (player.isStereo) {
                        screen.getComponent(SurfaceRendererComponent::class.java).stereoMode = SurfaceRendererComponent.StereoMode.TOP_BOTTOM
                    } else {
                        screen.getComponent(SurfaceRendererComponent::class.java).stereoMode = SurfaceRendererComponent.StereoMode.NORMAL
                    }
                }

                // シーン中の球にMediaPlayerの映像を送るようにする
                val surfaceRenderer = SurfaceRendererComponent()
                surfaceRenderer.setContinuousUpdate(true)
                screen.add(surfaceRenderer)
                player.setSurface(surfaceRenderer.surface)
            }
        }

    /**
     * シーンの初期化を行う。
     */
    override fun init() {
        super.init()

        // Get Entity
        waiting = findById(R.id.waiting)!!
        screen = findById(R.id.screen)!!
    }
}
