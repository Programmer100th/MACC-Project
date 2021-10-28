package com.bringmetheapp.worldmonuments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class UserFragment: Fragment(R.layout.fragment_user)  {

    private var currentUserNickname : String? = null

    private var textViewReviews : TextView? = null
    private var textViewFavorites : TextView? = null

    private var mQueue: RequestQueue? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewNickname = view.findViewById<TextView>(R.id.userNickname)
        val textViewEmail = view.findViewById<TextView>(R.id.userEmail)

        val textViewHighscore = view.findViewById<TextView>(R.id.userHighscore)
        textViewReviews = view.findViewById<TextView>(R.id.userReviews)
        textViewFavorites = view.findViewById<TextView>(R.id.userFavorites)

        val buttonLogout = view.findViewById<Button>(R.id.buttonLogout)

        mQueue = Volley.newRequestQueue(context)

        val sharedPreferences = context?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        currentUserNickname = sharedPreferences?.getString("nickname", "").toString()
        val email = sharedPreferences?.getString("email", "").toString()
        val highScore = sharedPreferences?.getInt("highScore", 0).toString()
        Log.d("email", "ciao" + email)

        jsonParseFavorites()
        jsonParseReviews()

        textViewNickname.text = currentUserNickname
        textViewEmail.text = email
        textViewHighscore.text = highScore + "/5"

        buttonLogout.setOnClickListener{

            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Log Out")
            alert.setCancelable(false)
            alert.setMessage("Are you sure you want to exit?")


            alert.setPositiveButton("Yes") { dialog, which ->
                val editor = sharedPreferences?.edit()
                editor?.apply {
                    putString("nickname", "")
                    putString("email", "")
                    putInt("highScore", 0)
                    Log.d("nick", "ok")
                    apply()
                }
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)

            }
            alert.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()

            }
            alert.show()
        }


    }

    fun jsonParseReviews() {
        val url = "https://world-monuments.herokuapp.com/reviews/" + currentUserNickname
        val request = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                try {

                    Log.d("reviews", response.toString())

                    textViewReviews?.text = response.length().toString()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue?.add(request)
    }

    fun jsonParseFavorites() {
        val url = "https://world-monuments.herokuapp.com/favorites/" + currentUserNickname
        val request = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                try {

                    Log.d("favorites", response.toString())

                    textViewFavorites?.text = response.length().toString()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue?.add(request)
    }
}