package blackorbs.dev.blackshop.di

import androidx.room.Room
import blackorbs.dev.blackshop.data.local.Database
import blackorbs.dev.blackshop.data.remote.Api
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import timber.log.Timber

val testAppModule = module {

    single<Database> {
        Room.inMemoryDatabaseBuilder(get(), Database::class.java)
            .allowMainThreadQueries()
            .build()
    }

    single {
        get<Database>().productDao()
    }
}