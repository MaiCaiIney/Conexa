package com.conexa.challenge.ui.home.products

import android.os.Bundle
import android.view.*
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
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

    private var enabledMenuFilter = false
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
        menu.findItem(R.id.menu_item_filter).isEnabled = enabledMenuFilter
        val icon = if (emptyCart) R.drawable.ic_cesta else R.drawable.ic_cesta_no_vacia
        menu.findItem(R.id.menu_item_cart).setIcon(icon)
    }

    private fun subscribeUi() {
        viewModel.products.observe(viewLifecycleOwner, {
            val isEmpty = it.data?.isEmpty() ?: true

            binding.apply {
                rvProducts.isVisible = it.isSuccessful() && !isEmpty

                tvProductsMessageTitle.isVisible = !it.isLoading() && (it.isError() || isEmpty)
                tvProductsMessageDescription.isVisible = !it.isLoading() && (it.isError() || isEmpty)

                tvProductsSortedBy.isEnabled = !isEmpty
                btProductsSortedByDirection.isEnabled = !isEmpty

                pbProducts.isVisible = it.isLoading()
            }

            when (it.status) {
                Resource.Status.SUCCESS -> {
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
                        tvProductsMessageTitle.text = getString(R.string.products_error_title)
                        tvProductsMessageDescription.text =
                            getString(R.string.products_error_description)
                    }
                }
                Resource.Status.LOADING -> {
                }
            }
        })

        viewModel.filter.observe(viewLifecycleOwner, {
            binding.tvProductsFilter.apply {
                isVisible = it != null
                text = it
            }
        })

        viewModel.categories.observe(viewLifecycleOwner, {
            enabledMenuFilter = it.isSuccessful() && it.data?.isEmpty() == false
            activity?.invalidateOptionsMenu()
        })

        cartViewModel.emptyCart.observe(viewLifecycleOwner, {
            emptyCart = it
            activity?.invalidateOptionsMenu()
        })

        viewModel.orderDesc.observe(viewLifecycleOwner, {
            val id = if (it) R.drawable.ic_down else R.drawable.ic_up
            ViewCompat.setBackground(
                binding.btProductsSortedByDirection,
                ResourcesCompat.getDrawable(resources, id, null)
            )
        })

        viewModel.productsSortedBy.observe(viewLifecycleOwner, {
            binding.tvProductsSortedBy.text = getString(it)
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

        binding.tvProductsSortedBy.setOnClickListener { view ->
            showMenuOrder(
                view,
                R.menu.menu_products_order
            )
        }

        binding.btProductsSortedByDirection.setOnClickListener {
            viewModel.changeOrder()
        }
    }

    private fun showMenuOrder(view: View, @MenuRes menuRes: Int) {
        val wrapper = ContextThemeWrapper(context, R.style.Theme_Conexa_Popup)
        PopupMenu(wrapper, view).apply {
            menuInflater.inflate(menuRes, this.menu)
            setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.menu_item_order_by_price -> {
                        viewModel.orderProductsByPrice()
                        true
                    }
                    R.id.menu_item_order_by_rating -> {
                        viewModel.orderProductsByRating()
                        true
                    }
                    else -> {
                        viewModel.orderProductsByName()
                        true
                    }
                }
            }
        }.show()
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