package com.neko.hiepdph.dogtranslatorlofi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    private var _player: Player? = null
    private var playerListener: Player.Listener? = null
    private var playerListener1: Player.Listener? = null
     var currentType = 0
    fun setupPlayer(player: Player) {
        if (_player == null) {
            _player = player

        }
    }


    fun playAudio(mediaItem: MediaItem, onEnd: () -> Unit) {

        try {
            playerListener1?.let { _player?.removeListener(it) }
            playerListener?.let { _player?.removeListener(it) }
            _player?.stop()

            playerListener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    Log.d("TAG", "onPlaybackStateChanged: " + playbackState)
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                        onEnd()
                    }
                }
            }
            _player?.repeatMode = Player.REPEAT_MODE_OFF
            _player?.addListener(playerListener!!)
            _player?.clearMediaItems()
            _player?.setMediaItem(mediaItem)
            _player?.prepare()
            _player?.playWhenReady = true

        } catch (e: Exception) {
        }

    }


    fun playAudio(
        mediaItem: MutableList<MediaItem>, timeLimit: Long, onEnd: () -> Unit
    ) {
        try {
            playerListener1?.let { _player?.removeListener(it) }
            playerListener?.let { _player?.removeListener(it) }
            _player?.stop()

            playerListener1 = object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        viewModelScope.launch {
                            delay(timeLimit)
                            _player?.let {
                                it.stop()
                                onEnd()
                            }
                        }
                    }
                }
            }
            _player?.addListener(playerListener1!!)
            _player?.clearMediaItems()
            _player?.setMediaItems(mediaItem)
            _player?.repeatMode = Player.REPEAT_MODE_ALL
            _player?.prepare()
            _player?.playWhenReady = true

        } catch (e: Exception) {
        }

    }

    fun resetPlayer() {
        playerListener?.let { _player?.removeListener(it) }
        playerListener1?.let { _player?.removeListener(it) }
        if (_player?.isPlaying == true || _player?.isLoading == true) {
            _player?.stop()
        }
        _player?.clearMediaItems()

    }

    fun pausePlayer() {
        _player?.pause()
        playerListener?.let { _player?.removeListener(it) }
        playerListener1?.let { _player?.removeListener(it) }
    }

    fun resetAll() {
        if (_player?.isPlaying == true || _player?.isLoading == true) {
            _player?.stop()
        }
        _player?.apply {
            playWhenReady = false
            playbackState
        }
        _player?.release()
        _player = null

    }
}