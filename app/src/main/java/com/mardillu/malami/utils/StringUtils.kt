package com.mardillu.malami.utils

/**
 * Created on 17/06/2024 at 7:29 pm
 * @author mardillu
 */
fun String.sanitiseForTts(): String {
    val regex = Regex("""\\.|\\u[0-9a-fA-F]{4}""")
    return this
        .replace("#","")
        .replace("*","")
        .replace("\\u0027","'")
        .replace("\\u0022","\"")
        .replace(regex, "")
        .trim()
}