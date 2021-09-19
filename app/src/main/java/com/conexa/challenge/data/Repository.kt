package com.conexa.challenge.data

import com.conexa.challenge.model.Product
import com.conexa.challenge.model.Resource
import javax.inject.Inject

class Repository @Inject constructor(
    private val productDataSource: ProductDataSource
) {

    suspend fun products(category: String? = null): Resource<List<Product>> {
        return if(category != null) {
            productDataSource.filter(category)
        } else productDataSource.products()
    }

    suspend fun categories() = productDataSource.categories()
}