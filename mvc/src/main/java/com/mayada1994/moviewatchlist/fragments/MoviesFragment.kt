package com.mayada1994.moviewatchlist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mayada1994.moviewatchlist.databinding.FragmentMoviesBinding

class MoviesFragment : Fragment() {

    enum class MovieType {
        POPULAR,
        UPCOMING
    }

    private lateinit var binding: FragmentMoviesBinding

    private var movieType: MovieType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString(MOVIE_TYPE)?.let {
            movieType = MovieType.valueOf(it)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        const val MOVIE_TYPE = "MOVIE_TYPE"

        @JvmStatic
        fun newInstance(movieType: MovieType) =
            MoviesFragment().apply {
                arguments = Bundle().apply {
                    putString(MOVIE_TYPE, movieType.name)
                }
            }
    }

}