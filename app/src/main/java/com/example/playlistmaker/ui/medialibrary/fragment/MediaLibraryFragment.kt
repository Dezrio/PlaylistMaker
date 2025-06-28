package com.example.playlistmaker.ui.medialibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.example.playlistmaker.ui.medialibrary.adapter.MediaLibraryViewPagerAdapter
import com.example.playlistmaker.util.BindingFragment
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryFragment : BindingFragment<FragmentMediaLibraryBinding>() {
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaLibraryBinding {
        return FragmentMediaLibraryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPagerLibrary.adapter = MediaLibraryViewPagerAdapter(childFragmentManager, lifecycle)

        tabLayoutMediator = TabLayoutMediator(binding.tabLibrary, binding.viewPagerLibrary){tab, position ->
            when (position){
                0 -> tab.text = getString(R.string.media_library_tab_favourite_tracks)
                1 -> tab.text = getString(R.string.media_library_tab_playlists)
            }
        }
        tabLayoutMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }
}