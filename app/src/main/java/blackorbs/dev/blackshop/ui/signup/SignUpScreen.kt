package blackorbs.dev.blackshop.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import blackorbs.dev.blackshop.databinding.SignupScreenBinding
import blackorbs.dev.blackshop.ui.UiState
import blackorbs.dev.blackshop.utils.load
import blackorbs.dev.blackshop.utils.showFeedback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpScreen: Fragment() {
    private var binding: SignupScreenBinding? = null
    private val viewModel: SignupViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(binding == null){
            binding = SignupScreenBinding.inflate(inflater)
            setupListeners()
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupListeners(){
        with(binding!!){
            actionBtn.setOnClickListener {
                signUp()
            }
            goToLogin.setOnClickListener {
                goBack()
            }
        }
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest(::handleState)
        }
    }

    private fun signUp(){
        val email = binding!!.email.text
        val password = binding!!.password.text
        if(email.isNotEmpty() && password.isNotEmpty()){
            viewModel.signUp(email, password)
        }
    }

    private fun handleState(state: UiState){
        with(binding!!) {
            when(state){
                is UiState.Error -> {
                    showFeedback(state.exception)
                    viewModel.resetState()
                }
                UiState.Idle -> {
                    actionBtn.updateState(false)
                }
                UiState.Loading -> {
                    actionBtn.updateState(true)
                }
                UiState.Success -> {
                    load(SignUpScreenDirections.signupToProductList())
                }
            }
        }
    }

    private fun goBack(){
       binding!!.root.findNavController().popBackStack()
    }
}