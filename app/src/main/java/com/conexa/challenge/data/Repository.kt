package com.conexa.challenge.data

import javax.inject.Inject

class Repository @Inject constructor(
    private val productDataSource: ProductDataSource
) {

    suspend fun products() = productDataSource.products()

    suspend fun categories() = productDataSource.categories()
}