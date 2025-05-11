package blackorbs.dev.blackshop.viewmodel

import androidx.test.filters.SmallTest
import blackorbs.dev.blackshop.helpers.util.MainCoroutineRule
import blackorbs.dev.blackshop.ui.UiState
import blackorbs.dev.blackshop.ui.login.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@SmallTest
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authResult: Task<AuthResult>
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        firebaseAuth = mockk()
        authResult = mockk()
        viewModel = LoginViewModel(firebaseAuth)
    }

    @Test
    fun `login success emits UiState Success`() = runTest {
        // Arrange
        val listenerSlot = slot<OnCompleteListener<AuthResult>>()
        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns authResult
        every { authResult.addOnCompleteListener(capture(listenerSlot)) } answers {
            listenerSlot.captured.onComplete(mockk(relaxed = true) {
                every { isSuccessful } returns true
            })
            authResult
        }

        // Act
        viewModel.login("test@example.com", "password")

        // Assert
        advanceUntilIdle()
        assertThat(viewModel.uiState.value).isEqualTo(UiState.Success)
    }

    @Test
    fun `login failure emits UiState Error`() = runTest {
        val listenerSlot = slot<OnCompleteListener<AuthResult>>()
        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns authResult
        every { authResult.addOnCompleteListener(capture(listenerSlot)) } answers {
            listenerSlot.captured.onComplete(mockk(relaxed = true) {
                every { isSuccessful } returns false
            })
            authResult
        }

        viewModel.login("test@example.com", "wrongpass")

        advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(UiState.Error::class.java)
    }
}
