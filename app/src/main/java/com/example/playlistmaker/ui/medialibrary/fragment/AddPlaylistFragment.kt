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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream

class AddPlaylistFragment : BindingFragment<FragmentAddPlaylistBinding>() {

    private var isEdit: Boolean = false

    private val playlistId: Int by lazy(LazyThreadSafetyMode.NONE) {
        requireArguments().getInt(PLAYLIST_ID_KEY)
    }

    private val viewModel: AddPlaylistFragmentViewModel by lazy {
        getViewModel { parametersOf(playlistId) }
    }

    private var backDialog: MaterialAlertDialogBuilder? = null

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

        isEdit = playlistId > 0

        storagePath = getStoragePath()

        if (!isEdit)
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

        binding.etPlaylistTitle.doOnTextChanged { text, start, before, count ->
            setBorderColor(text, EDIT_TEXT_TITLE)
            setTextColor(text, EDIT_TEXT_TITLE)
            binding.btnCreatePlaylist.isEnabled = !text.isNullOrBlank()
        }

        binding.etPlaylistDescription.doOnTextChanged { text, start, before, count ->
            setBorderColor(text, EDIT_TEXT_DESCRIPTION)
            setTextColor(text, EDIT_TEXT_DESCRIPTION)
        }

        if (!isEdit) {
            binding.btnCreatePlaylist.setOnClickListener {
                viewModel.createPlaylist(
                    binding.etPlaylistTitle.text.toString(),
                    binding.etPlaylistDescription.text.toString(),
                    storagePath
                )
            }

            setCreateState()
        }
        else {
            binding.btnCreatePlaylist.setOnClickListener {
                viewModel.updatePlaylist(
                    binding.etPlaylistTitle.text.toString(),
                    binding.etPlaylistDescription.text.toString(),
                    storagePath
                )
            }

            setEditState()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenStateFlow.collect { state ->
                when (state) {
                    is AddPlaylistScreenState.NotFound -> setNotFoundScreenState()
                    is AddPlaylistScreenState.Found -> setCover(state.url)
                }
            }
        }

        viewModel.observeOnCreateClickedLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddPlaylistResultScreenState.Found -> {
                    binding.etPlaylistTitle.setText(state.playlistTitle)
                    binding.etPlaylistDescription.setText(state.description)

                    if (state.coverUri.isNotBlank())
                        setCover(state.coverUri)
                }

                is AddPlaylistResultScreenState.Created -> {
                    saveCoverToStorage(state.coverUri, state.filePath)
                    Snackbar.make(
                        view.rootView,
                        "${requireActivity().getString(R.string.toast_playlist)} ${state.playlistTitle} ${requireActivity().getString(R.string.toast_created)}",
                        Snackbar.LENGTH_LONG
                    ).show()

                    parentFragmentManager.popBackStack()
                }

                is AddPlaylistResultScreenState.Updated -> {
                    if (state.needUpdateCover)
                        overwriteCoverFile(state.coverUri, state.playlistTitle, state.oldTitle)
                    else
                        saveCoverToStorage(state.coverUri, state.filePath)

                    Snackbar.make(
                        view.rootView,
                        "${requireActivity().getString(R.string.toast_playlist)} ${state.playlistTitle} ${requireActivity().getString(R.string.toast_updated)}",
                        Snackbar.LENGTH_LONG
                    ).show()

                    parentFragmentManager.popBackStack()
                }

                is AddPlaylistResultScreenState.AlreadyExists -> {
                    Snackbar.make(
                        binding.btnCreatePlaylist,
                        "${requireActivity().getString(R.string.toast_playlist)} ${state.playlistTitle} ${requireActivity().getString(R.string.toast_already_exists)}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewModel.observeOnBackClickedLiveData().observe(viewLifecycleOwner) { isContentEntered ->
            if (isContentEntered)
                backDialog?.let { it.show() }
            else
                parentFragmentManager.popBackStack()
        }
    }

    private fun setCreateState() {
        binding.playlistTitle.setText(R.string.media_library_btn_new_playlist)
        binding.btnCreatePlaylist.setText(R.string.btn_create_playlist)
    }

    private fun setEditState() {
        binding.playlistTitle.setText(R.string.media_library_btn_edit_playlist)
        binding.btnCreatePlaylist.setText(R.string.btn_save_playlist)
    }

    private fun setNotFoundScreenState() {
        binding.frAddPlaylist.isEnabled = false
    }

    private fun setCover(uri: String) {
        Glide.with(requireContext())
            .load(uri)
            .placeholder(R.drawable.player_placeholder)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(8)))
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
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

        if (!s.isNullOrBlank()) {
            ContextCompat.getColorStateList(
                requireContext(),
                R.color.border_color_not_empty
            )?.let {
                editText?.setBoxStrokeColorStateList(it)
            }
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

        if (!s.isNullOrBlank()) {
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

    private fun overwriteCoverFile(newCoverUri: String, newTitle: String, oldTitle: String) {
        File(storagePath, oldTitle).delete()
        val inputStream = requireContext().contentResolver.openInputStream(newCoverUri.toUri())
        val outputStream = FileOutputStream(File(storagePath, newTitle))
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        inputStream?.close()
        outputStream.close()
    }

    companion object {
        private const val EDIT_TEXT_TITLE = 1
        private const val EDIT_TEXT_DESCRIPTION = 2
        private const val SCHEME = "package"
        private const val PLAYLIST_ID_KEY = "PLAYLIST_ID_KEY"

        fun newInstance(playlistId: Int): AddPlaylistFragment = AddPlaylistFragment().apply {
            arguments = bundleOf(PLAYLIST_ID_KEY to playlistId)
        }

        fun setArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST_ID_KEY to playlistId)
    }
}