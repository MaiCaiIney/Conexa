package com.conexa.challenge.utils

import androidx.lifecycle.MutableLiveData

/*
 * Internamente, LiveData realiza un seguimiento de cada cambio como un número de versión (un contador simple almacenado como un int).
 * Llamar a setValue() incrementa esta versión y actualiza a los observadores con los nuevos datos.
 * El efecto secundario es que si la estructura de datos subyacente de LiveData ha cambiado
 * (como agregar un elemento a una colección), no ocurrirá nada para comunicar esto a los observadores.
 * Por lo tanto, tenemos que llamar a setValue() después de agregar un elemento a una colección.
 */
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}