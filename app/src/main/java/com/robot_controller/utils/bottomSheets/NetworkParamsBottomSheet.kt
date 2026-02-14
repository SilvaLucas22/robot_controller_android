package com.robot_controller.utils.bottomSheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.robot_controller.R
import com.robot_controller.databinding.BottomSheetNetworkParamsBinding
import com.robot_controller.utils.extensions.enableWhenAllValid
import com.robot_controller.utils.extensions.isValidIpOrDomain
import com.robot_controller.utils.extensions.isValidPort
import com.robot_controller.utils.extensions.setupValidation

class NetworkParamsBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetNetworkParamsBinding
    private val listener: NetworkParamsBottomSheetListener by lazy {
        activity as NetworkParamsBottomSheetListener
    }

    private val ipOrDomain: String
        get() = binding.ipOrDomainEditText.text.toString()
    private val tcpPortCommands: String
        get() = binding.tcpPortCommandsEditText.text.toString()
    private val httpPortVideo: String
        get() = binding.httpPortVideoEditText.text.toString()

    companion object {
        const val TAG = "NETWORK_PARAMS_BOTTOM_SHEET"
        private const val IP_OR_DOMAIN = "IP_OR_DOMAIN"
        private const val TCP_PORT_COMMANDS = "TCP_PORT_COMMANDS"
        private const val HTTP_PORT_VIDEO = "HTTP_PORT_VIDEO"

        fun newInstance(
            ipOrDomain: String?,
            tcpPortCommands: String?,
            httpPortVideo: String?,
        ): NetworkParamsBottomSheet {
            val args = Bundle().apply {
                putString(IP_OR_DOMAIN, ipOrDomain)
                putString(TCP_PORT_COMMANDS, tcpPortCommands)
                putString(HTTP_PORT_VIDEO, httpPortVideo)
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

        val ipOrDomainPrefs = arguments?.getString(IP_OR_DOMAIN)
        val tcpPortCommandsPrefs = arguments?.getString(TCP_PORT_COMMANDS)
        val httpPortVideoPrefs = arguments?.getString(HTTP_PORT_VIDEO)
        setupView(ipOrDomainPrefs, tcpPortCommandsPrefs, httpPortVideoPrefs)
        setupListeners()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.findFragmentByTag(TAG) == null) super.show(manager, tag)
    }

    private fun setupView(
        ipAddressPrefs: String?,
        tcpPortCommandsPrefs: String?,
        httpPortVideoPrefs: String?,
        ) {
        with(binding) {
            ipOrDomainEditText.setText(ipAddressPrefs)
            tcpPortCommandsEditText.setText(tcpPortCommandsPrefs)
            httpPortVideoEditText.setText(httpPortVideoPrefs)
        }
    }

    private fun setupListeners() {
        with(binding) {
            cancelButton.setOnClickListener {
                dismiss()
            }

            saveButton.setOnClickListener {
                listener.onSavedNetworkParams(ipOrDomain, tcpPortCommands, httpPortVideo)
                dismiss()
            }

            val ipErrorMessage = getString(R.string.ip_domain_error_text)
            ipOrDomainEditText.setupValidation(ipOrDomainInputLayout, ipErrorMessage) { it.isValidIpOrDomain() }

            val portErrorMessage = getString(R.string.port_error_text)
            tcpPortCommandsEditText.setupValidation(tcpPortCommandsInputLayout, portErrorMessage) { it.isValidPort() }
            httpPortVideoEditText.setupValidation(httpPortVideoInputLayout, portErrorMessage) { it.isValidPort() }

            val editTexts = listOf(ipOrDomainEditText, tcpPortCommandsEditText, httpPortVideoEditText)
            saveButton.enableWhenAllValid(editTexts) {
                ipOrDomain.isValidIpOrDomain() && tcpPortCommands.isValidPort() && httpPortVideo.isValidPort()
            }
        }
    }

    interface NetworkParamsBottomSheetListener {
        fun onSavedNetworkParams(ipOrDomain: String, tcpPortCommands: String, httpPortVideo: String)
    }
}