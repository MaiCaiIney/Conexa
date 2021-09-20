package com.conexa.challenge.model

data class CartItem(
    var product: Product,
    var count: Int,
    var total: Float
)