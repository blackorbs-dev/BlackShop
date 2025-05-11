package blackorbs.dev.blackshop.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity(tableName = "products")
@Serializable
data class Product(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    @Embedded val rating: Rating,
    @Transient val isInCart: Boolean = false
)

@Serializable
data class Rating(
    val rate: Float,
    val count: Int
)

data class ProductState(
    val value: Product,
    val isLoading: Boolean
)