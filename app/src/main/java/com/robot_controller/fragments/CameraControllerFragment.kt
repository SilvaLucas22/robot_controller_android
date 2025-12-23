package com.robot_controller.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.robot_controller.MainViewModel
import com.robot_controller.R
import com.robot_controller.databinding.FragmentCameraControllerBinding
import com.robot_controller.joystick.JoystickCommandModel
import com.robot_controller.joystick.JoystickType
import com.robot_controller.joystick.JoystickView
import com.robot_controller.utils.enums.RobotModule
import com.robot_controller.utils.extensions.isValidIpOrDomain
import com.robot_controller.utils.extensions.isValidPanOrTilt
import com.robot_controller.utils.extensions.isValidPort

class CameraControllerFragment :
    Fragment(),
    JoystickView.JoystickListener
{
    private lateinit var binding: FragmentCameraControllerBinding
    private lateinit var viewModel: MainViewModel

    private val panValue: String
        get() = binding.panEditText.text.toString()
    private val tiltValue: String
        get() = binding.tiltEditText.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        FragmentCameraControllerBinding.inflate(layoutInflater, container, false).let {
            binding = it
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupJoystick()
        setupListeners()
        binding.goToButton.isEnabled = panValue.isValidPanOrTilt() && tiltValue.isValidPanOrTilt()
    }

    private fun setupJoystick() {
        binding.cameraJoystick.setup(this@CameraControllerFragment, JoystickType.CAMERA)
    }

    private fun setupListeners() {
        with(binding) {
            stopButton.setOnClickListener {
                viewModel.sendStopCommand(RobotModule.CAMERA)
            }

            centralizeCameraButton.setOnClickListener {
                viewModel.centralizeCamera()
            }

            goToButton.setOnClickListener {
                val pan = panValue.ifEmpty { null }
                val tilt = tiltValue.ifEmpty { null }
                viewModel.goToPanTilt(pan = pan?.toInt(), tilt = tilt?.toInt())
            }

            val errorMessage = getString(R.string.invalid_value)
            setupValidation(panEditText, panInputLayout, errorMessage) { it.isValidPanOrTilt() }
            setupValidation(tiltEditText, tiltInputLayout, errorMessage) { it.isValidPanOrTilt() }

            listOf(panEditText, tiltEditText).forEach { editText ->
                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {
                        goToButton.isEnabled = panValue.isValidPanOrTilt() && tiltValue.isValidPanOrTilt()
                    }
                })
            }
        }
    }

    private fun setupValidation(
        editText: TextInputEditText,
        textInputLayout: TextInputLayout,
        errorMessage: String,
        validationRule: (String) -> Boolean
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                textInputLayout.error = if (validationRule(p0.toString())) null else errorMessage
            }
        })
    }

    override fun onJoystickButtonClicked(joystickCommandModel: JoystickCommandModel) {
        viewModel.sendJoystickCommand(joystickCommandModel)
    }
}