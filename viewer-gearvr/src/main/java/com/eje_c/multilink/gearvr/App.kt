package com.eje_c.multilink.gearvr

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.Surface
import com.eje_c.multilink.MultiLinkApp
import org.gearvrf.*
import org.gearvrf.scene_objects.GVRSphereSceneObject
import org.gearvrf.scene_objects.GVRViewSceneObject

/**
 * アプリケーションのメインクラス。
 */
class App(context: Context) : GVRMain() {

    private val app = MultiLinkApp(context)
    private lateinit var surfaceTexture: SurfaceTexture

    override fun onInit(gvrContext: GVRContext) {

        // 待機中の表示
        val text = GVRViewSceneObject(gvrContext, R.layout.waiting).apply {
            transform.positionZ = -1.5f
        }

        // 待機中の背景
        val background = GVRSphereSceneObject(gvrContext, false, 100f).apply {
            renderData.material = GVRMaterial(gvrContext).apply {
                mainTexture = gvrContext.assetLoader.loadTexture(GVRAndroidResource(gvrContext, R.drawable.background))
            }
        }

        // 動画テクスチャー
        val videoTexture = GVRExternalTexture(gvrContext)
        surfaceTexture = SurfaceTexture(videoTexture.id)
        app.player.setSurface(Surface(surfaceTexture))

        // 毎フレーム更新する
        gvrContext.registerDrawFrameListener {
            surfaceTexture.updateTexImage()
        }

        // 動画スクリーン
        val screen = GVRSphereSceneObject(gvrContext, false, 100f).apply {
            renderData.material = GVRMaterial(gvrContext, GVRMaterial.GVRShaderType.OES.ID).apply {
                mainTexture = videoTexture
            }
            setEnable(false)
        }

        // オブジェクトをシーンに追加
        gvrContext.mainScene.addSceneObject(text)
        gvrContext.mainScene.addSceneObject(background)
        gvrContext.mainScene.addSceneObject(screen)

        // 再生開始時
        app.player.onStartPlaying = {
            screen.setEnable(true)
            text.setEnable(false)
            background.setEnable(false)
        }

        // 再生停止時
        app.player.onStopPlaying = {
            screen.setEnable(false)
            text.setEnable(true)
            background.setEnable(true)
        }

        // 読み込み完了時
        app.player.onLoaded = {

            if (app.player.isStereo) {
                screen.renderData.material = GVRMaterial(gvrContext, GVRMaterial.GVRShaderType.OESHorizontalStereo.ID).apply {
                    mainTexture = videoTexture
                }
            } else {
                screen.renderData.material = GVRMaterial(gvrContext, GVRMaterial.GVRShaderType.OES.ID).apply {
                    mainTexture = videoTexture
                }
            }
        }

        /**
         * 毎フレーム更新時に呼ばれる。
         */
        gvrContext.registerDrawFrameListener {
            // ヘッドトラッキング情報を更新
            val transform = gvrContext.mainScene.mainCameraRig.transform
            app.updateHeadOrientation(transform.rotationX, transform.rotationY, transform.rotationZ, transform.rotationW)
        }
    }
}
