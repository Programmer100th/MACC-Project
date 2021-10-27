package com.bringmetheapp.worldmonuments

import org.json.JSONArray

class Configuration {
    companion object {

        // User data
        var NICKNAME = ""
        var EMAIL = ""
        var ID = -1

        // Server URL
        const val URL = "https://world-monuments.herokuapp.com/minigameServer"

        // Game states
        val WANT_TO_PLAY=0
        val POLLING = 1
        val ANSWER = 2
        val END = 3

        var END_VALUE = ""

        val pollingPeriod = 500L

        //States of the server
        val INIT = 0 //Not used
        val WAITING = 1
        val READY = 2
        val PLAY = 3 //Not used

        var canPoll=false

        // Game mode
        var MULTIPLAYER = false

        //Questions
        var questions : JSONArray? = null

    }
}