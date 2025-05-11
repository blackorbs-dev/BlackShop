package blackorbs.dev.blackshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import blackorbs.dev.blackshop.data.entity.Product

@Database(
    entities = [Product::class],
    version = 1,
    exportSchema = false
)
abstract class Database: RoomDatabase() {
    abstract fun productDao(): ProductDao
}