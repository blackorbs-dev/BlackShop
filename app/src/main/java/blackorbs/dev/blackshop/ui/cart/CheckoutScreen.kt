package blackorbs.dev.blackshop.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import blackorbs.dev.blackshop.R
import blackorbs.dev.blackshop.databinding.CheckoutScreenBinding
import blackorbs.dev.blackshop.ui.UiState
import blackorbs.dev.blackshop.utils.formatPrice
import blackorbs.dev.blackshop.utils.showFeedback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckoutScreen: Fragment() {
    private var binding: CheckoutScreenBinding? = null
    private val viewModel: CartViewModel by viewModel()
    private val adapter = CartItemsAdapter(::handleItemClick)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(binding == null){
            binding = CheckoutScreenBinding.inflate(inflater)
            with(activity as AppCompatActivity) {
                setSupportActionBar(binding!!.toolbarContainer.toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = getString(R.string.checkout)
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
            cartItemsList.setHasFixedSize(true)
            cartItemsList.adapter = adapter
            actionBtn.setOnClickListener {
                viewModel.checkout()
            }
            toolbarContainer.toolbar.setNavigationOnClickListener {
                root.findNavController().popBackStack()
            }
        }
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.apply {
            launch {
                viewModel.productList.collectLatest { products ->
                    adapter.submitList(products)
                    val noData = products.isEmpty()
                    with(binding!!) {
                        emptyBox.isVisible = noData
                        bottomDivider.isVisible = noData.not()
                        bottomContainer.isVisible = noData.not()
                        totalPrice.text = products.sumOf {
                            it.value.price
                        }.formatPrice()
                    }
                }
            }
            launch {
                viewModel.loadState.collectLatest { state ->
                    with(binding!!) {
                        actionBtn.updateState(state == UiState.Loading)
                        if(state is UiState.Error){
                            showFeedback(null)
                        }
                        else if(state == UiState.Success){
                            SuccessPopup().show(parentFragmentManager, null)
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

    private fun handleItemClick(productId: Int){
        viewModel.updateCart(productId, false)
    }
}