package com.bringmetheapp.worldmonuments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class HomeItemAdapter(
    private val homeList: List<HomeItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<HomeItemAdapter.ReviewViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.home_item,
            parent, false
        )

        return ReviewViewHolder(itemView)
    }


    //This function is called a lot of times, be careful with the istructions
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentItem = homeList[position]

        val homeItemImageLink = currentItem.imageLink


        //If there is no wiki image, then put a placeholder
        if (homeItemImageLink.isEmpty()) {
            holder.homeItemImage.setImageResource(R.drawable.ic_videogame);
        } else {

            //holder.homeItemImage.setImageResource(R.drawable.ic_videogame);


            Picasso.get()
                .load(homeItemImageLink)
                //.resize(100,100)
                //.onlyScaleDown()
                .fit()
                //.centerCrop()
                .into(holder.homeItemImage)


        }


        holder.homeItemCountry.text = currentItem.country
        holder.homeItemCategory.text = currentItem.category

    }

    override fun getItemCount() = homeList.size


    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val homeItemImage: ImageView = itemView.findViewById(R.id.homeItemImage)
        val homeItemCountry: TextView = itemView.findViewById(R.id.homeItemCountry)
        val homeItemCategory: TextView = itemView.findViewById(R.id.homeItemCategory)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = bindingAdapterPosition //Deprecated method in the video
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
