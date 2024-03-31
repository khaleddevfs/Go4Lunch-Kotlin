package com.example.go4lunch24kotlin.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.go4lunch24kotlin.R
import com.example.go4lunch24kotlin.models.RestaurantsViewState

class RestaurantsAdapter (
    private val listener: (placeId: String) -> Unit
): ListAdapter<RestaurantsViewState, RestaurantsAdapter.RestaurantViewHolder>(ListComparator){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        return RestaurantViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, listener)
    }

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val restaurantName: TextView = itemView.findViewById(R.id.restaurant_item_list_name)
        private val restaurantAddress: TextView = itemView.findViewById(R.id.restaurant_item_list_address)
        private val restaurantOpeningHours: TextView = itemView.findViewById(R.id.restaurant_item_list_info)
        private val restaurantDistanceTo: TextView = itemView.findViewById(R.id.restaurant_item_list_distance)
        private val restaurantRating: RatingBar = itemView.findViewById(R.id.restaurantItemListRate)
        private val interestedWorkmates: TextView = itemView.findViewById(R.id.restaurant_item_list_participants_number)
        private val photo: ImageView = itemView.findViewById(R.id.restaurant_image_photo_item)

        @SuppressLint("ResourceAsColor")
        fun bind(restaurantsViewState: RestaurantsViewState, listener: (String) -> Unit) {

            restaurantName.text = restaurantsViewState.name
            restaurantAddress.text = restaurantsViewState.address
            restaurantOpeningHours.text = restaurantsViewState.openingHours
            restaurantOpeningHours.setTextColor(restaurantsViewState.textColor)
            restaurantDistanceTo.text = restaurantsViewState.distanceText
            restaurantRating.rating = restaurantsViewState.rating.toFloat()
            interestedWorkmates.text = restaurantsViewState.usersWhoChoseThisRestaurant

            Glide
                .with(photo.context)
                .load(restaurantsViewState.photo)
                .override(600, 600)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .into(photo)


            itemView.setOnClickListener {
                listener.invoke(restaurantsViewState.placeId!!)
            }
        }


        companion object {
            fun create(parent: ViewGroup): RestaurantViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.restaurant_item_layout, parent, false)
                return RestaurantViewHolder(view)
            }
        }
    }

    object ListComparator : DiffUtil.ItemCallback<RestaurantsViewState>() {
        override fun areItemsTheSame(
            oldItem: RestaurantsViewState,
            newItem: RestaurantsViewState
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: RestaurantsViewState,
            newItem: RestaurantsViewState
        ): Boolean = oldItem == newItem
    }
}