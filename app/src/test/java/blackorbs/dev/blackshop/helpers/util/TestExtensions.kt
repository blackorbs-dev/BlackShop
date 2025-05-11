package blackorbs.dev.blackshop.helpers.util

import blackorbs.dev.blackshop.data.ProductServiceTest

object TestExtensions {

    fun loadJsonFromResources(path: String): String {
        return requireNotNull(
            ProductServiceTest::class.java.classLoader?.getResource(path)
        ) { "Resource not found: $path" }
            .readText()
    }
}
