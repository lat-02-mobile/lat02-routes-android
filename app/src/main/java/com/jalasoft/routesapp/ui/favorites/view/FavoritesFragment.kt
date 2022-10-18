package com.jalasoft.routesapp.ui.favorites.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.local.FavoriteDestinationEntity
import com.jalasoft.routesapp.databinding.FragmentFavoritesBinding
import com.jalasoft.routesapp.ui.favorites.adapters.FavoriteAdapter
import com.jalasoft.routesapp.ui.favorites.viewModel.FavoritesViewModel
import com.jalasoft.routesapp.util.SwipeGesture
import dagger.hilt.android.AndroidEntryPoint

enum class ActionType {
    EDIT, REMOVE
}

@AndroidEntryPoint
class FavoritesFragment : Fragment(), FavoriteAdapter.IFavoriteListener {
    private var _binding: FragmentFavoritesBinding? = null
    val viewModel: FavoritesViewModel by viewModels()
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecycler()

        viewModel.favoritesList.observe(viewLifecycleOwner) {
            (binding.recyclerFavorites.adapter as FavoriteAdapter).updateList(it.toMutableList())
            if (it.isEmpty()) {
                binding.tvNoFavorites.visibility = View.VISIBLE
            } else {
                binding.tvNoFavorites.visibility = View.GONE
            }
        }

        binding.svFavoritesSearcher.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.filterFavorites(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterFavorites(newText.toString())
                return true
            }
        })

        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                binding.recyclerFavorites.adapter?.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                val selFav = (binding.recyclerFavorites.adapter as FavoriteAdapter).favoritesList[viewHolder.absoluteAdapterPosition]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        showDialog(selFav, viewHolder.absoluteAdapterPosition, ActionType.REMOVE)
                    }

                    ItemTouchHelper.RIGHT -> {
                        showDialog(selFav, viewHolder.absoluteAdapterPosition, ActionType.EDIT)
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.recyclerFavorites)

        viewModel.getFavoriteDestinationsByCityAndUserId(requireContext())
    }

    private fun showDialog(targetFav: FavoriteDestinationEntity, index: Int, actionType: ActionType) {
        val builder = AlertDialog.Builder(binding.root.context)

        when (actionType) {
            ActionType.EDIT -> {
                builder.setTitle(R.string.edit_favorite_destination)
                builder.setMessage(requireContext().getString(R.string.set_a_new_name_for_this_fav_dest, targetFav.name))

                val textInputLayout = TextInputLayout(requireContext())
                textInputLayout.setPadding(80, 0, 80, 0)
                val input = EditText(requireContext())
                input.setBackgroundResource(android.R.color.transparent)
                textInputLayout.hint = requireContext().getString(R.string.new_name)

                textInputLayout.addView(input)
                textInputLayout.editText?.setPaddingRelative(10, 70, 10, 20)
                builder.setView(textInputLayout)

                builder.setPositiveButton(R.string.save) { _, _ ->
                    val name = input.text.toString()
                    viewModel.updateFavoriteDestination(name, targetFav)
                    binding.recyclerFavorites.adapter?.notifyItemChanged(index)
                }
            }

            ActionType.REMOVE -> {
                builder.setTitle(R.string.remove_fav_dest)
                builder.setMessage(requireContext().getString(R.string.are_you_sure_you_want_to_remove_this_fav_dest, targetFav.name))
                builder.setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.deleteFavoriteDestination(targetFav, index)
                    binding.recyclerFavorites.adapter?.notifyItemRemoved(index)
                }
            }
        }

        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun setRecycler() {
        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavorites.adapter = FavoriteAdapter(mutableListOf(), this)
    }

    override fun onFavoriteTap(fav: FavoriteDestinationEntity) {
        val direction = FavoritesFragmentDirections.actionFavoritesFragmentToHomeFragment(
            preSelectDestCoords = LatLng(fav.destination.latitude, fav.destination.longitude),
            preDestName = fav.name
        )
        findNavController().navigate(direction)
    }
}
