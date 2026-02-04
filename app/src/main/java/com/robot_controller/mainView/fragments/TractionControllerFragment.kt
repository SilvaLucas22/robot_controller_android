package com.robot_controller.mainView.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.robot_controller.mainView.MainViewModel
import com.robot_controller.databinding.FragmentTractionControllerBinding
import com.robot_controller.mainView.joystick.JoystickCommandModel
import com.robot_controller.mainView.joystick.JoystickType
import com.robot_controller.mainView.joystick.JoystickView
import com.robot_controller.utils.enums.RobotModule
import kotlin.math.roundToInt

class TractionControllerFragment :
    Fragment(),
    JoystickView.JoystickListener
{
    private lateinit var binding: FragmentTractionControllerBinding
    private lateinit var viewModel: MainViewModel

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

        FragmentTractionControllerBinding.inflate(layoutInflater, container, false).let {
            binding = it
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSliderSpeed()
        setupJoystick()
        setupListeners()
    }

    private fun setupSliderSpeed() {
        with(binding) {
            speedSlider.addOnChangeListener { _, value, _ ->
                speedValue.text = value.roundToInt().toString()
            }
        }
    }

    private fun setupJoystick() {
        binding.robotJoystick.setup(this@TractionControllerFragment, JoystickType.ROBOT)
    }

    private fun setupListeners() {
        binding.stopButton.setOnClickListener {
            viewModel.sendStopCommand(RobotModule.TRACTION)
        }
    }

    override fun onJoystickButtonClicked(joystickCommandModel: JoystickCommandModel) {
//        if (joystickCommandModel.joystickType == JoystickType.ROBOT)
//            joystickCommandModel.speed = binding.speedSlider.value.roundToInt()
        // For now, speed is not included on JSON robot protocol

        viewModel.sendJoystickCommand(joystickCommandModel)
    }
}
