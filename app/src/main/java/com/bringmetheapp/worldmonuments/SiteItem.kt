package com.bringmetheapp.worldmonuments

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//data class SiteItem (val imageResource: Int, val title: String, val relevance: String)
data class SiteItem (val geonameId: Int, val name: String, val longitude: Float, val latitude: Float,
                     val category: String, val country: String, val countryIso: String,
                     val admin1Code: String, val admin2Code: String, val link: String,
                     val relevance: Int, val description: String, val imageLink: String)
