package com.bringmetheapp.worldmonuments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Configuration.NICKNAME.equals(""))
            view.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)

    }
}