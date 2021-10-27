package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home), HomeItemAdapter.OnItemClickListener {

    val homeList = ArrayList<HomeItem>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        homeList.clear()

        val homeRecyclerView = view.findViewById<RecyclerView>(R.id.homeRecyclerView)

        val itemItaly = HomeItem("https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/Colosseo_2020.jpg/320px-Colosseo_2020.jpg", "Italy", "Amphitheater")
        val itemUnitedKingdom = HomeItem("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Tower_of_London_from_the_Shard_%288515883950%29.jpg/320px-Tower_of_London_from_the_Shard_%288515883950%29.jpg", "United Kingdom", "Castle")
        val itemSpain = HomeItem("https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/%CE%A3%CE%B1%CE%B3%CF%81%CE%AC%CE%B4%CE%B1_%CE%A6%CE%B1%CE%BC%CE%AF%CE%BB%CE%B9%CE%B1_2941.jpg/216px-%CE%A3%CE%B1%CE%B3%CF%81%CE%AC%CE%B4%CE%B1_%CE%A6%CE%B1%CE%BC%CE%AF%CE%BB%CE%B9%CE%B1_2941.jpg", "Spain", "Monument")
        val itemGermany = HomeItem("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/Castle_Neuschwanstein.jpg/320px-Castle_Neuschwanstein.jpg", "Germany", "Castle")
        val itemUnitedStates = HomeItem("https://upload.wikimedia.org/wikipedia/commons/thumb/1/10/Empire_State_Building_%28aerial_view%29.jpg/213px-Empire_State_Building_%28aerial_view%29.jpg", "United States", "Building")
        val itemEgypt = HomeItem("https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Kheops-Pyramid.jpg/320px-Kheops-Pyramid.jpg", "Egypt", "Pyramid")
        val itemFrance = HomeItem("https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/Palace_2%2C_Versailles_August_2013.jpg/320px-Palace_2%2C_Versailles_August_2013.jpg", "France", "Palace")



        homeList += itemItaly
        homeList += itemUnitedKingdom
        homeList += itemSpain
        homeList += itemGermany
        homeList += itemUnitedStates
        homeList += itemEgypt
        homeList += itemFrance

        homeRecyclerView.adapter = HomeItemAdapter(homeList, this)
        homeRecyclerView.layoutManager = LinearLayoutManager(context)
        homeRecyclerView.setHasFixedSize(true)
    }


    override fun onItemClick(position: Int) {
        val clickedItem = homeList[position]

        Log.d("CLICKED", clickedItem.toString())

        var countryIso = ""

        when(clickedItem.country)
        {
            "Italy" -> countryIso = "IT"
            "United Kingdom" -> countryIso = "GB"
            "Spain" -> countryIso = "ES"
            "France" -> countryIso = "FR"
            "Germany" -> countryIso = "DE"
            "United States" -> countryIso = "US"
            "Egypt" -> countryIso = "EG"
            else -> countryIso = "World"
        }




        val action = HomeFragmentDirections.actionHomeFragmentToSitesListFragment(

            countryIso, clickedItem.category, 0)


        findNavController().navigate(action)



    }


}