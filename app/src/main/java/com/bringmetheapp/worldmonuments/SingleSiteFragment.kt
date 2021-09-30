package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_single_site.*

class SingleSiteFragment : Fragment(R.layout.fragment_single_site) {

    private val args: SingleSiteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val singleSiteImage = view.findViewById<ImageView>(R.id.singleSiteImage)
        val singleSiteCategory = view.findViewById<TextView>(R.id.singleSiteCategory)
        val singleSiteCountry = view.findViewById<TextView>(R.id.singleSiteCountry)
        val singleSiteAdmin1Code = view.findViewById<TextView>(R.id.singleSiteAdmin1Code)
        val singleSiteAdmin2Code = view.findViewById<TextView>(R.id.singleSiteAdmin2Code)
        val singleSiteDescription = view.findViewById<TextView>(R.id.singleSiteDescription)
        val singleSiteLink = view.findViewById<TextView>(R.id.singleSiteLink)


        val url = args.imageLink

        //If there is no wiki image, then put a placeholder
        if (url.isEmpty()) {
            singleSiteImage.setImageResource(R.drawable.ic_videogame);
        } else {

            Picasso.get()
                .load(url)
                //.resize(100,100)
                //.onlyScaleDown()
                .fit()
                //.centerCrop()
                .into(singleSiteImage)
        }

        singleSiteCategory.text = args.category
        singleSiteCountry.text = args.country
        singleSiteAdmin1Code.text = args.admin1Code
        singleSiteAdmin2Code.text = args.admin2Code
        singleSiteDescription.text = args.description
        singleSiteLink.text = args.link

    }


}