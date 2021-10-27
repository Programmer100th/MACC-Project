package com.bringmetheapp.worldmonuments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.site_item.view.*

class SiteItemAdapter(
    private val siteList: List<SiteItem>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<SiteItemAdapter.SiteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiteViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.site_item,
            parent, false
        )

        return SiteViewHolder(itemView)
    }


    //This function is called a lot of times, be careful with the istructions
    override fun onBindViewHolder(holder: SiteViewHolder, position: Int) {
        val currentItem = siteList[position]

        //imageResource, Site and Relevance are name givens in siteItem kotlin file
        //holder.siteImage.setImageResource(currentItem.imageResource)

        val siteImageLink = currentItem.imageLink



        //If there is no wiki image, then put a placeholder
        if (siteImageLink.isEmpty()) {
            holder.siteImage.setImageResource(R.drawable.ic_videogame);
        } else {

            //holder.homeItemImage.setImageResource(R.drawable.ic_videogame);


            Picasso.get()
                .load(siteImageLink)
                //.resize(100,100)
                //.onlyScaleDown()
                .fit()
                //.centerCrop()
                .into(holder.siteImage)


        }



        holder.siteName.text = currentItem.name
        holder.siteRelevance.text = String.format("%,d", currentItem.relevance)
        holder.siteCountry.text = currentItem.country
        holder.siteCategory.text = currentItem.category
    }

    override fun getItemCount() = siteList.size


    inner class SiteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val siteImage: ImageView = itemView.findViewById(R.id.siteImage)
        val siteName: TextView = itemView.findViewById(R.id.siteName)
        val siteRelevance: TextView = itemView.findViewById(R.id.siteRelevance)
        val siteCountry: TextView = itemView.findViewById(R.id.siteCountry)
        val siteCategory: TextView = itemView.findViewById(R.id.siteCategory)


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = bindingAdapterPosition //Deprecated method in the video
            if(position != RecyclerView.NO_POSITION)
            {
                listener.onItemClick(position)
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}