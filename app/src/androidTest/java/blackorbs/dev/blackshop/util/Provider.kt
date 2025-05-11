package blackorbs.dev.blackshop.util

import blackorbs.dev.blackshop.di.MutableMockHandler
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

fun provideTestHttpClient(
    mutableHandler: MutableMockHandler
): HttpClient {
    return HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                mutableHandler.handler(this, request)
            }
        }
        expectSuccess = true
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

fun loadJsonFromResources(path: String): String {
    return requireNotNull(
        TestApp::class.java.classLoader?.getResource(path)
    ) { "Resource not found: $path" }
        .readText()
}