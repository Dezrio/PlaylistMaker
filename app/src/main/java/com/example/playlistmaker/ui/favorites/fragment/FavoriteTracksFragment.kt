package com.example.playlistmaker.ui.favorites.fragment

class FavoriteTracksFragment  : BindingFragment<FragmentFavoritesBinding>() {

    private val viewModel: FavoritesViewModel by viewModel<FavoritesViewModel>()

    private lateinit var trackAdapter: TrackAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter { onTrackClicked(it) }
        binding.rvTrackList.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.rvTrackList.adapter = trackAdapter

        viewModel.observeScreenState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesScreenState.Loading -> showLoading()
                is FavoritesScreenState.Empty -> showEmpty()
                is FavoritesScreenState.Content -> showContent(state.tracks)
            }
        }

        viewModel.observeOnTrackClickedLiveData().observe(viewLifecycleOwner) { track ->
            val action = LibraryFragmentDirections.actionLibraryFragmentToAudioPlayerActivity(
                track
            )
            parentFragment?.findNavController()?.navigate(action)
        }
    }

    private fun onTrackClicked(track: Track) {
        viewModel.onTrackClicked(track)
    }

    private fun showLoading() {
        binding.rvTrackList.isVisible = false
        binding.groupEmpty.isVisible = false

        binding.progressBarFavorites.isVisible = true
    }

    private fun showEmpty() {
        binding.rvTrackList.isVisible = false
        binding.progressBarFavorites.isVisible = false

        val emptyImageId: Int = getEmptyImageIdAccordingTheme(
            R.drawable.ic_placeholder_nothing_found_lm_120,
            R.drawable.ic_placeholder_nothing_found_dm_120
        )

        binding.ivErrorImage.setImageResource(emptyImageId)
        binding.tvErrorMessage.text =
            requireActivity().getString(R.string.empty_favorite_tracks_message)

        binding.groupEmpty.isVisible = true
    }

    private fun getEmptyImageIdAccordingTheme(imageIdLightMode: Int, imageIdDarkMode: Int): Int {
        return when (requireActivity().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> imageIdDarkMode

            Configuration.UI_MODE_NIGHT_NO -> imageIdLightMode

            else -> imageIdDarkMode
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.groupEmpty.isVisible = false
        binding.progressBarFavorites.isVisible = false

        trackAdapter.updateTracks(tracks)
        binding.rvTrackList.isVisible = true
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateFavoriteTracks()
    }

    override fun onDestroyView() {
        binding.rvTrackList.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}