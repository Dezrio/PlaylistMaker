package com.example.playlistmaker.ui.medialibrary.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.ui.medialibrary.view_model.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.ui.medialibrary.view_model.FavouriteTracksScreenState
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment : BindingFragment<FragmentFavouriteTracksBinding>() {

    private val viewModel: FavouriteTracksFragmentViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouriteTracksBinding {
        return FragmentFavouriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.screenStateObserve().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavouriteTracksScreenState.Default -> setDefaultState()
                is FavouriteTracksScreenState.NotFound -> setNotFoundState()
            }
        }
    }

    private fun setNotFoundState() {
        binding.tvNotFoundText.text = requireActivity().getString(R.string.media_library_empty_favourite_tracks_text)
        binding.groupEmpty.isVisible = true
    }

    private fun setDefaultState(){
        binding.groupEmpty.isVisible = false
    }

    companion object{
        fun newInstance() : FavouriteTracksFragment = FavouriteTracksFragment()
    }
}