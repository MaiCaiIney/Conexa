package com.conexa.challenge.ui.home.products.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.conexa.challenge.databinding.FragmentProductDetailBinding
import com.conexa.challenge.model.Product
import com.conexa.challenge.utils.formatPrice
import com.squareup.picasso.Picasso

class ProductDetailFragment : Fragment() {

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    private lateinit var binding: FragmentProductDetailBinding
    private lateinit var product: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)

        arguments?.getSerializable(EXTRA_PRODUCT)?.let { product = it as Product }

        bind(product)

        return binding.root
    }

    private fun bind(product: Product) {
        binding.apply {
            tvProductDetailName.text = product.title
            tvProductDetailPrice.text = product.price.formatPrice()
            tvProductDetailDescription.text = product.description

            Picasso.get().load(product.image).into(ivProductDetailImage)
        }
    }
}