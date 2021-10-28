package com.bringmetheapp.worldmonuments

import android.R.attr.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


class GameSinglePlayerFragment : Fragment(R.layout.fragment_single_player), SensorEventListener2 {

    private var optionA: TextView? = null
    private var optionB: TextView? = null
    private var optionC: TextView? = null
    private var optionD: TextView? = null

    private var correctAnswer: TextView? = null

    private var questionnumber: TextView? = null
    private var question: TextView? = null
    private var score: TextView? = null

    private var checkout1: TextView? = null
    private var checkout2: TextView? = null

    private var mQueue: RequestQueue? = null

    val siteList = ArrayList<GameSite>()

    var currentIndex = 0
    var mscore = 0
    var qn = 1

    var progressBar: ProgressBar? = null

    lateinit var gameArrow: ImageView

    val PROGRESS_BAR = Math.ceil((100 / 5).toDouble()).toInt()

    private lateinit var sensorManager: SensorManager

    lateinit var main: MainActivity

    var accValues = FloatArray(3)

    var orientation = ""
    var prevOrientation = ""

    var flagC = false
    var flagS = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        main = requireActivity() as MainActivity

        sensorManager = main.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        val root = inflater.inflate(R.layout.fragment_single_player, container, false)

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        optionA = view.findViewById(R.id.optionA)
        optionB = view.findViewById(R.id.optionB)
        optionC = view.findViewById(R.id.optionC)
        optionD = view.findViewById(R.id.optionD)

        correctAnswer = view.findViewById(R.id.correct_answer)

        question = view.findViewById(R.id.question)
        score = view.findViewById(R.id.score)
        questionnumber = view.findViewById(R.id.QuestionNumber)

        checkout1 = view.findViewById(R.id.selectoption)
        checkout2 = view.findViewById(R.id.CorrectAnswer)
        progressBar = view.findViewById(R.id.progress_bar)

        siteList.clear()

        mQueue = Volley.newRequestQueue(context)

        gameArrow = view.findViewById(R.id.gameArrow)

        gameArrow.setImageResource(R.drawable.game_arrow)

        checkOrientation(view)

        loadQuestions()

        /*
        optionA?.setOnClickListener(View.OnClickListener {
            flagS = true
            checkAnswer(optionA!!)
            updateQuestion(view)
        })
        optionB?.setOnClickListener(View.OnClickListener {
            flagS = true
            checkAnswer(optionB!!)
            updateQuestion(view)
        })
        optionC?.setOnClickListener(View.OnClickListener {
            flagS = true
            checkAnswer(optionC!!)
            updateQuestion(view)
        })
        optionD?.setOnClickListener(View.OnClickListener {
            flagS = true
            checkAnswer(optionD!!)
            updateQuestion(view)
        })

         */

    }


    private fun loadQuestions() {

        val countries = resources.getStringArray(R.array.countries)

        Log.d("Item", countries[1])

        val url = "https://world-monuments.herokuapp.com/sites?relevance=10000"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null, { response ->
                try {

                    val jsonArray = response

                    for (i in 0 until jsonArray.length()) {
                        val site = jsonArray.getJSONObject(i)
                        val name = site.getString("name")
                        val country = site.getString("country")

                        val item = GameSite(name, country)

                        siteList += item
                    }

                    siteList.shuffle()

                    val site = siteList[0]

                    var randomCountries = countries.asSequence().shuffled().take(3).toList()

                    Log.d("Random", randomCountries.toString())

                    while (true) {
                        for (elem in randomCountries) {
                            if ((elem.dropLast(5)).equals(site.country))
                                flagC = true
                        }
                        if (flagC == true)
                            randomCountries = countries.asSequence().shuffled().take(3).toList()
                        else
                            break
                    }

                    flagC = false

                    val answers: Array<String> =
                        arrayOf(
                            site.country,
                            randomCountries[0].dropLast(5),
                            randomCountries[1].dropLast(5),
                            randomCountries[2].dropLast(5)
                        )

                    answers.shuffle()

                    question?.setText("Where is the " + site.name + " located?")
                    optionA?.setText(answers[0])
                    optionB?.setText(answers[1])
                    optionC?.setText(answers[2])
                    optionD?.setText(answers[3])
                    correctAnswer?.setText(site.country)

                    Log.d("Item", "ciao")

                    sensorManager.registerListener(
                        this,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL
                    )

                    flagS = false


                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            },
            {
                Log.d("API Request", "Something Went Wrong")
                loadQuestions()
            })

        mQueue?.add(request)

    }

    private fun checkAnswer(answer: String, view: View) {

        Log.d("answer", optionA?.text.toString())

        //gameArrow.setRotation(0f)
        sensorManager.unregisterListener(this@GameSinglePlayerFragment)

        val correctanswer = correctAnswer?.text.toString()

        checkout1!!.setText(answer)
        checkout2!!.setText(correctanswer)
        val m: String = checkout1!!.text.toString().trim()
        val n: String = checkout2!!.text.toString().trim()
        if (m == n) {
            view.setBackgroundColor(Color.GREEN)
            Toast.makeText(requireContext(), "Right", Toast.LENGTH_SHORT).show()
            mscore = mscore + 1

        } else {
            view.setBackgroundColor(Color.RED)
            Toast.makeText(requireContext(), "Wrong", Toast.LENGTH_SHORT).show()

        }

        Handler(Looper.getMainLooper()).postDelayed({
            updateQuestion(view)
        }, 2000)

    }

    @SuppressLint("SetTextI18n")
    private fun updateQuestion(view: View) {

        view.setBackgroundColor(Color.parseColor("#272343"))

        currentIndex = (currentIndex + 1) % 5
        if (currentIndex == 0) {
            optionA?.setBackgroundResource(R.color.option)
            optionB?.setBackgroundResource(R.color.option)
            optionC?.setBackgroundResource(R.color.option)
            optionD?.setBackgroundResource(R.color.option)

            question?.setText("")
            optionA?.setText("")
            optionB?.setText("")
            optionC?.setText("")
            optionD?.setText("")

            orientation = ""

            val sharedPreferences = context?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
            val highScore = sharedPreferences?.getInt("highScore", 0).toString()

            if(mscore > highScore.toInt()){
                val editor = sharedPreferences?.edit()
                editor?.apply {
                    putInt("highScore", mscore)
                    apply()
                }
            }


            val alert = AlertDialog.Builder(view.context)
            alert.setTitle("Game Over")
            alert.setCancelable(false)
            alert.setMessage("Your Score: $mscore points")
            alert.setPositiveButton("Close") { dialog, which ->
                view.findNavController()
                    .navigate(R.id.action_gameSinglePlayerFragment_to_gameFragment)
            }
            alert.setNegativeButton("Restart") { dialog, which ->
                mscore = 0
                qn = 1
                progressBar!!.progress = 0
                score!!.text = "Score: " + mscore + "/" + 5
                questionnumber!!.text = qn.toString() + "/" + 5 + " Question"

                loadQuestions()
            }
            alert.show()
        } else
            loadQuestions()

        qn = qn + 1
        if (qn <= 5) {
            questionnumber!!.text = qn.toString() + "/" + 5 + " Question"
        }
        score!!.text = "Score: " + mscore + "/" + 5
        progressBar!!.incrementProgressBy(PROGRESS_BAR)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> accValues = event.values.clone()
        }

        if (flagS == false) {
            if ((event!!.values[0] <= 2 && event!!.values[0] >= -2) && (event!!.values[1] < 3)) {
                gameArrow.setRotation(0f)
                orientation = "up"
                optionA?.setBackgroundResource(R.color.yellowRating)
                optionB?.setBackgroundResource(R.color.option)
                optionC?.setBackgroundResource(R.color.option)
                optionD?.setBackgroundResource(R.color.option)
            } else if ((event!!.values[0] > 2)) {
                gameArrow.setRotation(270f)
                orientation = "left"
                optionA?.setBackgroundResource(R.color.option)
                optionB?.setBackgroundResource(R.color.yellowRating)
                optionC?.setBackgroundResource(R.color.option)
                optionD?.setBackgroundResource(R.color.option)
            } else if ((event!!.values[0] < -2)) {
                gameArrow.setRotation(90f)
                orientation = "right"
                optionA?.setBackgroundResource(R.color.option)
                optionB?.setBackgroundResource(R.color.option)
                optionC?.setBackgroundResource(R.color.yellowRating)
                optionD?.setBackgroundResource(R.color.option)
            } else {
                gameArrow.setRotation(180f)
                orientation = "down"
                optionA?.setBackgroundResource(R.color.option)
                optionB?.setBackgroundResource(R.color.option)
                optionC?.setBackgroundResource(R.color.option)
                optionD?.setBackgroundResource(R.color.yellowRating)
            }
        }

        view?.invalidate()
    }

    private fun checkOrientation(view: View) {
        if (qn > 5) return
        Handler(Looper.getMainLooper()).postDelayed({
            if (prevOrientation == orientation) {

                flagS = true

                if (orientation.equals("up"))
                    checkAnswer(optionA?.text.toString(), view)
                else if (orientation.equals("left"))
                    checkAnswer(optionB?.text.toString(), view)
                else if (orientation.equals("right"))
                    checkAnswer(optionC?.text.toString(), view)
                else
                    checkAnswer(optionD?.text.toString(), view)

            }
            prevOrientation = orientation

            checkOrientation(view)

        }, 4000)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("Accuracy", "None")
    }

    override fun onFlushCompleted(p0: Sensor?) {
        TODO("Not yet implemented")
    }

}
