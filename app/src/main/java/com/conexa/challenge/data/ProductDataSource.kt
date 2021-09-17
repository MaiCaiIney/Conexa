package com.conexa.challenge.data

import com.conexa.challenge.api.ProductService
import javax.inject.Inject

class ProductDataSource @Inject constructor(private val service: ProductService) :
    BaseDataSource() {

    suspend fun products() = getResult { service.products() }

}