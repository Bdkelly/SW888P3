package com.example.sw888p3

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int = 0,
    val name: String,
    val description: String,
    val seller: String,
    val price: Double,
    @DrawableRes val image: Int? = null,
    var isSelected: Boolean = false
) : Parcelable
