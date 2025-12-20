package com.robot_controller

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.robot_controller.data.local.PreferencesManager
import com.robot_controller.databinding.ActivityMainBinding
import com.robot_controller.joystick.JoystickCommandModel
import com.robot_controller.joystick.JoystickType.*
import com.robot_controller.joystick.JoystickView
import com.robot_controller.networkParamsBottomSheet.NetworkParamsBottomSheet
import kotlin.math.roundToInt

class MainActivity :
    AppCompatActivity(),
    JoystickView.JoystickListener,
    NetworkParamsBottomSheet.NetworkParamsBottomSheetListener
{

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupActionBar()
        setupSliderSpeed()
        setupJoysticks()
        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_config_network_params, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.networkParams -> showConfigNetworkParamsBottomSheet()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupViewModel() {
        val preferencesManager = PreferencesManager(this)
        val viewModelFactory = MainViewModelFactory(preferencesManager)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.actionBar.toolbar)
        title = "TCC Lucas da Silva"
    }

    private fun setupSliderSpeed() {
        with(binding) {
            speedSlider.addOnChangeListener { _, value, _ ->
                speedValue.text = value.roundToInt().toString()
            }
        }
    }

    private fun setupJoysticks() {
        with(binding) {
            robotJoystick.setup(this@MainActivity, ROBOT)
            cameraJoystick.setup(this@MainActivity, CAMERA)
        }
    }

    private fun showConfigNetworkParamsBottomSheet() {
        val (currentIpAddress, currentUdpPort) = viewModel.getNetworkParams() ?: Pair(null, null)
        val bottomSheet = NetworkParamsBottomSheet.newInstance(currentIpAddress, currentUdpPort)
        bottomSheet.show(supportFragmentManager, NetworkParamsBottomSheet.TAG)
    }

    private fun setupObservers() {
        viewModel.onErrorLiveData.observe(this) { error ->
            Toast
                .makeText(this, error.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onJoystickButtonClicked(joystickCommandModel: JoystickCommandModel) {
        if (joystickCommandModel.joystickType == ROBOT)
            joystickCommandModel.speed = binding.speedSlider.value.roundToInt()

        viewModel.sendJoystickCommand(joystickCommandModel)
    }

    override fun onSavedNetworkParams(ipAddress: String, udpPort: String) {
        viewModel.saveNetworkParams(ipAddress, udpPort)
    }

}