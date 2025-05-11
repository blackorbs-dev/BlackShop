package blackorbs.dev.blackshop.ui.components

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import blackorbs.dev.blackshop.data.entity.ProductState

val diffCallback = object : ItemCallback<ProductState>() {
    override fun areItemsTheSame(oldItem: ProductState, newItem: ProductState) =
        oldItem.value.id == newItem.value.id

    override fun areContentsTheSame(oldItem: ProductState, newItem: ProductState) =
        oldItem == newItem
}