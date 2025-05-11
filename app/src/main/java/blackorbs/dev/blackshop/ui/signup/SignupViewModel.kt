package blackorbs.dev.blackshop.ui.signup

import androidx.lifecycle.ViewModel
import blackorbs.dev.blackshop.ui.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class SignupViewModel(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun signUp(email: String, password: String){
        _uiState.value = UiState.Loading
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                Timber.i("Sign up request completed!")
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