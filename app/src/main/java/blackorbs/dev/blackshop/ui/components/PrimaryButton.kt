package blackorbs.dev.blackshop.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import blackorbs.dev.blackshop.R
import blackorbs.dev.blackshop.databinding.PrimaryButtonBinding
import com.google.android.material.button.MaterialButton

class PrimaryButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding = PrimaryButtonBinding.inflate(LayoutInflater.from(context), this)

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PrimaryButton, 0, 0)
            .apply {
                try {
                    val text = getString(R.styleable.PrimaryButton_text)
                    val icon = getResourceId(R.styleable.PrimaryButton_icon, 0)

                    binding.button.text = text ?: ""
                    if (icon != 0) {
                        binding.button.icon = AppCompatResources.getDrawable(context, icon)
                        binding.button.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                    }
                } finally {
                    recycle()
                }
            }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        binding.button.setOnClickListener(listener)
    }

    fun setText(value: String){
        binding.button.text = value
    }

    fun updateState(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                loadingIndicator.show()
                button.isEnabled = false
            } else {
                loadingIndicator.hide()
                button.isEnabled = true
            }
        }
    }

    fun setLoading(isLoading: Boolean){
        if (isLoading) {
            binding.loadingIndicator.show()
        } else {
            binding.loadingIndicator.hide()
        }
    }

    fun setEnable(value: Boolean){
        binding.button.isEnabled = value
    }
}