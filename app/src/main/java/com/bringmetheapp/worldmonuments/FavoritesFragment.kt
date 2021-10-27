package com.bringmetheapp.worldmonuments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_sites_list.*
import org.json.JSONException

class FavoritesFragment : Fragment(R.layout.fragment_favorites),
    SiteItemAdapter.OnItemClickListener {


    private lateinit var currentUserNickname : String

    //private val currentUserNickname = "Samu"
    private var mQueue: RequestQueue? = null
    val siteList = ArrayList<SiteItem>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = context?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        currentUserNickname = sharedPreferences?.getString("nickname", "").toString()


        //Svuoto la lista perchè altrimenti dopo aver premuto back
        //dalla schermata del singolo sito mi ritrovo il dopione di tutti i siti
        //ma sarebbe piu corretto chiamare l'api solo una volta
        siteList.clear()


        mQueue = Volley.newRequestQueue(context)

        val favoritesRecyclerView = view.findViewById<RecyclerView>(R.id.favoritesRecyclerView)

        jsonParse(favoritesRecyclerView)

    }


    fun jsonParse(favoritesRecyclerView: RecyclerView) {
        val url = "https://world-monuments.herokuapp.com/favorites/" + currentUserNickname
        val request = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                try {

                    val jsonArray = response

                    for (i in 0 until jsonArray.length()) {
                        val site = jsonArray.getJSONObject(i)
                        val geonameId = site.getInt("geonameId")
                        val name = site.getString("name")
                        val longitude = site.getDouble("longitude").toFloat()
                        val latitude = site.getDouble("latitude").toFloat()
                        val category = site.getString("category")
                        val country = site.getString("country")
                        val countryIso = site.getString("countryIso")
                        val admin1Code = site.getString("admin1Code")
                        val admin2Code = site.getString("admin2Code")
                        val link = site.getString("link")
                        val relevance = site.getInt("relevance")
                        val description = site.getString("description")
                        val imageLink = site.getString("imageLink")


                        val item = SiteItem(
                            geonameId, name, longitude.toDouble(),
                            latitude.toDouble(), category, country, countryIso,
                            admin1Code, admin2Code, link, relevance,
                            description, imageLink
                        )
                        siteList += item

                    }


                    //Order the list according to relevance value
                    siteList.sortByDescending { it.relevance }

                    favoritesRecyclerView.adapter = SiteItemAdapter(siteList, this)
                    favoritesRecyclerView.layoutManager = LinearLayoutManager(context)
                    favoritesRecyclerView.setHasFixedSize(true)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue?.add(request)


    }

    override fun onItemClick(position: Int) {
        val clickedItem = siteList[position]
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToSingleSiteFragment(
            clickedItem.geonameId, clickedItem.name, clickedItem.longitude.toFloat(), clickedItem.latitude.toFloat(),
            clickedItem.category, clickedItem.country, clickedItem.countryIso,
            clickedItem.admin1Code, clickedItem.admin2Code, clickedItem.link,
            clickedItem.relevance, clickedItem.description, clickedItem.imageLink
        )
        findNavController().navigate(action)
    }


}