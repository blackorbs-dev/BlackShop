package blackorbs.dev.blackshop.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import blackorbs.dev.blackshop.databinding.LoginScreenBinding
import blackorbs.dev.blackshop.ui.UiState
import blackorbs.dev.blackshop.utils.load
import blackorbs.dev.blackshop.utils.showFeedback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginScreen: Fragment() {
    private var binding: LoginScreenBinding? = null
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(binding == null){
            binding = LoginScreenBinding.inflate(inflater)
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
                login()
            }
            gotoSignUp.setOnClickListener {
                load(LoginScreenDirections.loginToSignup())
            }
        }
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest(::handleState)
        }
    }

    private fun login(){
        val email = binding!!.email.text
        val password = binding!!.password.text
        if(email.isNotEmpty() && password.isNotEmpty()){
            viewModel.login(email, password)
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
                    load(LoginScreenDirections.loginToProductList())
                }
            }
        }
    }
}