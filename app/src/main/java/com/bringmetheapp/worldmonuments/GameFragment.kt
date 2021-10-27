package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bringmetheapp.worldmonuments.Configuration as myConfiguration

class GameFragment : Fragment(R.layout.fragment_game) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSinglePlayer = view.findViewById<Button>(R.id.singlePlayerButton)

        val btnMultiPlayer = view.findViewById<Button>(R.id.multiPlayerButton)

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
    }



}