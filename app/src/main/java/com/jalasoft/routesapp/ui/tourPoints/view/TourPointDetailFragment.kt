package com.jalasoft.routesapp.ui.tourPoints.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.gms.maps.model.LatLng
import com.jalasoft.routesapp.databinding.FragmentTourPointDetailBinding
import com.jalasoft.routesapp.util.helpers.Constants
import java.util.*

class TourPointDetailFragment : Fragment() {

    private var _binding: FragmentTourPointDetailBinding? = null
    private val binding get() = _binding!!
    private val args: TourPointDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTourPointDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTourPointName.text = args.tourPointPathData.name
        val currLang = Locale.getDefault().isO3Language
        if (currLang == Constants.CURRENT_SPANISH_LANGUAGE) {
            binding.tvTourPointCategory.text = args.tourPointPathData.category?.descriptionEsp?.capitalize(Locale.ROOT) ?: ""
        } else binding.tvTourPointCategory.text = args.tourPointPathData.category?.descriptionEng?.capitalize(Locale.ROOT) ?: ""

        binding.tvTourPointAddress.text = args.tourPointPathData.address

        binding.ivTourPoint.load(args.tourPointPathData.urlImage)

        binding.btnSetAsDestination.setOnClickListener {
            args.tourPointPathData.destination?.let {
                val direction = TourPointDetailFragmentDirections.actionTourPointDetailFragmentToHomeFragment(
                    preSelectDestCoords = LatLng(it.latitude, it.longitude),
                    preDestName = args.tourPointPathData.name
                )
                findNavController().navigate(direction)
            }
        }
    }
}
