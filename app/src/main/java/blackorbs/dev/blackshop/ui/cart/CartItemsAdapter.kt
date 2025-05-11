package blackorbs.dev.blackshop.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import blackorbs.dev.blackshop.data.entity.ProductState
import blackorbs.dev.blackshop.databinding.CartItemBinding
import blackorbs.dev.blackshop.ui.components.diffCallback
import blackorbs.dev.blackshop.utils.formatPrice
import blackorbs.dev.blackshop.utils.loadWithPlaceholder

class CartItemsAdapter(
    private val itemClickCallback: (Int) -> Unit
): ListAdapter<ProductState, CartItemsAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder (
            CartItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(
        private val binding: CartItemBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(product: ProductState){
            with(binding){
                if(product.isLoading) {
                    loader.show()
                    removeBtn.visibility = View.GONE
                } else {
                    loader.hide()
                    removeBtn.visibility = View.VISIBLE
                }
                title.text = product.value.title
                price.text = product.value.price.formatPrice()
                removeBtn.setOnClickListener {
                    itemClickCallback(product.value.id)
                }
                image.loadWithPlaceholder(product.value.image)
            }
        }
    }
}