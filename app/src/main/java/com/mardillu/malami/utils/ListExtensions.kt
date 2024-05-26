package com.mardillu.malami.utils

/**
 * Created on 26/05/2024 at 7:41 pm
 * @author mardillu
 */
fun <E> List<E>.add(item: E): List<E> {
    val mutableList = this.toMutableList()
    mutableList.add(item)

    return mutableList
}