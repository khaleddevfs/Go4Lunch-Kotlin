package com.example.go4lunch24kotlin.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.go4lunch24kotlin.adapters.RestaurantsAdapter
import com.example.go4lunch24kotlin.databinding.FragmentListRestBinding
import com.example.go4lunch24kotlin.factory.Go4LunchFactory
import com.example.go4lunch24kotlin.models.RestaurantsWrapperViewState
import com.example.go4lunch24kotlin.ui.RestaurantDetailsActivity
import com.example.go4lunch24kotlin.viewModel.RestaurantsViewModel


class RestaurantsFragment: Fragment() {

    private var _binding: FragmentListRestBinding? = null
    private val binding get() = _binding!!

    //private val viewModel by viewModels<RestaurantsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("RestaurantsFragment", "onCreateView: Starting.")

        _binding = FragmentListRestBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  Log.d("RestaurantsFragment", "Data received: ${RestaurantsWrapperViewState.restaurantList.size} restaurants")

        Log.d("RestaurantsFragment", "onViewCreated: View created.")



        // INIT RESTAURANT VIEWMODEL
        val viewModelFactory = Go4LunchFactory.instance
        val restaurantsViewModel = ViewModelProvider(this, viewModelFactory!!)[RestaurantsViewModel::class.java]
        Log.d("RestaurantsFragment", "ViewModel initialized.")

        val adapter = RestaurantsAdapter { restaurantId ->
            Log.d("RestaurantsFragment", "Navigating to restaurant details for ID: $restaurantId")

            startActivity(Intent(RestaurantDetailsActivity.navigate(requireContext(), restaurantId)))
        }

        binding.restaurantRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.restaurantRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.restaurantRecyclerView.adapter = adapter

        restaurantsViewModel.getRestaurantsWrapperViewStateMediatorLiveData.observe(viewLifecycleOwner){ restaurantsWrapperViewState ->
            Log.d("RestaurantsFragment", "Restaurant list updated.")

            adapter.submitList(restaurantsWrapperViewState.itemRestaurant)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("RestaurantsFragment", "onDestroyView: Cleaning up.")

        _binding = null
    }
}