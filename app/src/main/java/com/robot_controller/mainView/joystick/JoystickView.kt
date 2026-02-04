package com.robot_controller.mainView.joystick

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.robot_controller.databinding.ViewJoystickBinding
import com.robot_controller.mainView.joystick.JoystickCommand.*

class JoystickView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewJoystickBinding = ViewJoystickBinding
        .inflate(LayoutInflater.from(context), this, true)
    private lateinit var listener: JoystickListener
    private lateinit var joystickType: JoystickType

    fun setup(listener: JoystickListener, joystickType: JoystickType) {
        this.listener = listener
        this.joystickType = joystickType
        setupJoystickIcon()
        setupButtonsListener()
    }

    private fun setupJoystickIcon() {
        binding.joystickCenterImage.setImageResource(joystickType.icon)
    }

    private fun setupButtonsListener() {
        with(binding) {
            arrowUp.setOnClickListener {
                listener.onJoystickButtonClicked(JoystickCommandModel(joystickType, UP))
            }
            arrowDown.setOnClickListener {
                listener.onJoystickButtonClicked(JoystickCommandModel(joystickType, DOWN))
            }
            arrowLeft.setOnClickListener {
                listener.onJoystickButtonClicked(JoystickCommandModel(joystickType, LEFT))
            }
            arrowRight.setOnClickListener {
                listener.onJoystickButtonClicked(JoystickCommandModel(joystickType, RIGHT))
            }
        }
    }

    interface JoystickListener {
        fun onJoystickButtonClicked(joystickCommandModel: JoystickCommandModel)
    }
}