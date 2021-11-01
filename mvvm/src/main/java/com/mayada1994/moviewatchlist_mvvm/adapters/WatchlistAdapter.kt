package com.mayada1994.moviewatchlist_mvvm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.databinding.ItemMovieWhitelistBinding
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.squareup.picasso.Picasso

class WatchlistAdapter(
    private val items: ArrayList<Movie>,
    private val listener: OnWatchlistItemSelectListener
) : RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistViewHolder {
        val itemBinding =
            ItemMovieWhitelistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WatchlistViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(selectedItems: List<Movie>) {
        selectedItems.forEach { item ->
            notifyItemRemoved(items.indexOf(item))
            items.remove(item)
        }
        listener.checkMoviesList(items)
    }

    inner class WatchlistViewHolder(private val itemBinding: ItemMovieWhitelistBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(item: Movie) {
            with(itemBinding) {
                if (item.posterPath.isNullOrBlank()) {
                    imgPoster.setImageResource(R.drawable.ic_poster_placeholder)
                } else {
                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500/${item.posterPath}")
                        .into(imgPoster)
                }
                txtTitle.text = item.getMovieTitle()
                txtReleaseDate.text = item.getReleaseYearFromDate()
                checkbox.setOnCheckedChangeListener { _, checked ->
                    listener.onItemSelect(item, checked)
                }
            }
        }
    }

    interface OnWatchlistItemSelectListener {
        fun onItemSelect(item: Movie, checked: Boolean)
        fun checkMoviesList(movies: List<Movie>)
    }

}