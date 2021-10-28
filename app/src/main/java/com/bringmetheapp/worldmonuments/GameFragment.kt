package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import com.bringmetheapp.worldmonuments.Configuration as myConfiguration

class GameFragment : Fragment(R.layout.fragment_game) {

    /*private lateinit var nicknameList : Array<String>
    private lateinit var gamesWonList : Array<String>
    private lateinit var gamesLostList : Array<String>*/

    private var mQueue: RequestQueue? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSinglePlayer = view.findViewById<Button>(R.id.singlePlayerButton)

        val btnMultiPlayer = view.findViewById<Button>(R.id.multiPlayerButton)

        val btnScores = view.findViewById<Button>(R.id.scoresButton)

        mQueue = Volley.newRequestQueue(context)

        myConfiguration.nicknameList.clear()
        myConfiguration.gamesWonList.clear()
        myConfiguration.gamesLostList.clear()

        jsonParseMinigameScores()

        btnSinglePlayer.setOnClickListener{
            myConfiguration.MULTIPLAYER = false
            val action = GameFragmentDirections.actionGameFragmentToGameSinglePlayerFragment()
            findNavController().navigate(action)
        }

        btnMultiPlayer.setOnClickListener{
            myConfiguration.MULTIPLAYER = true
            myConfiguration.ID = -1
            val action = GameFragmentDirections.actionGameFragmentToGameLoadingFragment()
            findNavController().navigate(action)
        }

        btnScores.setOnClickListener{
            val action = GameFragmentDirections.actionGameFragmentToGameScoresFragment()
            findNavController().navigate(action)
        }
    }

    fun jsonParseMinigameScores() {

        Log.d("Result", "ok")

        val url = "https://world-monuments.herokuapp.com/minigameResults"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                try {

                    val jsonArray = response

                    for (i in 0 until jsonArray.length()) {
                        val row = jsonArray.getJSONObject(i)
                        Log.d("Result", row.toString())
                        val nickname = row.getString("nickname")
                        val gamesWon = row.getInt("gamesWon")
                        val gamesLost = row.getInt("gamesPlayed") - gamesWon

                        myConfiguration.nicknameList += nickname
                        myConfiguration.gamesWonList += gamesWon
                        myConfiguration.gamesLostList += gamesLost


                        /*nicknameList += nickname
                        gamesWonList += gamesWon.toString()
                        gamesLostList += gamesLost.toString()*/

                    }

                    /*Log.d("nick", nicknameList.toString())
                    Log.d("gw", gamesWonList.toString())
                    Log.d("gl", gamesLostList.toString())*/

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            { Log.d("API Request", "Something Went Wrong") })

        mQueue?.add(request)
    }

}