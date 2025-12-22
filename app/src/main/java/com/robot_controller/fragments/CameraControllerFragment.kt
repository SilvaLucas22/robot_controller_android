package com.robot_controller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.robot_controller.MainViewModel
import com.robot_controller.databinding.FragmentCameraControllerBinding
import com.robot_controller.joystick.JoystickCommandModel
import com.robot_controller.joystick.JoystickType
import com.robot_controller.joystick.JoystickView
import com.robot_controller.utils.enums.RobotModule

class CameraControllerFragment :
    Fragment(),
    JoystickView.JoystickListener
{
    private lateinit var binding: FragmentCameraControllerBinding
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

        FragmentCameraControllerBinding.inflate(layoutInflater, container, false).let {
            binding = it
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupJoystick()
        setupListeners()
    }

    private fun setupJoystick() {
        binding.cameraJoystick.setup(this@CameraControllerFragment, JoystickType.CAMERA)
    }

    private fun setupListeners() {
        binding.stopButton.setOnClickListener {
            viewModel.sendStopCommand(RobotModule.CAMERA)
        }
    }

    override fun onJoystickButtonClicked(joystickCommandModel: JoystickCommandModel) {
        viewModel.sendJoystickCommand(joystickCommandModel)
    }
}