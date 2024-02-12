package com.divyanshu.musicplayerapp.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.divyanshu.musicplayerapp.R
import com.divyanshu.musicplayerapp.adapter.SongAdapter
import com.divyanshu.musicplayerapp.databinding.FragmentMusicListBinding
import com.divyanshu.musicplayerapp.helper.Constants.toMusicDuration
import com.divyanshu.musicplayerapp.model.Song

class MusicListFragment : Fragment(R.layout.fragment_music_list) {

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!
    private var isWritePermissionGranted = false
    private var isReadPermissionGranted = false
    private var isReadAudioPermissionGranted = false
    private var requiredPermissionsList = mutableListOf<String>()
    private var songsList = mutableListOf<Song>()
    private lateinit var adapter: SongAdapter
    private var permissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                isWritePermissionGranted = it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
                loadSongs()
            }
            if (it[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                isReadPermissionGranted = it[Manifest.permission.READ_EXTERNAL_STORAGE] == true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (it[Manifest.permission.READ_MEDIA_AUDIO] == true) {
                    isReadPermissionGranted = it[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                    loadSongs()
                }
            }
            return@registerForActivityResult
        }


    private fun loadSongs() {
        //fetch the audio files from storage
        val contentResolver = activity?.contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contentResolver?.query(uri, null, null, null, null)
        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title: String =
                    cursor.getString(getColumnIndex(cursor, MediaStore.Audio.Media.TITLE))
                val artist: String =
                    cursor.getString(getColumnIndex(cursor, MediaStore.Audio.Media.ARTIST))
                val duration: String =
                    cursor.getString(getColumnIndex(cursor, MediaStore.Audio.Media.DURATION))
                val url: String =
                    cursor.getString(getColumnIndex(cursor, MediaStore.Audio.Media.DATA))


                songsList.add(
                    Song(
                        title = title,
                        uri = url,
                        artist = artist,
                        duration = duration.toLong().toMusicDuration()
                    )
                )
            } while (cursor.moveToNext())
            cursor.close()
        }
    }

    private fun getColumnIndex(cursor: Cursor, type: String): Int {
        return cursor.getColumnIndex(type)
    }

    private fun checkSelfPermission(): Boolean {
        isWritePermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isReadAudioPermissionGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (!isWritePermissionGranted) {
            requiredPermissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!isReadPermissionGranted) {
            requiredPermissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!isReadAudioPermissionGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requiredPermissionsList.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) isReadAudioPermissionGranted else (isWritePermissionGranted && isReadPermissionGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        songsList.clear()
        if (!checkSelfPermission()) {
            permissionResultLauncher.launch(requiredPermissionsList.toTypedArray())
        } else {
            loadSongs()
        }
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = SongAdapter()
        binding.rlvMusicList.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = this@MusicListFragment.adapter
        }
        adapter.differ.submitList(songsList)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}