package com.divyanshu.musicplayerapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.divyanshu.musicplayerapp.databinding.ItemSongBinding
import com.divyanshu.musicplayerapp.fragment.MusicListFragmentDirections
import com.divyanshu.musicplayerapp.model.Song

class SongAdapter : RecyclerView.Adapter<SongAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.uri == newItem.uri
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemSongBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.apply {
            txtTitle.text = item.title
            "${position + 1}.".also { txtSno.text = it }
            txtArtist.text = item.artist
        }

        holder.itemView.setOnClickListener {
            val direction =
                MusicListFragmentDirections.actionMusicListFragmentToPlayMusicFragment(item)
            it.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}