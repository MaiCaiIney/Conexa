package com.conexa.challenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conexa.challenge.R
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
) : ViewModel() {

    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> = _products

    private val _categories = MutableLiveData<Resource<List<String>>>()
    val categories: LiveData<Resource<List<String>>> = _categories

    private val _filter = MutableLiveData<String?>()
    val filter: LiveData<String?> = _filter

    private var _orderDirection = DIRECTION.DESC
    private val _orderDesc = MutableLiveData(true)
    val orderDesc: LiveData<Boolean> = _orderDesc

    private var _productsSortedByParam: PARAM? = null
    private val _productsSortedBy = MutableLiveData(R.string.products_sorted_by)
    val productsSortedBy: LiveData<Int> = _productsSortedBy

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

    fun orderProductsByName() {
        _productsSortedByParam = PARAM.NAME
        sort()
    }

    fun orderProductsByPrice() {
        _productsSortedByParam = PARAM.PRICE
        sort()
    }

    fun orderProductsByRating() {
        _productsSortedByParam = PARAM.RATING
        sort()
    }

    fun changeOrder() {
        _orderDirection = if (_orderDirection == DIRECTION.ASC) DIRECTION.DESC else DIRECTION.ASC
        _orderDesc.postValue(_orderDirection == DIRECTION.DESC)
        sort()
    }

    private fun sort() {
        val sorted = when (_productsSortedByParam) {
            PARAM.PRICE -> {
                _productsSortedBy.postValue(R.string.products_sorted_by_price)
                when (_orderDirection) {
                    DIRECTION.ASC -> _products.value?.data?.sortedBy { product -> product.price }
                    DIRECTION.DESC -> _products.value?.data?.sortedByDescending { product -> product.price }
                }
            }
            PARAM.NAME -> {
                _productsSortedBy.postValue(R.string.products_sorted_by_name)
                when (_orderDirection) {
                    DIRECTION.ASC -> _products.value?.data?.sortedBy { product -> product.title }
                    DIRECTION.DESC -> _products.value?.data?.sortedByDescending { product -> product.title }
                }
            }
            PARAM.RATING -> {
                _productsSortedBy.postValue(R.string.products_sorted_by_rating)
                when (_orderDirection) {
                    DIRECTION.ASC -> _products.value?.data?.sortedBy { product -> product.rating.rate }
                    DIRECTION.DESC -> _products.value?.data?.sortedByDescending { product -> product.rating.rate }
                }
            }
            null -> _products.value?.data
        }
        _products.value = Resource.success(sorted)
    }

    init {
        searchProducts()
        categories()
    }

    enum class PARAM {
        PRICE, NAME, RATING
    }

    enum class DIRECTION {
        ASC, DESC
    }
}