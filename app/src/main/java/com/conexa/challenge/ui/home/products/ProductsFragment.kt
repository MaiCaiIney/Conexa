package com.conexa.challenge.ui.home.products

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.conexa.challenge.R
import com.conexa.challenge.databinding.FragmentProductsBinding
import com.conexa.challenge.model.Resource
import com.conexa.challenge.ui.home.products.detail.ProductDetailFragment
import com.conexa.challenge.viewmodel.CartViewModel
import com.conexa.challenge.viewmodel.ProductsViewModel
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private val viewModel by activityViewModels<ProductsViewModel>()
    private val cartViewModel by activityViewModels<CartViewModel>()

    private var enabledMenu = false
    private var emptyCart = true

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

        initUi()
        subscribeUi()

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
            R.id.menu_item_cart -> {
                navigateToCart()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_filter).isEnabled = enabledMenu
        val icon = if (emptyCart) R.drawable.ic_cesta else R.drawable.ic_cesta_no_vacia
        menu.findItem(R.id.menu_item_cart).setIcon(icon)
    }

    private fun subscribeUi() {
        viewModel.products.observe(viewLifecycleOwner, {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    val isEmpty = it.data?.isEmpty() ?: false
                    binding.apply {
                        rvProducts.isVisible = !isEmpty
                        tvProductsMessageTitle.isVisible = isEmpty
                        tvProductsMessageDescription.isVisible = isEmpty
                    }

                    if (isEmpty) {
                        binding.apply {
                            tvProductsMessageTitle.text = getString(R.string.products_empty_title)
                            tvProductsMessageDescription.text =
                                getString(R.string.products_empty_description)
                        }
                    } else {
                        it?.data?.map { product -> ProductItem(product) }?.let { list ->
                            adapter.replaceAll(list)
                        }
                    }
                }
                Resource.Status.ERROR -> {
                    binding.apply {
                        tvProductsMessageTitle.isVisible = true
                        tvProductsMessageDescription.isVisible = true
                        tvProductsMessageTitle.text = getString(R.string.products_error_title)
                        tvProductsMessageDescription.text =
                            getString(R.string.products_error_description)
                    }
                }
                Resource.Status.LOADING -> {
                }
            }
            binding.pbProducts.isVisible = it.isLoading()
        })

        viewModel.filter.observe(viewLifecycleOwner, {
            binding.tvProductsFilter.apply {
                isVisible = it != null
                text = it
            }
        })

        viewModel.categories.observe(viewLifecycleOwner, {
            enabledMenu = it.isSuccessful() && it.data?.isEmpty() == false
            activity?.invalidateOptionsMenu()
        })

        cartViewModel.emptyCart.observe(viewLifecycleOwner, {
            emptyCart = it
            activity?.invalidateOptionsMenu()
        })
    }

    private fun initUi() {
        adapter = GroupieAdapter().apply {
            setOnItemClickListener(navigateToProductDetail)
            add(Section().apply {
                val updatingGroup = Section()
                add(updatingGroup)
            })
        }
        binding.rvProducts.adapter = adapter

        binding.tvProductsFilter.setOnClickListener { viewModel.searchProducts() }
    }

    private fun navigateToCategories() {
        findNavController().navigate(R.id.action_fragment_products_to_fragment_filter_products)
    }

    private fun navigateToCart() {
        findNavController().navigate(R.id.action_fragment_products_to_fragment_cart)
    }

    private val navigateToProductDetail = OnItemClickListener { item, _ ->
        val bundle = Bundle().apply {
            putSerializable(ProductDetailFragment.EXTRA_PRODUCT, (item as ProductItem).product)
        }
        findNavController().navigate(
            R.id.action_fragment_products_to_fragment_product_detail,
            bundle
        )
    }
}