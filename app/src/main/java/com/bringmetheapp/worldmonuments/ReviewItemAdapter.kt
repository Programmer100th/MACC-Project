package com.bringmetheapp.worldmonuments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewItemAdapter(private val reviewList:List<ReviewItem>) : RecyclerView.Adapter<ReviewItemAdapter.ReviewViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.review_item,
            parent, false
        )

        return ReviewViewHolder(itemView)
    }


    //This function is called a lot of times, be careful with the istructions
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentItem = reviewList[position]

        holder.reviewRating.rating = currentItem.rating
        holder.reviewUser.text = currentItem.userNickame
        holder.reviewDescription.text = currentItem.review

    }

    override fun getItemCount() = reviewList.size


    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val reviewRating: RatingBar = itemView.findViewById(R.id.reviewRating)
        val reviewUser: TextView = itemView.findViewById(R.id.reviewUser)
        val reviewDescription: TextView = itemView.findViewById(R.id.reviewDescription)



    }


}