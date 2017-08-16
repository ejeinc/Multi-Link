package com.eje_c.multilink.data

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.telephony.TelephonyManager
import java.io.File

/**
 * VR device information.
 */
class DeviceInfo(val imei: String, val name: String, val videos: List<VideoInfo>) {

    /**
     * Video metadata in VR device.
     */
    class VideoInfo(val name: String, val path: String, val length: Long)

    companion object {

        @SuppressLint("HardwareIds")
        fun get(context: Context): DeviceInfo {

            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val imei = telephonyManager.deviceId
            val videoList = getVRVideos(context)

            return DeviceInfo(imei, "${Build.BRAND}: ${Build.MODEL}", videoList)
        }

        /**
         * 端末内にあるVR動画を返す。VR動画であるかどうかは動画のアスペクト比が2:1または1:1であるかどうかによって判断する。
         */
        private fun getVRVideos(context: Context): List<VideoInfo> {

            // 端末内の動画を取得
            val cursorOrNull: Cursor? = MediaStore.Video.query(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf(
                    MediaStore.Video.VideoColumns.TITLE,    // 0
                    MediaStore.Video.VideoColumns.DATA,     // 1
                    MediaStore.Video.VideoColumns.DURATION, // 2
                    MediaStore.Video.VideoColumns.WIDTH,    // 3
                    MediaStore.Video.VideoColumns.HEIGHT    // 4
            ))

            cursorOrNull?.use { cursor ->

                return cursor.map {

                    val width = getInt(3)
                    val height = getInt(4)

                    // 2:1 または 1:1 の動画のみフィルタリング
                    if (width == height * 2 || width == height) {

                        val name = getString(0)
                        val path = File(getString(1)).relativeTo(Environment.getExternalStorageDirectory()).path
                        val length = getLong(2)

                        VideoInfo(name, path, length)

                    } else {
                        null
                    }

                }
            }

            return emptyList()
        }

        /**
         * Create List from Cursor. [callback] is called per row.
         */
        fun <T> Cursor.map(callback: Cursor.() -> T?): List<T> {

            val result = mutableListOf<T>()

            while (moveToNext()) {
                result += callback() ?: continue
            }

            return result
        }
    }
}
