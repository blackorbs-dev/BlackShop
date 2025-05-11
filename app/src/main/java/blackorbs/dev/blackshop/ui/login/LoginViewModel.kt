package blackorbs.dev.blackshop.ui.login

import androidx.lifecycle.ViewModel
import blackorbs.dev.blackshop.ui.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class LoginViewModel(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun login(email: String, password: String){
        _uiState.value = UiState.Loading
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Timber.i("Login request completed!")
                if(task.isSuccessful){
                    _uiState.value = UiState.Success
                }
                else{
                    _uiState.value = UiState.Error(task.exception)
                }
            }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }

}