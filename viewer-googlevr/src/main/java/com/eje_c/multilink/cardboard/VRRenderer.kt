/**
 * Copyright 2015 Dennis Ippel
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.eje_c.multilink.cardboard

import android.content.Context
import android.view.MotionEvent
import com.google.vr.sdk.base.Eye
import com.google.vr.sdk.base.GvrView
import com.google.vr.sdk.base.HeadTransform
import com.google.vr.sdk.base.Viewport
import org.rajawali3d.math.Matrix4
import org.rajawali3d.renderer.Renderer
import javax.microedition.khronos.egl.EGLConfig

/**
 * VRアプリケーションのベースクラス。
 * @author dennis.ippel
 * @author eje
 */
abstract class VRRenderer(context: Context) : Renderer(context), GvrView.StereoRenderer {

    private val mCurrentEyeMatrix = Matrix4()
    private val projectionMatrix = Matrix4()
    private var ellapsedRealtime: Long = 0
    private var deltaTime: Double = 0.toDouble()

    override fun initScene() {
        init()
    }

    abstract fun init()

    override fun onNewFrame(headTransform: HeadTransform) {
        super.onRenderFrame(null)
    }

    override fun onRender(ellapsedRealtime: Long, deltaTime: Double) {
        this.ellapsedRealtime = ellapsedRealtime
        this.deltaTime = deltaTime
    }

    override fun onDrawEye(eye: Eye) {

        val camera = currentCamera

        // Update projection matrix
        projectionMatrix.setAll(eye.getPerspective(camera.nearPlane.toFloat(), camera.farPlane.toFloat()))
        camera.projectionMatrix = projectionMatrix

        // Update camera position and rotation
        mCurrentEyeMatrix.setAll(eye.eyeView).inverse()
        camera.setRotation(mCurrentEyeMatrix)
        camera.position = mCurrentEyeMatrix.translation

        render(ellapsedRealtime, deltaTime)
    }


    override fun onSurfaceChanged(width: Int, height: Int) = super.onRenderSurfaceSizeChanged(null, width, height)

    override fun onSurfaceCreated(eglConfig: EGLConfig) = super.onRenderSurfaceCreated(eglConfig, null, -1, -1)

    override fun onRendererShutdown() = super.onRenderSurfaceDestroyed(null)

    override fun onFinishFrame(viewport: Viewport) {}
    override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float, yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {}
    override fun onTouchEvent(event: MotionEvent?) {}

    open fun onDestroy() {}
}
