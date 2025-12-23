package com.robot_controller

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.robot_controller.data.local.PreferencesManager
import com.robot_controller.databinding.ActivityMainBinding
import com.robot_controller.fragments.AutonomyControllerFragment
import com.robot_controller.fragments.CameraControllerFragment
import com.robot_controller.fragments.SystemControllerFragment
import com.robot_controller.fragments.TractionControllerFragment
import com.robot_controller.networkParamsBottomSheet.NetworkParamsBottomSheet

class MainActivity :
    AppCompatActivity(),
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
                Pair(R.drawable.ic_gps, SystemControllerFragment()),
                Pair(R.drawable.ic_where_am_i, AutonomyControllerFragment()),
            )

        fragmentsPageAdapter.fragments.addAll(fragmentsList.map { it.second })

        with(binding) {
            viewPager.apply {
                offscreenPageLimit = 4
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
            with(binding) {
                if (playButton.isVisible) playButton.isVisible = false

                robotVideo.setImageBitmap(frameAsBitmap)
            }
        }
    }

    private fun setupListeners() {
        binding.playButton.setOnClickListener {
            viewModel.playVideo()
        }
    }

    private fun showConfigNetworkParamsBottomSheet() {
        val (currentIpOrDomain, currentTcpPortCommands, currentTcpPortVideo) = viewModel.getNetworkParams() ?: Triple(null, null, null)
        val bottomSheet = NetworkParamsBottomSheet.newInstance(currentIpOrDomain, currentTcpPortCommands, currentTcpPortVideo)
        bottomSheet.show(supportFragmentManager, NetworkParamsBottomSheet.TAG)
    }

    override fun onSavedNetworkParams(ipOrDomain: String, tcpPortCommands: String, tcpPortVideo: String) {
        viewModel.saveNetworkParams(ipOrDomain, tcpPortCommands, tcpPortVideo)
    }
}
