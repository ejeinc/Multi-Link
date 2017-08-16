package com.eje_c.multilink.controller

import android.os.Environment
import java.io.File

/**
 * Convert [path] string to [File] which represents a file on external storage.
 */
fun fromExternal(path: String): File = File(Environment.getExternalStorageDirectory(), path)