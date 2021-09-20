package com.conexa.challenge.ui.home.products

import android.view.View
import com.conexa.challenge.R
import com.conexa.challenge.databinding.ItemProductBinding
import com.conexa.challenge.model.Product
import com.conexa.challenge.utils.formatPrice
import com.squareup.picasso.Picasso
import com.xwray.groupie.viewbinding.BindableItem

class ProductItem(val product: Product): BindableItem<ItemProductBinding>() {

    override fun bind(viewBinding: ItemProductBinding, position: Int) {
        viewBinding.tvProductName.text = product.title
        viewBinding.tvProductPrice.text = product.price.formatPrice()
        viewBinding.tvProductRate.text = product.rating.rate.toString()
        Picasso.get().load(product.image).into(viewBinding.ivProductImage)
    }

    override fun getLayout() = R.layout.item_product

    override fun initializeViewBinding(view: View): ItemProductBinding = ItemProductBinding.bind(view)
}