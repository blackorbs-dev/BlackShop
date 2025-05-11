package blackorbs.dev.blackshop.util

import blackorbs.dev.blackshop.data.entity.Product
import blackorbs.dev.blackshop.data.entity.Rating

object TestUtil {
    private var _id: Int = 0

    fun testProduct(
        id: Int? = null,
        title: String = "ProductTest",
        description: String = "Just a product for testing",
        image: String = "https://dummyurl",
        category: String = "No Category",
        price: Double = 45.66,
        rating: Rating = Rating(
            rate = 4.5f,
            count = 124
        ),
        isInCart: Boolean = false
    ): Product {
        _id += 1
        return Product(
            id = id ?: _id,
            title = title,
            description = description,
            image = image,
            category = category,
            price = price,
            rating = rating,
            isInCart = isInCart
        )
    }
}