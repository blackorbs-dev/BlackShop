package blackorbs.dev.blackshop.ui.product_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import blackorbs.dev.blackshop.R
import blackorbs.dev.blackshop.databinding.ProductListScreenBinding
import blackorbs.dev.blackshop.ui.UiState
import blackorbs.dev.blackshop.utils.load
import blackorbs.dev.blackshop.utils.showFeedback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductListScreen: Fragment() {
    private var binding: ProductListScreenBinding? = null
    private val viewModel: ProductListViewModel by viewModel()
    private val productListAdapter = ProductListAdapter(::handleItemClick)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(binding == null){
            binding = ProductListScreenBinding.inflate(inflater)
            with(activity as AppCompatActivity) {
                setSupportActionBar(binding!!.toolbarContainer.toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.title = getString(R.string.products_listing)
            }
           setupListeners()
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupListeners(){
        with(binding!!) {
            productsList.setHasFixedSize(true)
            productsList.adapter = productListAdapter
            retryBtn.setOnClickListener { viewModel.refresh() }
            goToCart.setOnClickListener {
                load(ProductListScreenDirections.listToCheckout())
            }
        }
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.apply {
            launch {
                viewModel.productList.collectLatest {
                    productListAdapter.submitList(it)
                }
            }
            launch {
                viewModel.refreshState.collectLatest { state ->
                    with(binding!!) {
                        when (state) {
                            is UiState.Error -> {
                                errorBox.visibility = View.VISIBLE
                                loadingIndicator.hide()
                            }
                            UiState.Loading -> {
                                errorBox.visibility = View.GONE
                                loadingIndicator.show()
                            }
                            else -> {
                                loadingIndicator.hide()
                                errorBox.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            launch {
                viewModel.cartErrorState.collectLatest {
                    binding!!.showFeedback(null)
                }
            }
        }
    }

    private fun handleItemClick(id: Int, action: ItemClickAction){
        when(action){
            ItemClickAction.ViewDetails -> {
                binding!!.load(
                    ProductListScreenDirections.listToDetails(id)
                )
            }
            ItemClickAction.AddToCart -> {
                viewModel.updateCart(id, true)
            }
        }
    }
}