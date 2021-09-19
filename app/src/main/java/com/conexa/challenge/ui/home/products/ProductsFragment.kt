package com.conexa.challenge.ui.home.products

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.conexa.challenge.R
import com.conexa.challenge.databinding.FragmentProductsBinding
import com.conexa.challenge.ui.home.products.detail.ProductDetailFragment
import com.conexa.challenge.viewmodel.ProductsViewModel
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private val viewModel by activityViewModels<ProductsViewModel>()

    private lateinit var adapter: GroupieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)

        initLayout()
        setObservers()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_products, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_filter -> {
                navigateToCategories()
                true
            }
            R.id.menu_item_cart ->
                // TODO navegar al carrito
                true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setObservers() {
        viewModel.products.observe(viewLifecycleOwner, {
            it?.data?.map { product -> ProductItem(product) }?.let { list ->
                adapter.add(Section().apply {
                    val updatingGroup = Section()
                    val updatableItems = ArrayList<ProductItem>(list)
                    updatingGroup.update(updatableItems)
                    add(updatingGroup)
                })
            }
        })
    }

    private fun initLayout() {
        adapter = GroupieAdapter().apply {
            setOnItemClickListener(navigateToProductDetail)
        }
        binding.rvProducts.adapter = adapter
    }

    private fun navigateToCategories() {
        findNavController().navigate(R.id.action_fragment_products_to_fragment_filter_products)
    }

    private val navigateToProductDetail = OnItemClickListener { item, _ ->
        val bundle = Bundle().apply {
            putSerializable(ProductDetailFragment.EXTRA_PRODUCT, (item as ProductItem).product)
        }
        findNavController().navigate(R.id.action_fragment_products_to_fragment_product_detail, bundle)
    }
}