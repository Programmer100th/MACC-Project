package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_sites_list.*
import org.json.JSONException

class SitesListFragment : Fragment(R.layout.fragment_sites_list),
    SiteItemAdapter.OnItemClickListener {

    private val args: SitesListFragmentArgs by navArgs()

    private var mQueue: RequestQueue? = null
    val siteList = ArrayList<SiteItem>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {

            Log.d("Arguments", args.country)
            Log.d("Arguments", args.relevance.toString())
            Log.d("Arguments", args.categories)


            //Svuoto la lista perchè altrimenti dopo aver premuto back
            //dalla schermata del singolo sito mi ritrovo il dopione di tutti i siti
            //ma sarebbe piu corretto chiamare l'api solo una volta
            siteList.clear()





            mQueue = Volley.newRequestQueue(context)


            /*
        val siteList = generateDummyList(50)
        recyclerView.adapter = SiteItemAdapter(siteList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
         */


            jsonParse()
        }


    }


    fun jsonParse() {
        val url = "https://world-monuments.herokuapp.com/sites?" +
                "country=" + args.country + "&category=" + args.categories + "&relevance=" + args.relevance
        val request = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                try {

                    val jsonArray = response

                    for (i in 0 until jsonArray.length()) {
                        val site = jsonArray.getJSONObject(i)
                        val geonameId = site.getInt("geonameId")
                        val name = site.getString("name")
                        val longitude = site.getLong("longitude").toFloat()
                        val latitude = site.getLong("latitude").toFloat()
                        val category = site.getString("category")
                        val country = site.getString("country")
                        val countryIso = site.getString("countryIso")
                        val admin1Code = site.getString("admin1Code")
                        val admin2Code = site.getString("admin2Code")
                        val link = site.getString("link")
                        val relevance = site.getInt("relevance")
                        val description = site.getString("description")
                        val imageLink = site.getString("imageLink")


                        //val drawable = R.drawable.ic_videogame


                        //val item = SiteItem(drawable, name, relevance)
                        val item = SiteItem(
                            geonameId, name, longitude,
                            latitude, category, country, countryIso,
                            admin1Code, admin2Code, link, relevance,
                            description, imageLink
                        )
                        siteList += item

                    }


                    //Order the list according to relevance value
                    siteList.sortByDescending { it.relevance }

                    recyclerView.adapter = SiteItemAdapter(siteList, this)
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.setHasFixedSize(true)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue?.add(request)


    }

    override fun onItemClick(position: Int) {
        val clickedItem = siteList[position]
        val action = SitesListFragmentDirections.actionSitesListFragmentToSingleSiteFragment(
            clickedItem.geonameId, clickedItem.name, clickedItem.longitude, clickedItem.latitude,
            clickedItem.category, clickedItem.country, clickedItem.countryIso,
            clickedItem.admin1Code, clickedItem.admin2Code, clickedItem.link,
            clickedItem.relevance, clickedItem.description, clickedItem.imageLink
        )
        findNavController().navigate(action)
    }


    /*
    private fun generateDummyList(size: Int): List<SiteItem> {
        val list = ArrayList<SiteItem>()
        for (i in 0 until size) {
            val drawable = when (i % 3) {
                0 -> R.drawable.ic_videogame
                1 -> R.drawable.ic_videogame
                else -> R.drawable.ic_videogame
            }
            val item = SiteItem(drawable, "Item $i", "Line 2")
            list += item
        }
        return list
    }

     */
}