package com.conexa.challenge.ui.home.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.conexa.challenge.databinding.FragmentProductsBinding
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment() {

    private lateinit var binding: FragmentProductsBinding
    private val viewModel by viewModels<ProductsViewModel>()

    private lateinit var adapter : GroupieAdapter

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
        adapter = GroupieAdapter()
        binding.rvProducts.adapter = adapter
    }
}