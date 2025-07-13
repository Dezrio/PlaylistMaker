package com.example.playlistmaker.ui.medialibrary.fragment

import AddPlaylistFragmentViewModel
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.example.playlistmaker.ui.App.Companion.EXTERNAL_STORAGE_NAME
import com.example.playlistmaker.ui.medialibrary.view_model.AddPlaylistResultScreenState
import com.example.playlistmaker.ui.medialibrary.view_model.AddPlaylistScreenState
import com.example.playlistmaker.util.BindingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class AddPlaylistFragment : BindingFragment<FragmentAddPlaylistBinding>() {

    private val viewModel: AddPlaylistFragmentViewModel by viewModel()

    private lateinit var backDialog: MaterialAlertDialogBuilder

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.backClicked(
                binding.etPlaylistTitle.text.toString(),
                binding.etPlaylistDescription.text.toString()
            )
        }
    }

    private lateinit var permissionDialog: MaterialAlertDialogBuilder

    private val requester = PermissionRequester.instance()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.setCover(uri.toString())
            }
        }

    private lateinit var storagePath: File

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddPlaylistBinding {
        return FragmentAddPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storagePath = getStoragePath()

        backDialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setTitle(requireContext().getString(R.string.back_title))
            .setMessage(requireContext().getString(R.string.back_message))
            .setNeutralButton(requireContext().getString(R.string.back_cancel)) { dialog, which -> }
            .setPositiveButton(requireContext().getString(R.string.back_ok)) { dialog, which ->
                parentFragmentManager.popBackStack()
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        binding.playerHeader.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        permissionDialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogStyle)
            .setTitle(requireContext().getString(R.string.permission_title))
            .setMessage(requireContext().getString(R.string.permission_message))
            .setNeutralButton(requireContext().getString(R.string.permission_cancel)) { dialog, which -> }
            .setPositiveButton(requireContext().getString(R.string.permission_ok)) { dialog, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data =
                    Uri.fromParts(SCHEME, requireContext().packageName, null)
                requireContext().startActivity(intent)
            }

        binding.ivAddCover.setOnClickListener {
            openExternalStorage()
        }

        val titleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setBorderColor(s, EDIT_TEXT_TITLE)

                setTextColor(s, EDIT_TEXT_TITLE)

                binding.btnCreatePlaylist.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etPlaylistTitle.addTextChangedListener(titleTextWatcher)

        val descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setBorderColor(s, EDIT_TEXT_DESCRIPTION)

                setTextColor(s, EDIT_TEXT_DESCRIPTION)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etPlaylistDescription.addTextChangedListener(descriptionTextWatcher)

        binding.btnCreatePlaylist.setOnClickListener {
            viewModel.createPlaylist(
                binding.etPlaylistTitle.text.toString(),
                binding.etPlaylistDescription.text.toString(),
                storagePath
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenStateFlow.collect { state ->
                when (state) {
                    is AddPlaylistScreenState.NotFound -> setNotFoundScreenState()
                    is AddPlaylistScreenState.Found -> setFoundScreenState(state.url)
                }
            }
        }

        viewModel.observeOnCreateClickedLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddPlaylistResultScreenState.Created -> {
                    saveCoverToStorage(state.coverUri, state.filePath)
                    Snackbar.make(
                        binding.btnCreatePlaylist,
                        "${requireActivity().getString(R.string.toast_playlist)} " +
                                "${state.playlistTitle} ${requireActivity().getString(R.string.toast_created)}",
                        Snackbar.LENGTH_LONG
                    ).show()

                    parentFragmentManager.popBackStack()
                }

                is AddPlaylistResultScreenState.AlreadyExists -> {
                    Snackbar.make(
                        binding.btnCreatePlaylist,
                        "${requireActivity().getString(R.string.toast_playlist)} " +
                                "${state.playlistTitle} ${requireActivity().getString(R.string.toast_already_exists)}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }

        viewModel.observeOnBackClickedLiveData().observe(viewLifecycleOwner) { isContentEntered ->
            if (isContentEntered) backDialog.show() else parentFragmentManager.popBackStack()
        }
    }

    private fun setNotFoundScreenState() {
        binding.frAddPlaylist.isEnabled = false
    }

    private fun setFoundScreenState(uri: String) {
        Glide.with(requireContext())
            .load(uri)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(8)))
            .into(binding.ivAddCover)
    }

    private fun openExternalStorage() {
        viewLifecycleOwner.lifecycleScope.launch {
            requester.request(getManifestPermission())
                .collect { result ->
                    when (result) {
                        is PermissionResult.Granted -> {
                            pickMedia.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }

                        is PermissionResult.Denied.DeniedPermanently -> {
                            permissionDialog.show()
                        }

                        is PermissionResult.Denied, PermissionResult.Cancelled -> {}

                    }
                }
        }
    }

    private fun getManifestPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private fun setBorderColor(s: CharSequence?, typeEditText: Int) {
        var editText: TextInputLayout? = when (typeEditText) {
            EDIT_TEXT_TITLE -> binding.tilPlaylistTitle
            EDIT_TEXT_DESCRIPTION -> binding.tilPlaylistDescription
            else -> return
        }

        if (!s.isNullOrEmpty()) {
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.border_color_not_empty
            )?.let { editText?.setBoxStrokeColorStateList(it) }
        } else {
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.border_color_empty
            )?.let { editText?.setBoxStrokeColorStateList(it) }
        }
        editText = null
    }

    private fun setTextColor(s: CharSequence?, typeEditText: Int) {
        var editText: TextInputLayout? = when (typeEditText) {
            EDIT_TEXT_TITLE -> binding.tilPlaylistTitle
            EDIT_TEXT_DESCRIPTION -> binding.tilPlaylistDescription
            else -> return
        }

        if (!s.isNullOrEmpty()) {
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.text_color_not_empty
            )?.let { editText?.defaultHintTextColor = it }
        } else {
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.text_color_empty
            )?.let { editText?.defaultHintTextColor = it }
        }

        editText = null
    }

    private fun getStoragePath(): File {
        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            EXTERNAL_STORAGE_NAME
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        return filePath
    }

    private fun saveCoverToStorage(coverUri: String, targetFile: File?) {
        if (coverUri == "" || targetFile == null) return

        val inputStream = requireContext().contentResolver.openInputStream(coverUri.toUri())
        val outputStream = FileOutputStream(targetFile)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    companion object {
        private const val EDIT_TEXT_TITLE = 1
        private const val EDIT_TEXT_DESCRIPTION = 2
        private const val SCHEME = "package"
    }
}