package com.eje_c.multilink.cardboard

import com.eje_c.multilink.BasePlayer
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.scene.Scene

/**
 * 動画再生を行うシーン。
 */
class PlayerScene(renderer: Renderer, player: BasePlayer) : Scene(renderer) {

    /**
     * シーンの初期化を行う。
     */
    init {
        val waiting = ViewUtil.toObject3D(mRenderer.context, R.layout.waiting, "waiting")
        waiting.position = Vector3(0.0, 0.0, -10.0)
        addChild(waiting)

        val screen = VRSphere()
        screen.isVisible = false
        addChild(screen)

        screen.setSurfaceListener({ surface -> player.setSurface(surface) })

        player.onStartPlaying = {
            screen.isVisible = true
            waiting.isVisible = false
        }

        player.onStopPlaying = {
            screen.isVisible = false
            waiting.isVisible = true
        }
    }
}
