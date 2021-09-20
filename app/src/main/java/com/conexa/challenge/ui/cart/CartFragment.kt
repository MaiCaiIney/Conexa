package com.conexa.challenge.ui.cart

import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.conexa.challenge.R
import com.conexa.challenge.databinding.FragmentCartBinding
import com.conexa.challenge.databinding.ItemCartBinding
import com.conexa.challenge.model.CartItem
import com.conexa.challenge.model.Product
import com.conexa.challenge.utils.formatPrice
import com.conexa.challenge.view.Modal
import com.conexa.challenge.view.Toast
import com.conexa.challenge.view.Type
import com.conexa.challenge.viewmodel.CartViewModel
import com.squareup.picasso.Picasso

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val viewModel by activityViewModels<CartViewModel>()
    private var enabledMenu = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        initUi()
        subscribeUi()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_cart, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_item_clear) {
            viewModel.clear()
            Toast.show(
                binding.root,
                R.string.cart_empty_toast,
                Type.WARNING
            )
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_clear).isEnabled = enabledMenu
    }

    private fun subscribeUi() {
        viewModel.cart.observe(viewLifecycleOwner, {
            (binding.rvCartItems.adapter as CartAdapter).submitData(it)
        })

        viewModel.total.observe(viewLifecycleOwner, {
            binding.tvCartTotalPrice.text = it.formatPrice()
        })

        viewModel.emptyCart.observe(viewLifecycleOwner, {
            binding.apply {
                rvCartItems.isVisible = !it
                tvCartEmptyTitle.isVisible = it
                tvCartEmptyDescription.isVisible = it
                btCartContinue.isEnabled = !it
                tvCartTotalDescription.isVisible = !it
                tvCartTotalPrice.isVisible = !it
            }
        })

        viewModel.menuEnabled.observe(viewLifecycleOwner, {
            enabledMenu = !it
            activity?.invalidateOptionsMenu()
        })
    }

    private fun initUi() {
        binding.rvCartItems.adapter = CartAdapter(
            onDownButtonClicked = { product -> viewModel.deleteItem(product) },
            onUpButtonClicked = { product -> viewModel.addItem(product) }
        )

        binding.btCartContinue.setOnClickListener {
            Modal(requireContext()).apply {
                setIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_message_type_positive, null))
                setTitle(getString(R.string.cart_continue_modal_title))
                setMessage(getString(R.string.cart_continue_modal_message))
                setButton(getString(R.string.cart_continue_modal_button))
                setOnClickListener { viewModel.clear() }
            }.show()
        }
    }

    private inner class CartAdapter(
        private val onDownButtonClicked: (product: Product) -> Unit,
        private val onUpButtonClicked: (product: Product) -> Unit
    ) :
        RecyclerView.Adapter<CartAdapter.ViewHolder>() {

        private var data: MutableList<CartItem>? = null

        private inner class ViewHolder(private val binding: ItemCartBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: CartItem) {
                val product = item.product
                binding.apply {
                    tvCartItemName.text = product.title
                    tvCartItemPrice.text = product.price.formatPrice()
                    Picasso.get().load(product.image).into(ivCartItemImage)
                    tvCartItemCount.text = item.count.toString()

                    btCartItemDown.setOnClickListener { onDownButtonClicked.invoke(product) }
                    btCartItemUp.setOnClickListener { onUpButtonClicked.invoke(product) }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                ItemCartBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            data?.get(position)?.let { holder.bind(it) }
        }

        override fun getItemCount() = data?.size ?: 0

        fun submitData(list: MutableList<CartItem>) {
            data = list
            notifyDataSetChanged()
        }
    }
}