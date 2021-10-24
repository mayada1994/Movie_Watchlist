package com.mayada1994.moviewatchlist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mayada1994.moviewatchlist.databinding.FragmentMoviesBinding

class MoviesFragment : Fragment() {

    private lateinit var binding: FragmentMoviesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

}