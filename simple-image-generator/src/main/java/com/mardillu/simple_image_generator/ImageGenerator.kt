package com.mardillu.simple_image_generator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created on 23/06/2024 at 3:58 pm
 * @author mardillu
 */
fun drawTextAsImage(text: String, isDarkTheme: Boolean, width: Int = 100, height: Int = 100,): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    for (x in 0 until width) {
        for (y in 0 until height) {
            paint.color = getGradientColor(text, x, y, width, height, isDarkTheme)
            canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
        }
    }

    return bitmap
}

fun polarToCartesian(radius: Double, angle: Double): Pair<Int, Int> {
    val x = (radius * cos(angle)).toInt()
    val y = (radius * sin(angle)).toInt()
    return Pair(x, y)
}