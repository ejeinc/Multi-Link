package com.eje_c.multilink.gearvr

import android.content.Context
import com.eje_c.multilink.MultiLinkApp
import org.meganekkovr.FrameInput
import org.meganekkovr.HeadTransform
import org.meganekkovr.MeganekkoApp

/**
 * アプリケーションのメインクラス。
 */
class App(context: Context) : MeganekkoApp() {

    private val app = MultiLinkApp(context)
    private lateinit var playerScene: PlayerScene

    /**
     * アプリケーション開始時に呼ばれる。
     */
    override fun init() {
        super.init()

        // シーンを読み込む
        playerScene = setSceneFromXml(R.xml.scene) as PlayerScene
        playerScene.player = app.player
    }

    /**
     * 毎フレーム更新時に呼ばれる。
     */
    override fun update(frame: FrameInput) {

        // ヘッドトラッキング情報の送信
        val quaternion = HeadTransform.getInstance().quaternion
        app.updateHeadOrientation(quaternion.x, quaternion.y, quaternion.z, quaternion.w)

        super.update(frame)
    }
}
