package com.conexa.challenge.ui.home.filter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.conexa.challenge.R
import com.conexa.challenge.databinding.FragmentCategoriesListBinding
import com.conexa.challenge.databinding.ItemCategoryBinding
import com.conexa.challenge.viewmodel.ProductsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class CategoriesFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCategoriesListBinding
    private val viewModel by activityViewModels<ProductsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesListBinding.inflate(inflater, container, false)

        val adapter = CategoryAdapter()
        binding.list.adapter = adapter
        subscribeUi(adapter)

        return binding.root
    }

    private fun subscribeUi(adapter: CategoryAdapter) {
        viewModel.categories.observe(viewLifecycleOwner, {
            adapter.submitList(it.data)
        })
    }

    private inner class CategoryAdapter :
        ListAdapter<String, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

        private val rainbow: IntArray by lazy { resources.getIntArray(R.array.rainbow) }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val color = Random().nextInt(rainbow.size)

            return ViewHolder(
                ItemCategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                rainbow[color]
            )
        }

        private inner class ViewHolder(
            private val binding: ItemCategoryBinding,
            private val color: Int
        ) : RecyclerView.ViewHolder(binding.root) {

            init {
                val unwrappedDrawable: Drawable = binding.text.background
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    color
                )
            }

            fun bind(category: String) {
                binding.text.text = category
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }
}

private class CategoryDiffCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(
        oldItem: String,
        newItem: String
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: String,
        newItem: String
    ): Boolean {
        return oldItem == newItem
    }
}