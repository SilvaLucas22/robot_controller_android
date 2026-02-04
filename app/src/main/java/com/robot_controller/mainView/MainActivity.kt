package com.robot_controller.mainView

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.robot_controller.R
import com.robot_controller.data.local.PreferencesManager
import com.robot_controller.databinding.ActivityMainBinding
import com.robot_controller.mainView.fragments.AntennaControllerFragment
import com.robot_controller.mainView.fragments.AutonomyControllerFragment
import com.robot_controller.mainView.fragments.CameraControllerFragment
import com.robot_controller.mainView.fragments.SystemControllerFragment
import com.robot_controller.mainView.fragments.TractionControllerFragment
import com.robot_controller.utils.bottomSheets.NetworkParamsBottomSheet

class MainActivity :
    AppCompatActivity(),
    NetworkParamsBottomSheet.NetworkParamsBottomSheetListener
{
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window = this.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = true

        setupViewModel()
        setupActionBar()
        setupFragments()
        setupObservers()
        setupListeners()
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

    private fun setupFragments() {
        val fragmentsPageAdapter = FragmentsPageAdapter(this)

        val fragmentsList =
            listOf(
                Pair(R.drawable.ic_car, TractionControllerFragment()),
                Pair(R.drawable.ic_camera, CameraControllerFragment()),
                Pair(R.drawable.ic_antenna, AntennaControllerFragment()),
                Pair(R.drawable.ic_gps, SystemControllerFragment()),
                Pair(R.drawable.ic_where_am_i, AutonomyControllerFragment()),
            )

        fragmentsPageAdapter.fragments.addAll(fragmentsList.map { it.second })

        with(binding) {
            viewPager.apply {
                offscreenPageLimit = fragmentsList.size
                currentItem = 0
                adapter = fragmentsPageAdapter
                isUserInputEnabled = true
            }

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.setIcon(fragmentsList[position].first)
            }.attach()
        }
    }

    private fun setupObservers() {
        viewModel.onErrorLiveData.observe(this) { error ->
            Toast
                .makeText(this, error.message, Toast.LENGTH_SHORT)
                .show()
        }

        viewModel.videoFrameLiveData.observe(this) { frameAsBitmap ->
            binding.robotVideo.setImageBitmap(frameAsBitmap)
        }

        viewModel.videoPlayingLiveData.observe(this) { isPlaying ->
            binding.playButton.isVisible = !isPlaying
        }

        viewModel.autonomyFeedbackLiveData.observe(this) { feedbackText ->
            Toast
                .makeText(this, feedbackText, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupListeners() {
        binding.playButton.setOnClickListener {
            viewModel.playVideo()
        }
    }

    //region Config Network Params Bottom Sheet

    private fun showConfigNetworkParamsBottomSheet() {
        val (currentIpOrDomain, currentTcpPortCommands, currentTcpPortVideo) = viewModel.getNetworkParams() ?: Triple(null, null, null)
        val bottomSheet = NetworkParamsBottomSheet.newInstance(currentIpOrDomain, currentTcpPortCommands, currentTcpPortVideo)
        bottomSheet.show(supportFragmentManager, NetworkParamsBottomSheet.TAG)
    }

    override fun onSavedNetworkParams(ipOrDomain: String, tcpPortCommands: String, tcpPortVideo: String) {
        viewModel.saveNetworkParams(ipOrDomain, tcpPortCommands, tcpPortVideo)
    }

    //endregion
}
