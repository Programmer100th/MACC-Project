package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.bringmetheapp.worldmonuments.Configuration
import com.bringmetheapp.worldmonuments.GameMultiPlayerView
import com.bringmetheapp.worldmonuments.R

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameMultiPlayerFragment : Fragment() {

    lateinit var pv : GameMultiPlayerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        pv = GameMultiPlayerView(context, this)
        return pv
        //return inflater.inflate(R.layout.fragment_second, container, false)
    }


    override fun onStart() {
        super.onStart()
        Configuration.canPoll=true
        pv.poll()
    }

    override fun onPause() {
        super.onPause()
        Configuration.canPoll=false
    }



}