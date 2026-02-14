package com.robot_controller.mainView.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.robot_controller.mainView.MainViewModel
import com.robot_controller.databinding.FragmentSystemControllerBinding

class SystemControllerFragment : Fragment() {
    private lateinit var binding: FragmentSystemControllerBinding
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

        FragmentSystemControllerBinding.inflate(layoutInflater, container, false).let {
            binding = it
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.compassValueLiveData.observe(viewLifecycleOwner) { compassValue ->
            binding.compassValue.text = compassValue.toString()
        }
    }

    private fun setupListeners() {
        binding.stopAllButton.setOnClickListener {
            viewModel.sendStopAllCommand()
        }

        binding.stopVideoButton.setOnClickListener {
            viewModel.stopVideoStreaming()
        }

        binding.compassButton.setOnClickListener {
            viewModel.getCompassValue()
//            viewModel.getTelemetry()
//            viewModel.getStatusVideoStreaming()
        }
    }
}