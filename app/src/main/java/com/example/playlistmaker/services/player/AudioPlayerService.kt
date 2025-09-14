package com.example.playlistmaker.services.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.player.models.AudioPlayerData
import com.example.playlistmaker.domain.player.models.AudioPlayerState
import com.example.playlistmaker.ui.App.Companion.AUDIO_PLAYER_INTENT_TRACK_ARTIST_NAME
import com.example.playlistmaker.ui.App.Companion.AUDIO_PLAYER_INTENT_TRACK_URL
import com.example.playlistmaker.ui.App.Companion.AUDIO_PLAYER_INTENT_TRACK_TITLE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerService : Service(), AudioPlayerControl, LifecycleEventObserver {
    private val binder = AudioPlayerServiceBinder()

    private var trackUrl: String = EMPTY_STRING
    private var trackArtistName: String = EMPTY_STRING
    private var trackTitle: String = EMPTY_STRING

    private var mediaPlayer: MediaPlayer? = null

    private val _audioPlayerData = MutableStateFlow(AudioPlayerData(AudioPlayerState.STATE_DEFAULT, DEFAULT_CUR_POSITION_STRING))
    private val audioPlayerData = _audioPlayerData.asStateFlow()
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onBind(intent: Intent?): IBinder {
        trackUrl = intent?.getStringExtra(AUDIO_PLAYER_INTENT_TRACK_URL) ?: EMPTY_STRING
        trackArtistName =
            intent?.getStringExtra(AUDIO_PLAYER_INTENT_TRACK_ARTIST_NAME) ?: EMPTY_STRING
        trackTitle = intent?.getStringExtra(AUDIO_PLAYER_INTENT_TRACK_TITLE) ?: EMPTY_STRING

        initMediaPlayer()

        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    private fun initMediaPlayer() {
        if (trackUrl.isEmpty()) return

        mediaPlayer?.setDataSource(trackUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _audioPlayerData.value = AudioPlayerData(AudioPlayerState.STATE_PREPARED, DEFAULT_CUR_POSITION_STRING)
        }
        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            stopForeground()
            _audioPlayerData.value = AudioPlayerData(AudioPlayerState.STATE_PREPARED, DEFAULT_CUR_POSITION_STRING)
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _audioPlayerData.value = AudioPlayerData(AudioPlayerState.STATE_PLAYING, getCurrentPlayerPosition())
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _audioPlayerData.value = AudioPlayerData(AudioPlayerState.STATE_PAUSED, getCurrentPlayerPosition())
    }

    private fun releasePlayer() {
        mediaPlayer?.stop()
        timerJob?.cancel()
        _audioPlayerData.value = AudioPlayerData(AudioPlayerState.STATE_DEFAULT, DEFAULT_CUR_POSITION_STRING)
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(SET_CURRENT_TRACK_TIME_DELAY_MILLIS)
                _audioPlayerData.value =AudioPlayerData(AudioPlayerState.STATE_PLAYING, getCurrentPlayerPosition())
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(TRACK_TIME_PATTERN, Locale.getDefault())
            .format(mediaPlayer?.currentPosition) ?: DEFAULT_CUR_POSITION_STRING
    }

    override fun getAudioPlayerData(): StateFlow<AudioPlayerData> {
        return audioPlayerData
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.audio_player_notification_channel_description)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$trackArtistName $trackTitle")
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    override fun startForeground() {
        if (checkPermissions().not()) {
            stopSelf()
            return
        }
    }

    override fun stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val res = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            return res == PackageManager.PERMISSION_GRANTED
        } else return true

    }

    inner class AudioPlayerServiceBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    private companion object {
        const val EMPTY_STRING = ""
        const val TRACK_TIME_PATTERN = "mm:ss"
        const val SET_CURRENT_TRACK_TIME_DELAY_MILLIS = 300L
        const val DEFAULT_CUR_POSITION_STRING = "00:00"
        const val NOTIFICATION_CHANNEL_ID = "audio_player_service_channel"
        const val NOTIFICATION_CHANNEL_NAME = "audio_player_service"
        const val SERVICE_NOTIFICATION_ID = 108
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                stopForeground()
            }
            Lifecycle.Event.ON_STOP -> {
                ServiceCompat.startForeground(
                    this,
                    SERVICE_NOTIFICATION_ID,
                    createServiceNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            }
            else -> {}
        }
    }
}