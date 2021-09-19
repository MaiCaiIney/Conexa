package com.conexa.challenge.utils

import java.util.*

fun Float.formatPrice(): String {
    val symbol = Currency.getInstance(Locale.getDefault()).symbol
    return symbol.plus("%.2f".format(this))
}