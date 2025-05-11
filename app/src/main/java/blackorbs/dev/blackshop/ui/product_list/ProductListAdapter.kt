package blackorbs.dev.blackshop.ui.product_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import blackorbs.dev.blackshop.data.entity.ProductState
import blackorbs.dev.blackshop.databinding.ProductItemBinding
import blackorbs.dev.blackshop.ui.components.diffCallback
import blackorbs.dev.blackshop.utils.formatPrice
import blackorbs.dev.blackshop.utils.loadWithPlaceholder

class ProductListAdapter(
    private val itemClickCallback: (Int, ItemClickAction) -> Unit
): ListAdapter<ProductState, ProductListAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder (
            ProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(
        private val binding: ProductItemBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(product: ProductState){
            with(binding){
                addBtn.setLoading(product.isLoading)
                addBtn.setEnable(product.value.isInCart.not() && product.isLoading.not())
                title.text = product.value.title
                price.text = product.value.price.formatPrice()
                addBtn.setOnClickListener {
                    itemClickCallback(product.value.id, ItemClickAction.AddToCart)
                }
                root.setOnClickListener {
                    itemClickCallback(product.value.id, ItemClickAction.ViewDetails)
                }
                image.loadWithPlaceholder(product.value.image)
            }
        }
    }

}