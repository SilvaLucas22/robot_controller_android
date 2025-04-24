package com.robot_controller.networkParamsBottomSheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.robot_controller.R
import com.robot_controller.databinding.BottomSheetNetworkParamsBinding
import com.robot_controller.utils.isValidIp
import com.robot_controller.utils.isValidUdpPort

class NetworkParamsBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetNetworkParamsBinding
    private val listener: NetworkParamsBottomSheetListener by lazy {
        activity as NetworkParamsBottomSheetListener
    }

    private val ipAddress: String
        get() = binding.ipAddressEditText.text.toString()
    private val udpPort: String
        get() = binding.udpPortEditText.text.toString()

    companion object {
        const val TAG = "NETWORK_PARAMS_BOTTOM_SHEET"
        private const val IP_ADDRESS = "IP_ADDRESS"
        private const val UDP_PORT = "UDP_PORT"

        fun newInstance(
            ipAddress: String?,
            udpPort: String?
        ): NetworkParamsBottomSheet {
            val args = Bundle().apply {
                putString(IP_ADDRESS, ipAddress)
                putString(UDP_PORT, udpPort)
            }

            return NetworkParamsBottomSheet().apply {
                arguments = args
            }
        }
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

        val ipAddressPrefs = arguments?.getString(IP_ADDRESS)
        val udpPortPrefs = arguments?.getString(UDP_PORT)
        setupView(ipAddressPrefs, udpPortPrefs)
        setupListeners()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.findFragmentByTag(TAG) == null) super.show(manager, tag)
    }

    private fun setupView(ipAddressPrefs: String?, udpPortPrefs: String?) {
        with(binding) {
            ipAddressEditText.setText(ipAddressPrefs)
            udpPortEditText.setText(udpPortPrefs)
            saveButton.isEnabled = ipAddress.isValidIp() && udpPort.isValidUdpPort()
        }
    }

    private fun setupListeners() {
        with(binding) {
            cancelButton.setOnClickListener {
                dismiss()
            }

            saveButton.setOnClickListener {
                listener.onSavedNetworkParams(ipAddress, udpPort)
                dismiss()
            }

            val ipErrorMessage = getString(R.string.ip_address_error_text)
            setupValidation(ipAddressEditText, ipAddressInputLayout, ipErrorMessage) { it.isValidIp() }

            val portErrorMessage = getString(R.string.udp_port_error_text)
            setupValidation(udpPortEditText, udpPortInputLayout, portErrorMessage) { it.isValidUdpPort() }

            listOf(ipAddressEditText, udpPortEditText).forEach { editText ->
                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun afterTextChanged(p0: Editable?) {
                        saveButton.isEnabled = ipAddress.isValidIp() && udpPort.isValidUdpPort()
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

    interface NetworkParamsBottomSheetListener {
        fun onSavedNetworkParams(ipAddress: String, udpPort: String)
    }

}