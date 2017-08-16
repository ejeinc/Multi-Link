package com.eje_c.multilink.cardboard

import android.content.Context
import com.eje_c.multilink.MultiLinkApp
import com.google.vr.sdk.base.HeadTransform

class App(context: Context) : VRRenderer(context) {

    private val app = MultiLinkApp(context)
    private lateinit var playerScene: PlayerScene
    private val quaternion = FloatArray(4)

    /**
     * アプリケーション開始時に呼ばれる。
     */
    override fun init() {

        // シーンを作成
        playerScene = PlayerScene(this, app.player)
        addAndSwitchScene(playerScene)
    }

    /**
     * 毎フレーム更新時に呼ばれる。
     */
    override fun onNewFrame(headTransform: HeadTransform) {

        // ヘッドトラッキング情報の送信
        headTransform.getQuaternion(quaternion, 0)
        app.updateHeadOrientation(quaternion[0], quaternion[1], quaternion[2], quaternion[3])

        super.onNewFrame(headTransform)
    }
}
