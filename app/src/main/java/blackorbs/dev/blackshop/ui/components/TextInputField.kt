package blackorbs.dev.blackshop.ui.components

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import blackorbs.dev.blackshop.R
import blackorbs.dev.blackshop.databinding.TextInputFieldBinding
import com.google.android.material.textfield.TextInputLayout

class TextInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding: TextInputFieldBinding =
        TextInputFieldBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.TextInputField, 0, 0)
            .apply {
                try {
                    val hint = getString(R.styleable.TextInputField_hintText)
                    val inputTypeEnum = getInt(R.styleable.TextInputField_inputType, 1)

                    binding.inputLayout.hint = hint ?: ""

                    binding.editText.inputType = when (inputTypeEnum) {
                        2 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        3 -> {
                            binding.inputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }
                        else -> InputType.TYPE_CLASS_TEXT
                    }
                }
                finally {
                    recycle()
                }
            }
    }

    val text get() = binding.editText.text?.toString()?.trim().orEmpty()
        .apply {
            if(isEmpty()) showError()
        }

    private fun showError(){
        binding.editText.error = context.getString(R.string.input_error)
    }
}
