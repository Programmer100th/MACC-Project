package com.bringmetheapp.worldmonuments

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bringmetheapp.worldmonuments.Configuration
import com.bringmetheapp.worldmonuments.R
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [Waiting.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameLoadingFragment : Fragment() {

    var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        queue = Volley.newRequestQueue(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        want_to_play()
        //  Handler(Looper.getMainLooper()).postDelayed({check()},Conf.pollingPeriod)
    }


    fun want_to_play() {

        Log.d("Play", "play")

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET,
            Configuration.URL + "?req=" + Configuration.WANT_TO_PLAY + "&who=" + Configuration.ID,
            { response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                val error = reply!!.getBoolean("error")
                if (error) {
                    Toast.makeText(context, "error:" + reply.toString(), Toast.LENGTH_SHORT).show()
                } else {
                    val who = reply!!.getInt("who")
                    Log.d("info", "response: " + reply?.toString(2))
                    if (who == 0) {
                        Configuration.ID = 0
                        //Wait until the other player comes in
                        Handler(Looper.getMainLooper()).postDelayed(
                            { check() },
                            Configuration.pollingPeriod
                        )
                    }

                    if (who == 1) {
                        Configuration.ID = 1
                        val action =
                            GameLoadingFragmentDirections.actionGameLoadingFragmentToGameMultiPlayerFragment()
                        findNavController().navigate(action)
                    }

                    Configuration.questions = reply!!.getString("questions")

                    Log.d("Quest", Configuration.questions)
                }
            },
            { error: VolleyError? ->
                Log.i("info", "Polling: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue?.add(stringRequest)
    }

    fun check() {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET,
            Configuration.URL + "?req=" + Configuration.POLLING + "&who=" + Configuration.ID,
            { response ->
                // Display the first 500 characters of the response string.
                val reply = JSONObject(response.toString())
                if (reply!!.getString("error") == "false") {
                    val state = reply!!.getInt("state")
                    Log.i("info", "response: " + reply?.toString(2))

                    //No players yet, keep polling...
                    if (state == Configuration.WAITING) {
                        Handler(Looper.getMainLooper()).postDelayed(
                            { check() },
                            Configuration.pollingPeriod
                        )
                    } else {
                        findNavController().navigate(R.id.action_gameLoadingFragment_to_gameMultiPlayerFragment)
                    }
                }
            },
            { error: VolleyError? ->
                Log.i("info", "Polling: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue?.add(stringRequest)
    }

}