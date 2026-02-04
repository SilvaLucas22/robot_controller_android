package com.robot_controller.mainView.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.robot_controller.R
import com.robot_controller.databinding.FragmentAntennaControllerBinding
import com.robot_controller.mainView.MainViewModel
import com.robot_controller.utils.extensions.enableWhenAllValid
import com.robot_controller.utils.extensions.isValidPanOrTilt
import com.robot_controller.utils.extensions.setupValidation

class AntennaControllerFragment :
    Fragment()
{
    private lateinit var binding: FragmentAntennaControllerBinding
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

        FragmentAntennaControllerBinding.inflate(layoutInflater, container, false).let {
            binding = it
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialValues()
        setupObservers()
        setupListeners()
    }

    private fun setupInitialValues() {
        with(binding) {
            antennaPanValue.text = getString(R.string.pan_value, 0)
            antennaTiltValue.text = getString(R.string.tilt_value, 0)
            antennaLqiValue.text = getString(R.string.lqi_value, 0.0)
        }
    }

    private fun setupObservers() {
        viewModel.antennaInfoLiveData.observe(viewLifecycleOwner) {
            with(binding) {
                antennaPanValue.text = getString(R.string.pan_value, it.pan)
                antennaTiltValue.text = getString(R.string.tilt_value, it.tilt)
                antennaLqiValue.text = getString(R.string.lqi_value, it.lqi ?: 0.0)
            }
        }
    }

    private fun setupListeners() {
        with(binding) {
            antennaInfoButton.setOnClickListener {
                viewModel.getAllAntennaInfo()
            }

            goToButton.setOnClickListener {
                val pan = panValue.ifEmpty { null }
                val tilt = tiltValue.ifEmpty { null }
                viewModel.goToAntennaPanTilt(pan = pan?.toInt(), tilt = tilt?.toInt())
            }

            val errorMessage = getString(R.string.invalid_value)
            panEditText.setupValidation(panInputLayout, errorMessage) { it.isValidPanOrTilt() }
            tiltEditText.setupValidation(tiltInputLayout, errorMessage) { it.isValidPanOrTilt() }

            goToButton.enableWhenAllValid(listOf(panEditText, tiltEditText)) {
                if (panValue.isBlank() && tiltValue.isBlank()) false
                else panValue.isValidPanOrTilt() || tiltValue.isValidPanOrTilt()
            }
        }
    }
}
