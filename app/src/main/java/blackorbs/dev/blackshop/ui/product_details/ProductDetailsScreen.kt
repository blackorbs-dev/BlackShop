package blackorbs.dev.blackshop.ui.product_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import blackorbs.dev.blackshop.R
import blackorbs.dev.blackshop.databinding.ProductDetailsScreenBinding
import blackorbs.dev.blackshop.ui.UiState
import blackorbs.dev.blackshop.utils.formatPrice
import blackorbs.dev.blackshop.utils.load
import blackorbs.dev.blackshop.utils.loadWithPlaceholder
import blackorbs.dev.blackshop.utils.showFeedback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailsScreen: Fragment() {
    private var binding: ProductDetailsScreenBinding? = null
    private val viewModel: ProductDetailsViewModel by viewModel()
    private val args by navArgs<ProductDetailsScreenArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(binding == null){
            binding = ProductDetailsScreenBinding.inflate(inflater)
            viewModel.getProduct(args.productID)
            with(activity as AppCompatActivity) {
                setSupportActionBar(binding!!.toolbarContainer.toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = getString(R.string.details)
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
            toolbarContainer.toolbar.setNavigationOnClickListener {
                root.findNavController().popBackStack()
            }
            actionBtn.setOnClickListener {
                if(viewModel.product.value?.isInCart == true){
                    load(ProductDetailsScreenDirections.detailsToCheckout())
                }
                else{
                    viewModel.updateCart()
                }
            }
        }
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.apply {
            launch {
                viewModel.product.collectLatest { product ->
                    with(binding!!) {
                        product?.let {
                            title.text = product.title
                            image.loadWithPlaceholder(product.image)
                            price.text = product.price.formatPrice()
                            rating.rating = product.rating.rate
                            ratingCount.text = getString(
                                R.string.rating_count,
                                product.rating.count
                            )
                            description.text = product.description
                            actionBtn.setText(getString(
                                if(product.isInCart) R.string.view_cart
                                else R.string.add_cart
                            ))
                        }
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
                    }
                }
            }
        }
    }
}