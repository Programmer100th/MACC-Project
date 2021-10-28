package com.bringmetheapp.worldmonuments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GameMultiPlayerFragment : Fragment(R.layout.fragment_multi_player) {

    private var optionA: AppCompatButton? = null
    private var optionB: AppCompatButton? = null
    private var optionC: AppCompatButton? = null
    private var optionD: AppCompatButton? = null

    private var correctAnswer: TextView? = null

    private var questionnumber: TextView? = null
    private var question: TextView? = null
    private var mScore: TextView? = null
    private var oScore: TextView? = null

    private var checkout1: TextView? = null
    private var checkout2: TextView? = null

    val siteList = ArrayList<GameSite>()

    var currentIndex = 0
    var myScore = 0
    var opponentScore = 0
    var qn = 1

    var progressBar: ProgressBar? = null

    val PROGRESS_BAR = Math.ceil((100 / 5).toDouble()).toInt()

    val POLLING = Configuration.POLLING
    val ANSWER = Configuration.ANSWER
    val END = Configuration.END
    val who = Configuration.ID

    val pollingperiod = Configuration.pollingPeriod

    var queue: RequestQueue? = null

    val url = Configuration.URL
    var reply: JSONObject? = null

    private var winner = -1

    private lateinit var currentUserNickname : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        optionA = view.findViewById(R.id.optionA)
        optionB = view.findViewById(R.id.optionB)
        optionC = view.findViewById(R.id.optionC)
        optionD = view.findViewById(R.id.optionD)

        correctAnswer = view.findViewById(R.id.correct_answer)

        question = view.findViewById(R.id.question)
        mScore = view.findViewById(R.id.score_0)
        oScore = view.findViewById(R.id.score_1)
        questionnumber = view.findViewById(R.id.QuestionNumber)

        checkout1 = view.findViewById(R.id.selectoption)
        checkout2 = view.findViewById(R.id.CorrectAnswer)
        progressBar = view.findViewById(R.id.progress_bar)

        siteList.clear()

        queue = Volley.newRequestQueue(context)

        Configuration.canPoll = true

        loadQuestions()

        poll()

        optionA?.setOnClickListener {
            optionA?.setBackgroundColor(Color.YELLOW)
            makeAnswer(0)
            checkAnswer(optionA!!, who)
            //updateQuestion(this)
        }
        optionB?.setOnClickListener {
            optionB?.setBackgroundColor(Color.YELLOW)
            makeAnswer(1)
            checkAnswer(optionB!!, who)
            //updateQuestion(this)
        }
        optionC?.setOnClickListener {
            optionC?.setBackgroundColor(Color.YELLOW)
            makeAnswer(2)
            checkAnswer(optionC!!, who)
            //updateQuestion(this)
        }
        optionD?.setOnClickListener {
            optionD?.setBackgroundColor(Color.YELLOW)
            makeAnswer(3)
            checkAnswer(optionD!!, who)
            //updateQuestion(this)
        }

    }

    private fun loadQuestions() {

        val questionJSON: JSONObject = (Configuration.questions)?.get(qn - 1) as JSONObject

        Log.d("Q1", questionJSON.toString())

        val values = questionJSON["Question" + qn] as JSONArray

        Log.d("Q1", values.toString())

        question!!.setText(values.getString(0))

        optionA!!.setText(values.getString(1))
        optionB!!.setText(values.getString(2))
        optionC!!.setText(values.getString(3))
        optionD!!.setText(values.getString(4))

        correctAnswer!!.setText(values.getString(5))

    }

    private fun checkAnswer(answerButton: AppCompatButton, player: Int) {

        val answer = answerButton?.text.toString()

        val correctanswer = correctAnswer?.text.toString()

        Log.d("ans", correctanswer + " " + answer)

        checkout1!!.setText(answer)
        checkout2!!.setText(correctanswer)
        val m: String = checkout1!!.text.toString().trim()
        val n: String = checkout2!!.text.toString().trim()
        if (m == n) {
            if (player == who) {
                answerButton.setBackgroundColor(Color.GREEN)
                Toast.makeText(context, "Right", Toast.LENGTH_SHORT).show()
                myScore += 1
            } else
                opponentScore += 1

            Thread.sleep(1000)

            reset()

            Thread.sleep(1000)

            answerButton.setBackgroundColor(Color.WHITE)

            updateQuestion(requireView())

        } else {
            if(player == who) {
                answerButton.setBackgroundColor(Color.RED)
                Toast.makeText(context, "Wrong", Toast.LENGTH_SHORT).show()

                markButtonDisable(optionA!!)
                markButtonDisable(optionB!!)
                markButtonDisable(optionC!!)
                markButtonDisable(optionD!!)

                Thread.sleep(500)

                answerButton.setBackgroundColor(Color.WHITE)

                markButtonEnable(optionA!!)
                markButtonEnable(optionB!!)
                markButtonEnable(optionC!!)
                markButtonEnable(optionD!!)
                /*optionA!!.setEnabled(false)
                optionB!!.setEnabled(false)
                optionC!!.setEnabled(false)
                optionD!!.setEnabled(false)
                Thread.sleep(1000)
                optionA!!.setEnabled(true)
                optionB!!.setEnabled(true)
                optionC!!.setEnabled(true)
                optionD!!.setEnabled(true)*/


            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun updateQuestion(view: View) {
        /*
            optionA?.setBackgroundResource(R.color.option)
            optionB?.setBackgroundResource(R.color.option)
            optionC?.setBackgroundResource(R.color.option)
            optionD?.setBackgroundResource(R.color.option)
        */
        question?.setText("")
        optionA?.setText("")
        optionB?.setText("")
        optionC?.setText("")
        optionD?.setText("")

        qn = qn + 1

        currentIndex = (currentIndex + 1) % 5

        if (currentIndex == 0) {
            val win = checkwinner()
            Configuration.canPoll = false
            val alert = AlertDialog.Builder(view.context)
            if(win == 1)
                alert.setTitle("You win the game!")
            else
                alert.setTitle("You lose the game!")
            alert.setCancelable(false)
            alert.setMessage("Your Score: $myScore points")
            alert.setPositiveButton("Close") { dialog, which ->
                endGame()
                findNavController().navigate(R.id.action_gameMultiPlayerFragment_to_gameFragment)
            }
            alert.show()
        } else
            loadQuestions()

        if (qn <= 5) {
            questionnumber!!.text = qn.toString() + "/" + 5 + " Question"
        }
        mScore!!.text = "My Score: " + myScore
        oScore!!.text = "Opponent Score: " + opponentScore
        progressBar!!.incrementProgressBy(PROGRESS_BAR)
    }


    fun checkwinner(): Int {

        winner = 0

        if (myScore > opponentScore) {
            winner = 1
        }

        val sharedPreferences = context?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        currentUserNickname = sharedPreferences?.getString("nickname", "").toString()

        if(Configuration.nicknameList.contains(currentUserNickname)) {

            val stringRequest = StringRequest(
                Request.Method.PATCH,
                "https://world-monuments.herokuapp.com/minigameResults/" + currentUserNickname + "?gameWon=" + winner,
                { response ->

                    Log.i("info", "response: " + response)
                },
                { error: VolleyError? ->
                    // Log.i("info", "makeMove: " + error.toString())
                })
            // Add the request to the RequestQueue.
            queue?.add(stringRequest)
        }
        else {

            val stringRequest = StringRequest(
                Request.Method.PUT,
                "https://world-monuments.herokuapp.com/minigameResults" + "?nickname=" + currentUserNickname + "&gameWon=" + winner,
                { response ->

                    Log.i("info", "response: " + response)
                },
                { error: VolleyError? ->
                    // Log.i("info", "makeMove: " + error.toString())
                })
            // Add the request to the RequestQueue.
            queue?.add(stringRequest)
        }


        return winner
    }

    fun endGame() {
        val stringRequest = StringRequest(
            Request.Method.GET, url + "?req=" + END + "&who=" + who,
            { response ->
                // Display the first 500 characters of the response string.
                reply = JSONObject(response.toString())

                Log.i("info", "response: " + reply?.toString(2))
            },
            { error: VolleyError? ->
                // Log.i("info", "makeMove: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue?.add(stringRequest)

    }

    fun reset() {
        val stringRequest = StringRequest(
            Request.Method.POST, url + "?req=" + ANSWER + "&who=" + who + "&answer=" + -1,
            { response ->
                // Display the first 500 characters of the response string.
                reply = JSONObject(response.toString())
                Log.i("info", "response: " + reply?.toString(2))
            },
            { error: VolleyError? ->
                // Log.i("info", "makeMove: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue?.add(stringRequest)

    }

    fun makeAnswer(k: Int) {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.POST,
            url + "?req=" + ANSWER + "&who=" + who + "&answer=" + k.toString(),
            { response ->
                // Display the first 500 characters of the response string.
                reply = JSONObject(response.toString())
                Log.i("info", "response: " + reply?.toString(2) + " " + k.toString())
            },
            { error: VolleyError? ->
                // Log.i("info", "makeMove: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue?.add(stringRequest)

    }


    // Function to check the answers of the players
    fun getAnswer() {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url + "?req=" + POLLING + "&who=" + who,
            { response ->
                // Display the first 500 characters of the response string.
                reply = JSONObject(response.toString())

                Log.d("answer", reply.toString())

                var a = -1
                var who = -1

                if (reply!!.has("answer") && reply!!.has("who")) {
                    a = reply!!.getInt("answer")
                    who = reply!!.getInt("who")
                }

                if (a != -1) {
                    val answers = arrayOf(
                        optionA,
                        optionB,
                        optionC,
                        optionD
                    )

                    checkAnswer(answers[a]!!, who)


                    if ((who != 0) and (who != 1)) Log.i("info", "who out of range")

                    Log.i("info", "response: " + reply?.toString(2))

                }

            },
            { error: VolleyError? ->
                Log.i("info", "Polling: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue?.add(stringRequest)
    }

    fun poll() {
        if (!Configuration.canPoll) return
        if (winner != -1) return
        Handler(Looper.getMainLooper()).postDelayed({
            getAnswer()
            poll()
        }, pollingperiod)

    }

    fun markButtonEnable(button: AppCompatButton) {
        button?.isEnabled = true
        button?.isClickable = true
    }

    fun markButtonDisable(button: AppCompatButton) {
        button?.isEnabled = false
        button?.isClickable = false
    }

}