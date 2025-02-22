package com.robot_controller.networkParamsBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.robot_controller.databinding.BottomSheetNetworkParamsBinding

class NetworkParamsBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetNetworkParamsBinding

    companion object {
        const val TAG = "NETWORK_PARAMS_BOTTOM_SHEET"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetNetworkParamsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupListeners()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.findFragmentByTag(TAG) == null) super.show(manager, tag)
    }

    private fun setupView() {

    }

    private fun setupListeners() {
        with(binding) {
            cancelButton.setOnClickListener {
                dismiss()
            }

            saveButton.setOnClickListener {
                dismiss()
            }
        }
    }

}