package com.example.go4lunch24kotlin.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.adapters.RestaurantDetailsAdapter
import com.example.go4lunch24kotlin.databinding.ActivityRestaurantDetailBinding
import com.example.go4lunch24kotlin.factory.Go4LunchFactory
import com.example.go4lunch24kotlin.models.RestaurantDetailsViewState
import com.example.go4lunch24kotlin.viewModel.RestaurantDetailsViewModel

class RestaurantDetailsActivity  : AppCompatActivity()  {

    private lateinit var binding: ActivityRestaurantDetailBinding
    private lateinit var viewModel: RestaurantDetailsViewModel

    companion object {
        const val RESTAURANT_ID = "RESTAURANT_ID"
        fun navigate(context: Context, placeId: String): Intent {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra(RESTAURANT_ID, placeId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageViewModel()
        managerAdapter()
        manageView()
    }

    private fun manageViewModel() {

        val viewModelFactory = Go4LunchFactory.instance
        viewModel = ViewModelProvider(this, viewModelFactory!!)[RestaurantDetailsViewModel::class.java]

        intent.getStringExtra(RESTAURANT_ID)?.let { viewModel.init(it) }
    }

    private fun managerAdapter() {

        binding.workMatesRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = RestaurantDetailsAdapter {

        }
        binding.workMatesRecyclerView.adapter = adapter

        viewModel.workmatesLikeThisRestaurantMediatorLiveData.observe(this) { workmates ->
            adapter.submitList(workmates)
        }

    }

    private fun manageView() {

        var detailsViewState: RestaurantDetailsViewState? = null


        viewModel.restaurantDetailsViewStateMediatorLiveData.observe(this) { details ->

            detailsViewState = details

            binding.restaurantDetailName.text = details.detailsRestaurantName
            binding.restaurantDetailAddress.text = details.detailsRestaurantAddress
            Glide.with(binding.restaurantDetailPicture.context)
                .load(details.detailsPhoto)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.restaurantDetailPicture)
            binding.restaurantDetailsRate.rating = details.rating.toFloat()
            binding.restaurantDetailFab.setImageResource(details.choseRestaurantButton)
            binding.restaurantDetailFab.setColorFilter(details.backgroundColor)
            //binding.restaurantDetailLikeButton.setImageResource(details.detailLikeButton)
        }

        binding.restaurantDetailFab.setOnClickListener {

            viewModel.onChoseRestaurantButtonClick(
                detailsViewState!!.detailsRestaurantId!!,
                detailsViewState!!.detailsRestaurantName!!,
                detailsViewState!!.detailsRestaurantAddress!!)

        }

        binding.restaurantDetailLikeButton.setOnClickListener {
            detailsViewState.let {
                viewModel.onFavoriteIconClick(
                    detailsViewState!!.detailsRestaurantId!!,
                    detailsViewState!!.detailsRestaurantName!!)
            }
        }

        binding.restaurantDetailCallButton.setOnClickListener {
            detailsViewState?.let {
                when (detailsViewState!!.detailsRestaurantNumber) {
                    R.string.text_no_phone_number.toString() ->
                        Toast.makeText(this,
                            getString(R.string.text_no_phone_number),
                            Toast.LENGTH_SHORT).show()
                    else -> {
                        startActivity(
                            Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse(getString(R.string.call_button) + detailsViewState!!.detailsRestaurantNumber))
                        )
                    }
                }
            }
        }

        binding.restaurantDetailWebsiteButton.setOnClickListener {
            detailsViewState?.let {
                when (detailsViewState!!.detailsWebsite) {
                    R.string.website_button.toString() ->
                        Toast.makeText(this, getString(R.string.text_no_web_site), Toast.LENGTH_LONG)
                            .show()
                    else -> startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                        Uri.parse(detailsViewState!!.detailsWebsite))
                    )

                }
            }
        }
    }
}