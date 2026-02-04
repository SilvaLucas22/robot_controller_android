package com.robot_controller.utils.bottomSheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.robot_controller.R
import com.robot_controller.databinding.BottomSheetEnvironmentNameBinding
import com.robot_controller.utils.extensions.enableWhenAllValid
import com.robot_controller.utils.extensions.setupValidation

class EnvironmentNameBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetEnvironmentNameBinding
    private val listener: EnvironmentNameBottomSheetListener by lazy {
        parentFragment as EnvironmentNameBottomSheetListener
    }

    private val environmentName: String
        get() = binding.environmentNameEditText.text.toString()

    companion object {
        const val TAG = "ENVIRONMENT_NAME_BOTTOM_SHEET"
        fun newInstance() = EnvironmentNameBottomSheet()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetEnvironmentNameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.findFragmentByTag(TAG) == null) super.show(manager, tag)
    }

    private fun setupListeners() {
        with(binding) {
            cancelButton.setOnClickListener {
                dismiss()
            }

            saveButton.setOnClickListener {
                listener.onEnvironmentNameSaved(environmentName)
                dismiss()
            }

            val environmentNameErrorMessage = getString(R.string.environment_name_error_text)
            environmentNameEditText.setupValidation(environmentNameInputLayout, environmentNameErrorMessage) {
                it.isNotBlank()
            }

            saveButton.enableWhenAllValid(listOf(environmentNameEditText)) {
                environmentName.isNotBlank()
            }
        }
    }

    interface EnvironmentNameBottomSheetListener {
        fun onEnvironmentNameSaved(environmentName: String)
    }
}
