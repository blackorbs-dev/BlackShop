package blackorbs.dev.blackshop.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import blackorbs.dev.blackshop.data.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(products: List<Product>)

    @Query("UPDATE products SET isInCart = :isInCart WHERE id = :id")
    suspend fun updateCartItem(id: Int, isInCart: Boolean)

    @Query("UPDATE products SET isInCart = 0 WHERE id IN (:ids)")
    suspend fun checkoutItems(ids: List<Int>)

    @Query("SELECT * FROM products WHERE id=:id")
    fun getItem(id: Int): Flow<Product?>

    @Query("SELECT * FROM products")
    fun getAll(): Flow<List<Product>>

    @Query("SELECT EXISTS (SELECT 1 FROM products LIMIT 1)")
    suspend fun hasData(): Boolean

    @Query("SELECT id FROM products WHERE isInCart = 1")
    suspend fun getCartItemIds(): List<Int>

    @Query("SELECT * FROM products WHERE isInCart = 1")
    fun getCartItems(): Flow<List<Product>>
}