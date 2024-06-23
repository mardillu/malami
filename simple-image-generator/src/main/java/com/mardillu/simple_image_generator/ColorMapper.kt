package com.mardillu.simple_image_generator

/**
 * Created on 23/06/2024 at 3:58 pm
 * @author mardillu
 */
import android.graphics.Color
import kotlin.math.abs

enum class ColorMode {
    BLUE, RED, GREEN, WHITE, BLACK, GRAY, LIGHT_GRAY, DARK_GRAY, CYAN, MAGENTA, YELLOW, ORANGE, PINK, PURPLE, TEAL, BROWN, INDIGO, VIOLET, LIME, AMBER,
}

fun getRandomColor(s: String): ColorMode {
    val colors = ColorMode.entries.toTypedArray()
    return colors[abs(s.hashCode()) % colors.size]
}

fun mapColorModeToColor(colorMode: ColorMode): Int {
    return when (colorMode) {
        ColorMode.BLUE -> Color.BLUE
        ColorMode.RED -> Color.RED
        ColorMode.GREEN -> Color.GREEN
        ColorMode.WHITE -> Color.WHITE
        ColorMode.BLACK -> Color.BLACK
        ColorMode.GRAY -> Color.GRAY
        ColorMode.LIGHT_GRAY -> Color.LTGRAY
        ColorMode.DARK_GRAY -> Color.DKGRAY
        ColorMode.CYAN -> Color.CYAN
        ColorMode.MAGENTA -> Color.MAGENTA
        ColorMode.YELLOW -> Color.YELLOW
        ColorMode.ORANGE -> Color.parseColor("#FFA500")
        ColorMode.PINK -> Color.parseColor("#FFC0CB")
        ColorMode.PURPLE -> Color.parseColor("#800080")
        ColorMode.TEAL -> Color.parseColor("#008080")
        ColorMode.BROWN -> Color.parseColor("#A52A2A")
        ColorMode.INDIGO -> Color.parseColor("#4B0082")
        ColorMode.VIOLET -> Color.parseColor("#8F00FF")
        ColorMode.LIME -> Color.parseColor("#00FF00")
        ColorMode.AMBER -> Color.parseColor("#FFBF00")
    }
}

fun charToColor(c: Char, isDarkTheme: Boolean): Int {
    val baseColor = c.code * 0xFFFFFF / 128
    return if (isDarkTheme) {
        Color.rgb(
            ((baseColor shr 16) and 0xFF) / 2,
            ((baseColor shr 8) and 0xFF) / 2,
            (baseColor and 0xFF) / 2
        )
    } else {
        Color.rgb(
            ((baseColor shr 16) and 0xFF) + ((0xFF - ((baseColor shr 16) and 0xFF)) / 2),
            ((baseColor shr 8) and 0xFF) + ((0xFF - ((baseColor shr 8) and 0xFF)) / 2),
            (baseColor and 0xFF) + ((0xFF - (baseColor and 0xFF)) / 2)
        )
    }
}

fun getGradientColor(s: String, x: Int, y: Int, width: Int, height: Int, isDarkTheme: Boolean): Int {
    val ratioX = x.toFloat() / width
    val ratioY = y.toFloat() / height
    val baseColor1 = if (isDarkTheme) Color.BLACK else Color.WHITE
    val baseColor2 = mapColorModeToColor(getRandomColor(s))
    val baseColor3 = if (isDarkTheme) Color.DKGRAY else Color.LTGRAY

    val red = ((1 - ratioX) * Color.red(baseColor1) + ratioX * Color.red(baseColor2)).toInt()
    val green = ((1 - ratioY) * Color.green(baseColor1) + ratioY * Color.green(baseColor3)).toInt()
    val blue = ((1 - ratioX) * Color.blue(baseColor1) + ratioX * Color.blue(baseColor2)).toInt()

    return Color.rgb(red, green, blue)
}