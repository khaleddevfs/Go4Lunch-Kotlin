package com.example.go4lunch24kotlin.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.go4lunch24kotlin.adapters.WorkMatesRecyclerViewAdapter
import com.example.go4lunch24kotlin.databinding.FragmentWorkmatesBinding
import com.example.go4lunch24kotlin.factory.Go4LunchFactory
import com.example.go4lunch24kotlin.viewModel.WorkMatesViewModel

class WorkMatesFragment : Fragment() {

    private var _binding: FragmentWorkmatesBinding? = null
    private val binding get() = _binding!!
    private val logTag = "WorkMatesFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Log.d(logTag, "onCreateView: Inflating layout for WorkMatesFragment.")
        _binding = FragmentWorkmatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(logTag, "onViewCreated: WorkMatesFragment view created.")

        val viewModelFactory = Go4LunchFactory.instance
        val workMatesViewModel =
            ViewModelProvider(this, viewModelFactory!!)[WorkMatesViewModel::class.java]
        Log.d(logTag, "ViewModel initialized.")

        // CONFIGURE RECYCLERVIEW
        val adapter = WorkMatesRecyclerViewAdapter {
            // Ici, vous pouvez gérer les clics sur les éléments de la liste si nécessaire.
            Log.d(logTag, "RecyclerView item clicked.")
        }

        binding.workMatesRecyclerView.adapter = adapter
        binding.workMatesRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.workMatesRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        workMatesViewModel.workMatesViewStateMediatorLiveData.observe(viewLifecycleOwner) { workmatesList ->
            Log.d(logTag, "Updating workmates list in adapter.")
            adapter.submitList(workmatesList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(logTag, "onDestroyView: Cleaning up WorkMatesFragment.")
        _binding = null
    }
}
