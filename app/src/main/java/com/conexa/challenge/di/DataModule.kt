package com.conexa.challenge.di

import com.conexa.challenge.api.ProductService
import com.conexa.challenge.data.ProductDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideProductService(retrofit: Retrofit) = retrofit.create(ProductService::class.java)

    @Provides
    fun provideProductDataSource(service: ProductService) = ProductDataSource(service)
}