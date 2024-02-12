package com.divyanshu.musicplayerapp.fragment

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.divyanshu.musicplayerapp.R
import com.divyanshu.musicplayerapp.databinding.FragmentPlayMusicBinding
import com.divyanshu.musicplayerapp.helper.Constants
import com.divyanshu.musicplayerapp.helper.Constants.toMusicDuration
import com.divyanshu.musicplayerapp.model.Song

class PlayMusicFragment : Fragment(R.layout.fragment_play_music) {
    private var _binding: FragmentPlayMusicBinding? = null
    private val binding get() = _binding!!
    private val args: PlayMusicFragmentArgs by navArgs()
    private lateinit var song: Song
    private lateinit var handler: Handler
    private var seekBarRunnable = Runnable { updateSeekBar() }
    private val mediaPlayer by lazy {
        MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        song = args.song!!
        mediaPlayer.apply {
            setDataSource(song.uri)
            mediaPlayer.prepare()
            mediaPlayer.setVolume(100.0F, 100.0F)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DEBUG", song.duration)
        binding.apply {
            txtMediaTitle.text = song.title
            "00:00".also { txtStartTime.text = it }
            txtEndTime.text = song.duration
            imgPlay.setOnClickListener {
                playSong()
            }
            imgPrev.setOnClickListener {
                seekTo5SecBackward()
            }
            imgFwd.setOnClickListener {
                seekTo5SecForward()
            }
            imgLoop.setOnClickListener {
                loopMediaPlayer()
            }
            loadAlbumArt()
            playSong()
        }
    }

    private fun loadAlbumArt() {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(song.uri)
        val data = mediaMetadataRetriever.embeddedPicture
        data?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, data.size)
            binding.imgAlbumArt.setImageBitmap(bitmap)

        }
    }

    private fun loopMediaPlayer() {
        mediaPlayer.let {
            if (it.isLooping) {
                it.isLooping = false
                binding.imgLoop.setImageResource(R.drawable.repeat)
            } else {
                it.isLooping = true
                binding.imgLoop.setImageResource(R.drawable.repeat_activated)
            }
        }
    }

    private fun seekTo5SecForward() {
        mediaPlayer.let {
            if (it.currentPosition + Constants.seekForwardTime <= it.duration) {
                mediaPlayer.seekTo(it.currentPosition + Constants.seekForwardTime)
            } else {
                mediaPlayer.seekTo(it.duration)
            }
        }
    }


    private fun seekTo5SecBackward() {
        mediaPlayer.let {
            if (it.currentPosition - Constants.seekForwardTime > 0) {
                mediaPlayer.seekTo(it.currentPosition - Constants.seekForwardTime)
            } else {
                mediaPlayer.seekTo(it.duration - it.duration)
            }
        }
    }

    private fun playSong() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            binding.imgPlay.setImageResource(R.drawable.pause)
            updateSeekBar()
        } else {
            mediaPlayer.pause()
            binding.imgPlay.setImageResource(R.drawable.play)
        }
    }

    private fun updateSeekBar() {
        mediaPlayer.let {
            binding.txtStartTime.text = it.currentPosition.toLong().toMusicDuration()
        }
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(seekBarRunnable, 20)
        seekbarSetup()
    }

    private fun seekbarSetup() {
        mediaPlayer.also {
            binding.seekBar.progress = it.currentPosition
            binding.seekBar.max = it.duration
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                    binding.txtStartTime.text = progress.toLong().toMusicDuration()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.seekTo(it.progress)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        handler.removeCallbacks(seekBarRunnable)
    }
}