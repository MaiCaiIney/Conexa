package com.conexa.challenge.data

import com.conexa.challenge.model.Resource
import retrofit2.Response

abstract class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            if(response.isSuccessful) {
                val body = response.body()
                if(body != null) return Resource.success(body)
            }
            return Resource.error("${response.code()}: ${response.message()}")
        } catch (e: Exception) {
            return Resource.error(e.message ?: "Generic error")
        }
    }
}