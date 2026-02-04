package com.robot_controller.mainView.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.robot_controller.mainView.MainViewModel
import com.robot_controller.databinding.FragmentAutonomyControllerBinding
import com.robot_controller.utils.bottomSheets.EnvironmentNameBottomSheet
import com.robot_controller.utils.enums.Angle
import com.robot_controller.utils.extensions.toBitmap

class AutonomyControllerFragment :
    Fragment(),
    EnvironmentNameBottomSheet.EnvironmentNameBottomSheetListener
{
    private lateinit var binding: FragmentAutonomyControllerBinding
    private lateinit var viewModel: MainViewModel

    private enum class GalleryFlow {
        MAP_ENVIRONMENT,
        LOCATE_ENVIRONMENT,
    }

    private var galleryFlow: GalleryFlow = GalleryFlow.MAP_ENVIRONMENT
    private var environmentName: String? = null

    private val pick4ImagesLauncher =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(Angle.entries.size)) { uris ->
            if (uris.isNullOrEmpty()) return@registerForActivityResult

            if (uris.size != Angle.entries.size) {
                Toast.makeText(requireContext(), "Selecione 4 fotos.", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            val bitmaps = uris.take(Angle.entries.size).map { it.toBitmap(requireContext()) }
            val (north, east, south, west) = bitmaps

            when (galleryFlow) {
                GalleryFlow.MAP_ENVIRONMENT -> {
                    if (environmentName.isNullOrBlank()) {
                        Toast.makeText(requireContext(), "Nome de ambiente inválido", Toast.LENGTH_SHORT).show()
                        return@registerForActivityResult
                    }

                    Toast.makeText(requireContext(), "Selecionadas: ${uris.size} fotos", Toast.LENGTH_SHORT).show()
                    viewModel.mapEnvironmentFromGallery(environmentName!!, north, east, south, west)
                }
                GalleryFlow.LOCATE_ENVIRONMENT -> {
                    Toast.makeText(requireContext(), "Processando localização...", Toast.LENGTH_SHORT).show()
                    viewModel.locateFromGallery(north, east, south, west)
                }
            }
        }

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

        FragmentAutonomyControllerBinding.inflate(layoutInflater, container, false).let {
            binding = it
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            mapEnvironmentButton.setOnClickListener {
                showEnvironmentNameBottomSheet()
            }

            whereAmIButton.setOnClickListener {
                galleryFlow = GalleryFlow.LOCATE_ENVIRONMENT
                openGalleryForSelectingImages()
            }
        }
    }

    //region Environment Name Bottom Sheet

    private fun showEnvironmentNameBottomSheet() {
        environmentName = null
        val bottomSheet = EnvironmentNameBottomSheet.newInstance()
        bottomSheet.show(childFragmentManager, EnvironmentNameBottomSheet.TAG)
    }

    override fun onEnvironmentNameSaved(environmentName: String) {
        galleryFlow = GalleryFlow.MAP_ENVIRONMENT
        this.environmentName = environmentName
        openGalleryForSelectingImages()
    }

    //endregion

    fun openGalleryForSelectingImages() {
        pick4ImagesLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}
