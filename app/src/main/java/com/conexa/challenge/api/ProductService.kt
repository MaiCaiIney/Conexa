package com.conexa.challenge.api

import com.conexa.challenge.model.Product
import retrofit2.Response
import retrofit2.http.GET

interface ProductService {

    @GET("products")
    suspend fun products(): Response<List<Product>>

    @GET("products/categories")
    suspend fun categories(): Response<List<String>>
}