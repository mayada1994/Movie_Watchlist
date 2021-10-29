package com.mayada1994.moviewatchlist_mvvm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.databinding.ItemMovieDetailedBinding
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.squareup.picasso.Picasso

class MoviesAdapter(
    private val items: List<Movie>,
    private val listener: OnMovieClickListener
) : RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val itemBinding =
            ItemMovieDetailedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoviesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MoviesViewHolder(private val itemBinding: ItemMovieDetailedBinding) :
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
                txtDescription.text = item.overview
                root.setOnClickListener { listener.onClick(item) }
            }
        }
    }

    interface OnMovieClickListener {
        fun onClick(movie: Movie)
    }

}