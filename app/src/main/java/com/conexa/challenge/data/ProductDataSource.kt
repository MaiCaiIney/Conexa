package com.conexa.challenge.data

import com.conexa.challenge.api.ProductService
import javax.inject.Inject

class ProductDataSource @Inject constructor(private val service: ProductService) :
    BaseDataSource() {

    suspend fun products() = getResult { service.products() }

    suspend fun categories() = getResult { service.categories() }

    suspend fun filter(category: String) = getResult { service.filter(category) }
}