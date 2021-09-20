package com.conexa.challenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conexa.challenge.data.Repository
import com.conexa.challenge.model.Product
import com.conexa.challenge.model.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> = _products

    private val _categories = MutableLiveData<Resource<List<String>>>()
    val categories: LiveData<Resource<List<String>>> = _categories

    private val _filter = MutableLiveData<String?>()
    val filter: LiveData<String?> = _filter

    fun searchProducts(category: String? = null) = viewModelScope.launch(Dispatchers.Main) {
        _products.postValue(Resource.loading())
        _filter.postValue(category)
        val result = withContext(Dispatchers.IO) {
            repository.products(category)
        }
        _products.postValue(result)
    }

    private fun categories() = viewModelScope.launch(Dispatchers.Main) {
        _categories.postValue(Resource.loading())
        val result = withContext(Dispatchers.IO) {
            repository.categories()
        }
        _categories.postValue(result)
    }

    init {
        searchProducts()
        categories()
    }
}