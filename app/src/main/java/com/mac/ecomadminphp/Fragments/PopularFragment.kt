package com.mac.ecomadminphp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mac.ecomadminphp.R
import com.mac.ecomadminphp.databinding.FragmentHomeBinding
import com.mac.ecomadminphp.databinding.FragmentPopularBinding

class PopularFragment : Fragment() {

    private lateinit var _binding: FragmentPopularBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularBinding.inflate(layoutInflater)



        return binding.root
    }

}