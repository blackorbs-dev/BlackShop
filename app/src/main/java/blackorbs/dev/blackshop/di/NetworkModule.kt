package blackorbs.dev.blackshop.di

import blackorbs.dev.blackshop.data.remote.Api
import blackorbs.dev.blackshop.data.remote.ProductService
import blackorbs.dev.blackshop.data.remote.ProductServiceImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import timber.log.Timber

val networkModule = module {

    single { Firebase.auth }
    singleOf(::ProductServiceImpl){ bind<ProductService>() }

    single {
        HttpClient(CIO) {
            defaultRequest { url(Api.BASE_URL) }
            install(ContentNegotiation) {
                json(
                    Json {
                        explicitNulls = false
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.i(message)
                    }
                }
            }
        }
    }
}