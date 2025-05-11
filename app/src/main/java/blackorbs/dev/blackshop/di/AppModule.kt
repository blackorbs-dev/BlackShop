package blackorbs.dev.blackshop.di

import androidx.room.Room
import blackorbs.dev.blackshop.data.local.Database
import blackorbs.dev.blackshop.data.repository.ProductRepository
import blackorbs.dev.blackshop.data.repository.ProductRepositoryImpl
import blackorbs.dev.blackshop.ui.cart.CartViewModel
import blackorbs.dev.blackshop.ui.product_list.ProductListViewModel
import blackorbs.dev.blackshop.ui.login.LoginViewModel
import blackorbs.dev.blackshop.ui.product_details.ProductDetailsViewModel
import blackorbs.dev.blackshop.ui.signup.SignupViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    viewModelOf(::CartViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignupViewModel)
    viewModelOf(::ProductListViewModel)
    viewModelOf(::ProductDetailsViewModel)

    singleOf(::ProductRepositoryImpl){ bind<ProductRepository>() }

    single {
        get<Database>().productDao()
    }

    single {
        Room.databaseBuilder(get(), Database::class.java, "app_db").build()
    }
}