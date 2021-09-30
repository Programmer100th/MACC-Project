package com.bringmetheapp.worldmonuments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(R.layout.fragment_search) {





    val categoryArray = arrayOf("Amphitheater", "Archaeological Site", "Building",
        "Castle", "Church", "Fort", "Historical Site", "Monastery", "Monument", "Museum",
        "Palace", "Pyramid", "Ruin", "Temple", "Tower")
    val arrayChecked = booleanArrayOf(true, true, true, true, true, true, true, true, true, true,
        true, true, true, true, true)

    lateinit var dialogCountries:AlertDialog
    lateinit var dialogCategories:AlertDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countriesArray = resources.getStringArray(R.array.countries)




        val btnSearchSites = view.findViewById<Button>(R.id.btnSearchSites)

        val categoriesView = view.findViewById<TextView>(R.id.categoriesView)

        val countriesView = view.findViewById<TextView>(R.id.countriesView)

        val relevanceSlider = view.findViewById<Slider>(R.id.relevanceSlider)

        var selectedCountry = ""
        var selectedCountryIndex = -1

        var selectedCategories = ""

        var selectedRelevance = 0





        countriesView.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select Country")
            builder.setCancelable(false)



            builder.setSingleChoiceItems(
                countriesArray, // array
                selectedCountryIndex // initial selection (-1 none)
            ){dialog2, i ->}

            builder.setPositiveButton("Submit"){dialog2,which->
                val position = (dialog2 as AlertDialog).listView.checkedItemPosition
                // if selected, then get item text
                if (position !=-1){
                    selectedCountryIndex = position
                    val selectedItem = countriesArray[position]
                    countriesView.text = "Selected country: : $selectedItem"
                    selectedCountry = selectedItem.split('(')[1].substring(0, 2)
                }
            }


            builder.setNeutralButton("Cancel",null)

            // Initialize the AlertDialog using builder object
            dialogCountries = builder.create()

            // Finally, display the alert dialog
            dialogCountries.show()

            dialogCountries.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

            // dialog list item click listener
            dialogCountries.listView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    // enable positive button when user select an item
                    dialogCountries.getButton(AlertDialog.BUTTON_POSITIVE)
                        .isEnabled = position != -1
                }
        }




        categoriesView.setOnClickListener{


            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select Categories")
            builder.setCancelable(false)

            builder.setMultiChoiceItems(categoryArray, arrayChecked) { dialog, which, isChecked ->
                // Update the clicked item checked status
                arrayChecked[which] = isChecked

                // Get the clicked item
                val category = categoryArray[which]

            }

            builder.setPositiveButton("OK") { _, _ ->
                // Do something when click positive button
                categoriesView.text = "Selected Categories: \n"
                for (i in 0 until categoryArray.size) {
                    val checked = arrayChecked[i]
                    if (checked) {
                        selectedCategories += categoryArray[i] + ','
                    }
                }
                selectedCategories = selectedCategories.substring(0, selectedCategories.length - 1)
                categoriesView.text = "${categoriesView.text}${selectedCategories}"
            }

            builder.setNegativeButton("Cancel") { _, _ ->
                dialogCategories.dismiss()
            }

            builder.setNeutralButton("Clear All") {_, _ ->
                for (i in 0 until categoryArray.size) {
                    arrayChecked[i] = false
                }

            }


            // Initialize the AlertDialog using builder object
            dialogCategories = builder.create()

            // Finally, display the alert dialog
            dialogCategories.show()
        }



        /*
        relevanceSlider.addOnChangeListener{
            Log.d("SLIDER", "ciaooo")
            selectedRelevance = relevanceSlider.value.toInt()
        }

         */



        val relevance = 200000

        btnSearchSites.setOnClickListener{
            selectedRelevance = relevanceSlider.value.toInt()
            val action = SearchFragmentDirections.actionSearchFragmentToSitesListFragment(selectedCountry, selectedCategories, selectedRelevance)
            findNavController().navigate(action)


        }



    }
}