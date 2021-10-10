package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONException

class SingleSiteFragment : Fragment(R.layout.fragment_single_site) {

    private val args: SingleSiteFragmentArgs by navArgs()

    private val currentUserNickname = "Samu"

    val reviewList = ArrayList<ReviewItem>()

    private var mQueue: RequestQueue? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val singleSiteImage = view.findViewById<ImageView>(R.id.singleSiteImage)
        val singleSiteCategory = view.findViewById<TextView>(R.id.singleSiteCategory)
        val singleSiteCountry = view.findViewById<TextView>(R.id.singleSiteCountry)
        val singleSiteAdmin1Code = view.findViewById<TextView>(R.id.singleSiteAdmin1Code)
        val singleSiteAdmin2Code = view.findViewById<TextView>(R.id.singleSiteAdmin2Code)
        val singleSiteDescription = view.findViewById<TextView>(R.id.singleSiteDescription)
        val singleSiteLink = view.findViewById<TextView>(R.id.singleSiteLink)
        val singleSiteRelevance = view.findViewById<TextView>(R.id.singleSiteRelevance)
        val singleSiteFavorite = view.findViewById<ToggleButton>(R.id.singleSiteFavorite)
        val avgRatingBar = view.findViewById<RatingBar>(R.id.simpleRatingBar)
        val singleSiteReviewsNumber = view.findViewById<TextView>(R.id.singleSiteReviewsNumber)

        val reviewsRecyclerView = view.findViewById<RecyclerView>(R.id.reviewsRecyclerView)

        val singleSiteAddReviewButton = view.findViewById<Button>(R.id.singleSiteAddReviewButton)






        mQueue = Volley.newRequestQueue(context)

        jsonParseReviews(avgRatingBar, singleSiteReviewsNumber, reviewsRecyclerView)

        jsonParseFavorite(singleSiteFavorite)

        singleSiteFavorite.setOnClickListener {
            if (singleSiteFavorite.isChecked) {
                //This means that before of click it was unchecked
                addFavorite()
                Toast.makeText(context, "Site added to favorites", Toast.LENGTH_SHORT).show();


            } else {
                //This means that before of click it was checked
                removeFavorite()
                Toast.makeText(context, "Site removed from favorites", Toast.LENGTH_SHORT).show();

            }

        }

        singleSiteAddReviewButton.setOnClickListener{
            val action = SingleSiteFragmentDirections.actionSingleSiteFragmentToBottomSheetReviewFragment(args.geonameId)
            findNavController().navigate(action)
            //bottomSheetReviewFragment.show(childFragmentManager,"BottomSheedDialog")
        }


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
        singleSiteRelevance.text = args.relevance.toString()

    }


    fun jsonParseReviews(avgRatingBar: RatingBar, singleSiteReviewsNumber: TextView, reviewsRecyclerView: RecyclerView) {
        val url = "https://world-monuments.herokuapp.com/reviews/" + args.geonameId

        var avgRating = 0F
        var numRatings = 0


        val request = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                try {

                    val jsonArray = response




                    for (i in 0 until jsonArray.length()) {
                        val row = jsonArray.getJSONObject(i)
                        Log.d("Result", row.toString())
                        val userNickname = row.getString("userNickname")
                        val siteId = row.getInt("siteId")
                        val score = row.getDouble("rating").toFloat()
                        val review = row.getString("description")
                        avgRating = avgRating + score
                        numRatings = numRatings + 1


                        val item = ReviewItem(
                            score, userNickname, review
                        )
                        reviewList += item

                    }



                    //Order the list according to relevance value
                    reviewList.sortByDescending { it.rating }


                    //Needed to make smoother the scrolling of recycler view inside scroll view
                    reviewsRecyclerView.setNestedScrollingEnabled(false);
                    reviewsRecyclerView.adapter = ReviewItemAdapter(reviewList)
                    reviewsRecyclerView.layoutManager = LinearLayoutManager(context)
                    reviewsRecyclerView.setHasFixedSize(true)


                    avgRatingBar.rating = avgRating / numRatings
                    singleSiteReviewsNumber.text = "(" + numRatings.toString() + " ratings)"
                    Log.d("ECCOLO", singleSiteReviewsNumber.text.toString())


                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue?.add(request)


    }


    fun jsonParseFavorite(singleSiteFavorite: ToggleButton) {
        val url = "https://world-monuments.herokuapp.com/favorites/" + currentUserNickname

        val request = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                try {

                    val jsonArray = response

                    for (i in 0 until jsonArray.length()) {
                        val row = jsonArray.getJSONObject(i)
                        Log.d("Result", row.toString())
                        val siteId = row.getInt("geonameId")
                        if (siteId == args.geonameId) {
                            Log.d("Prova", "ECCOLO")
                            singleSiteFavorite.isChecked = true

                        }

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue?.add(request)


    }

    fun addFavorite() {
        val url =
            "https://world-monuments.herokuapp.com/favorites/" + currentUserNickname + "/" + args.geonameId

        val request = StringRequest(
            Request.Method.PUT, url, { response ->
                try {

                    val jsonArray = response


                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue?.add(request)


    }


    fun removeFavorite() {
        val url =
            "https://world-monuments.herokuapp.com/favorites/" + currentUserNickname + "/" + args.geonameId

        val request = StringRequest(
            Request.Method.DELETE, url, { response ->
                try {

                    val jsonArray = response


                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue?.add(request)

    }


}