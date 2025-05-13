package com.example.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.support.setOnClickListener {
            viewModel.sendEmail(
                getString(R.string.support_subject),
                getString(R.string.support_message),
                arrayOf(getString(R.string.support_email)))
        }

        binding.arrowForward.setOnClickListener {
            viewModel.openLink(getString(R.string.offer_uri))
        }

        binding.share.setOnClickListener {
            viewModel.shareLink(getString(R.string.curs_uri))
        }

        viewModel.getThemeLiveData().observe(viewLifecycleOwner) { isDarkTheme ->
            binding.smThemeSwitch.isChecked = isDarkTheme
        }

        binding.smThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }
    }
}