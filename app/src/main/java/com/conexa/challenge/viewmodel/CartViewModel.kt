package com.conexa.challenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.conexa.challenge.model.CartItem
import com.conexa.challenge.model.Product
import com.conexa.challenge.utils.notifyObserver

class CartViewModel : ViewModel() {

    private val _cart = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cart: LiveData<MutableList<CartItem>> = _cart

    private val _total = MutableLiveData(0.0)
    val total: LiveData<Double> = _total

    private val _emptyCart = MutableLiveData(true)
    val emptyCart: LiveData<Boolean> = _emptyCart

    fun addItem(product: Product) {
        var item = _cart.value?.find { cartItem -> cartItem.product.id == product.id }

        if (item != null) {
            item.count = item.count.plus(1)
            item.total = item.product.price * item.count
        } else {
            item = CartItem(product, 1, product.price)
            _cart.value?.add(item)
        }
        _cart.notifyObserver()

        calculateTotal()
        emptyCart()
    }

    fun deleteItem(product: Product) {
        val item = _cart.value?.find { cartItem -> cartItem.product.id == product.id }

        if (item != null) {
            item.count = item.count.minus(1)
            item.total = item.product.price * item.count

            if (item.count == 0) _cart.value?.remove(item)
        }
        _cart.notifyObserver()

        calculateTotal()
        emptyCart()
    }

    fun clear() {
        _cart.value?.clear()
        _cart.notifyObserver()

        calculateTotal()
        emptyCart()
    }

    private fun calculateTotal() {
        val total = _cart.value?.sumOf { cartItem -> cartItem.total.toDouble() } ?: 0.0
        _total.postValue(total)
    }

    private fun emptyCart() {
        _emptyCart.postValue(_cart.value?.isEmpty() ?: true)
    }
}