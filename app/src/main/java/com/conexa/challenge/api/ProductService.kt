package com.conexa.challenge.api

import com.conexa.challenge.model.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductService {

    @GET("products")
    suspend fun products(): Response<List<Product>>

    @GET("products/categories")
    suspend fun categories(): Response<List<String>>

    @GET("products/category/{category}")
    suspend fun filter(
        @Path(
            value = "category",
            encoded = true
        ) category: String
    ): Response<List<Product>>
}