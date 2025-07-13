import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.medialibrary.api.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.medialibrary.models.Playlist
import com.example.playlistmaker.ui.medialibrary.view_model.AddPlaylistResultScreenState
import com.example.playlistmaker.ui.medialibrary.view_model.AddPlaylistScreenState
import com.example.playlistmaker.util.SingleEventLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AddPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _screenStateFlow =
        MutableStateFlow<AddPlaylistScreenState>(AddPlaylistScreenState.NotFound)
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val onCreateClickedLiveData = SingleEventLiveData<AddPlaylistResultScreenState>()
    fun observeOnCreateClickedLiveData(): LiveData<AddPlaylistResultScreenState> = onCreateClickedLiveData

    private val onBackClickedLiveData = SingleEventLiveData<Boolean>()
    fun observeOnBackClickedLiveData(): LiveData<Boolean> = onBackClickedLiveData

    private var coverUri: String = ""

    fun setCover(uri: String) {
        coverUri = uri
        _screenStateFlow.update { AddPlaylistScreenState.Found(uri) }
    }

    fun backClicked(title: String, description: String) {
        onBackClickedLiveData.value = title.isNotEmpty()
                || description.isNotEmpty()
                || coverUri != ""
    }

    fun createPlaylist(playlistTitle: String, playlistDescription: String, storagePath: File) {
        viewModelScope.launch {
            var isNeedCreate = true

            val checkJob = launch {
                playlistInteractor.checkPlaylistExistence(playlistTitle)
                    .collect { isPlaylistExists ->
                        if (isPlaylistExists) {
                            isNeedCreate = false
                            onCreateClickedLiveData.postValue(
                                AddPlaylistResultScreenState.AlreadyExists(
                                    playlistTitle
                                )
                            )
                        }
                    }
            }

            checkJob.join()

            launch {
                if (isNeedCreate) {
                    val filePath: File? = if (coverUri != "") {
                        File(storagePath, playlistTitle)
                    } else null
                    playlistInteractor.create(
                        Playlist(
                            title = playlistTitle,
                            description = playlistDescription,
                            coverPath = filePath?.toString() ?: "",
                            tracksIds = listOf(),
                            tracksCount = 0
                        )
                    )
                    onCreateClickedLiveData.postValue(
                        AddPlaylistResultScreenState.Created(
                            playlistTitle,
                            coverUri,
                            filePath
                        )
                    )
                }
            }
        }
    }
}