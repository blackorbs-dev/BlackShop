package blackorbs.dev.blackshop.utils

import android.widget.ImageView
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.viewbinding.ViewBinding
import blackorbs.dev.blackshop.R
import coil.load
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.lang.Exception
import java.text.NumberFormat

fun ViewBinding.load(directions: NavDirections){
    root.findNavController()
        .apply {
            currentDestination?.getAction(directions.actionId)?.run { navigate(directions) }
        }
}

fun ViewBinding.showFeedback(e: Exception?){
    Snackbar.make(
        root,
        if(e == null) root.context.getString(R.string.error_try_again)
        else e.localizedMessage ?: e.message ?: e.toString(),
        4000
    ).show()
}

fun Double.formatPrice(): String =
    NumberFormat.getCurrencyInstance().apply {
        maximumFractionDigits = 2
    }.format(this)

fun ImageView.loadWithPlaceholder(url: String?) =
    load(url){
        placeholder(R.drawable.placeholder)
        error(R.drawable.placeholder)
    }

fun Exception.log(): String{
    return localizedMessage?:message?:toString()
        .also { Timber.e(it) }
}
