package blackorbs.dev.blackshop.data.repository

import blackorbs.dev.blackshop.data.entity.Response
import blackorbs.dev.blackshop.utils.log
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.UnresolvedAddressException
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.CancellationException

object RequestHandler {

    suspend fun<T> execute(request: suspend () -> T): Response<T>{
        val (message, data) = safeCall(request)
        return if(message == null){
            Response.Success(data)
        }
        else {
            Response.Error(message)
        }
    }

    private suspend fun<T> safeCall(
        request: suspend () -> T
    ): Pair<String?, T?> =
        try {
            Timber.d( "Executing request...")
            null to request().also {
                Timber.d( "Api response received")
            }
        }
        catch (e: IOException){ e.log() to null }
        catch (e: ClientRequestException){ e.log() to null }
        catch (e: ServerResponseException){ e.log() to null }
        catch (e: ResponseException){ e.log() to null }
        catch (e: UnresolvedAddressException){ e.log() to null }
        catch (e: JsonConvertException){ e.log() to null }
        catch (e: CancellationException){ e.log() to null }
        catch (e: NoTransformationFoundException){ e.log() to null }

}