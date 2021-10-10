package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONException

class BottomSheetReviewFragment : BottomSheetDialogFragment() {

    private val args: BottomSheetReviewFragmentArgs by navArgs()

    private val currentUserNickname = "Samu"
    private var mQueue: RequestQueue? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_review, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addReviewRating = view.findViewById<RatingBar>(R.id.addReviewRating)
        val addReviewDescription = view.findViewById<EditText>(R.id.addReviewDescription)
        val addReviewCancelButton = view.findViewById<Button>(R.id.addReviewCancelButton)
        val addReviewConfirmButton = view.findViewById<Button>(R.id.addReviewConfirmButton)


        mQueue = Volley.newRequestQueue(context)


        addReviewCancelButton.setOnClickListener {
            dismiss()
        }

        addReviewConfirmButton.setOnClickListener {
            val rating = addReviewRating.rating
            val description = addReviewDescription.text.toString()

            if (rating == 0.0F && description.isEmpty()) {
                Toast.makeText(context, "Rating and Description are required", Toast.LENGTH_SHORT)
                    .show();
            } else if (rating == 0.0F) {
                Toast.makeText(context, "Rating is required", Toast.LENGTH_SHORT)
                    .show();
            }
            else if (description.isEmpty())
            {
                Toast.makeText(context, "Description is required", Toast.LENGTH_SHORT)
                    .show();
            }
            else{
                addReview(rating, description)
                dismiss()



                Toast.makeText(context, "Review added", Toast.LENGTH_SHORT)
                    .show();
            }

        }

    }


    fun addReview(rating: Float, description: String) {
        val url =
            "https://world-monuments.herokuapp.com/reviews/" + args.geonameId + "/" + currentUserNickname + "?" +
                    "rating=" + rating + "&description=" + description


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

}