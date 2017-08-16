package com.eje_c.multilink.cardboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import org.rajawali3d.Object3D
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.textures.Texture
import org.rajawali3d.primitives.Plane

object ViewUtil {

    fun toObject3D(context: Context, @LayoutRes layoutRes: Int, textureName: String): Object3D {
        return toObject3D(View.inflate(context, layoutRes, null), textureName)
    }

    fun toObject3D(view: View, textureName: String): Object3D {

        // Render view to bitmap
        val bitmap = toBitmap(view)

        // Create Material
        val material = Material()
        material.color = 0
        material.addTexture(Texture(textureName, bitmap))

        // Create object
        val plane = Plane(bitmap.width * 0.01f, bitmap.height * 0.01f, 1, 1)
        plane.material = material

        return plane
    }

    /**
     * Render layout to bitmap.
     */
    fun toBitmap(context: Context, @LayoutRes layoutRes: Int): Bitmap {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutRes, null)
        return toBitmap(view)
    }

    /**
     * Render View to bitmap.
     */
    fun toBitmap(view: View): Bitmap {

        // Measure
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        // Create bitmap and render to it
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap
    }
}
