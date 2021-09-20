package com.conexa.challenge.model

import java.io.Serializable

data class Product(
    val id: Int,
    val title: String,
    val price: Float,
    val category: String,
    val description: String,
    val image: String,
    val rating: Rating
) : Serializable

data class Rating(
    val rate: Float,
    val count: Int
) : Serializable