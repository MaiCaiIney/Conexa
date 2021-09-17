package com.conexa.challenge.model

import java.io.Serializable

/**
 * Assist class that allows you to encapsulate responses from the repository
 * according to its states (while it is loading, when it was loaded successfully and when an error occurred)
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) : Serializable {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(message: String, data: T? = null): Resource<T> {
            return Resource(
                Status.ERROR,
                data,
                message
            )
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(
                Status.LOADING,
                data,
                null
            )
        }
    }

    fun isSuccessful() = status == Status.SUCCESS

    fun isError() = status == Status.ERROR

    fun isLoading() = status == Status.LOADING
}