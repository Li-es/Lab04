package edu.msudenver.cs3013.lab04

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import edu.msudenver.cs3013.lab04.LocationViewModel
import edu.msudenver.cs3013.lab04.databinding.FragmentDetailBinding
import edu.msudenver.cs3013.lab04.databinding.FragmentMapsBinding

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    private val locationViewModel: LocationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationViewModel.savedLocation.observe(viewLifecycleOwner, Observer { location ->
            binding.textViewLocation.text = getString(R.string.location_text, location.latitude, location.longitude)
        })
    }
}