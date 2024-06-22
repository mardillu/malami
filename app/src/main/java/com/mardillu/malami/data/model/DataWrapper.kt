package com.mardillu.malami.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created on 17/06/2024 at 1:01 pm
 * @author mardillu
 */
data class DataWrapper<out T : Any>(
    @SerializedName("data")
    val `data`: T?
)
