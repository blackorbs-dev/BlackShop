package blackorbs.dev.blackshop.data.entity

sealed interface Response<out T>{
    data object Loading: Response<Nothing>
    data class Error(val message: String): Response<Nothing>
    data class Success<out T>(val data: T?): Response<T>
}
