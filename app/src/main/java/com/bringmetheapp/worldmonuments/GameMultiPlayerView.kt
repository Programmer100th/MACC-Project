package com.bringmetheapp.worldmonuments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bringmetheapp.worldmonuments.Configuration
import org.json.JSONException
import org.json.JSONObject

class GameMultiPlayerView(context: Context?, fragment: GameMultiPlayerFragment): View(context), SensorEventListener2 {

    private var fragment = fragment

    private var optionA: TextView? = null
    private var optionB: TextView? = null
    private var optionC: TextView? = null
    private var optionD: TextView? = null

    private var correctAnswer: TextView? = null

    private var questionnumber: TextView? = null
    private var question: TextView? = null
    private var mScore: TextView? = null
    private var oScore: TextView? = null

    private var checkout1: TextView? = null
    private var checkout2: TextView? = null

    private var mQueue: RequestQueue? = null

    val siteList = ArrayList<GameSite>()

    var currentIndex = 0
    var myScore = 0
    var opponentScore = 0
    var qn = 1

    var progressBar: ProgressBar? = null

    //var gameArrow: ImageView? = null

    val PROGRESS_BAR = Math.ceil((100 / 5).toDouble()).toInt()

    private var sensorManager: SensorManager

    //lateinit var main: MainActivity

    var mAccelerometer: Sensor? = null
    var accValues = FloatArray(3)

    var orientation = ""
    var prevOrientation = ""

    var flagC = false
    var flagS = false


    val POLLING = Configuration.POLLING
    val ANSWER = Configuration.ANSWER
    val who = Configuration.ID

    val pollingperiod = Configuration.pollingPeriod

    val queue = Volley.newRequestQueue(context)

    val url = Configuration.URL
    var reply : JSONObject? = null

    private var winner = -1
    private var chess = IntArray(9) { -1 }

    private val linepaint = Paint().apply {
        color= Color.WHITE
        strokeWidth=10f
        style= Paint.Style.STROKE
    }


    private var dx=0f
    private var dy=0f


    init {
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        /*sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_NORMAL
        )*/

        /*sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )*/

        optionA = this.findViewById(R.id.optionA)
        optionB = this.findViewById(R.id.optionB)
        optionC = this.findViewById(R.id.optionC)
        optionD = this.findViewById(R.id.optionD)

        correctAnswer = this.findViewById(R.id.correct_answer)

        question = this.findViewById(R.id.question)
        mScore = this.findViewById(R.id.score_0)
        oScore = this.findViewById(R.id.score_1)
        questionnumber = this.findViewById(R.id.QuestionNumber)

        checkout1 = this.findViewById(R.id.selectoption)
        checkout2 = this.findViewById(R.id.CorrectAnswer)
        progressBar = this.findViewById(R.id.progress_bar)

        siteList.clear()

        mQueue = Volley.newRequestQueue(context)

        //val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        //val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        loadQuestions()

        Log.d("Prova", siteList.size.toString())

        //gameArrow = this.findViewById(R.id.gameArrow)

        //gameArrow!!.setImageResource(R.drawable.game_arrow)

        /*AM.postRotate(-yaw * 180 / Math.PI.toFloat(), view.width / 2f, view.height / 2f)

        gameArrow.setImageMatrix(AM)*/


        optionA?.setOnClickListener(View.OnClickListener {
            flagS = true
            makeAnswer(0)
            //checkAnswer(optionA!!, who)
            //updateQuestion(this)
        })
        optionB?.setOnClickListener(View.OnClickListener {
            flagS = true
            makeAnswer(1)
            //checkAnswer(optionB!!, who)
            //updateQuestion(this)
        })
        optionC?.setOnClickListener(View.OnClickListener {
            flagS = true
            makeAnswer(2)
            //checkAnswer(optionC!!, who)
            //updateQuestion(this)
        })
        optionD?.setOnClickListener(View.OnClickListener {
            flagS = true
            makeAnswer(3)
            //checkAnswer(optionD!!, who)
            //updateQuestion(this)
        })

    }

    private fun loadQuestions() {

        val stringRequest = StringRequest(
            Request.Method.GET, url + "?req=" + POLLING + "&who=" + who,
            { response ->
                // Display the first 500 characters of the response string.
                reply = JSONObject(response.toString())
                val k = reply!!.getInt("answer")
                val who = reply!!.getInt("who")

                val answers = arrayOf(optionA?.text.toString(), optionB?.text.toString(), optionC?.text.toString(), optionD?.text.toString())

                checkAnswer(answers[k], who)

                if ((who != 0) and (who != 1)) Log.i("info", "who out of range")

                Log.i("info", "response: " + reply?.toString(2))

                invalidate()

            },
            { error: VolleyError? ->
                Log.i("info", "Polling: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)

    }


    private fun checkAnswer(answer: String, player: Int) {

        sensorManager.unregisterListener(this@GameMultiPlayerView)

        /*val mTextView: TextView

        if (orientation.equals("up"))
            mTextView = optionA!!
        else if (orientation.equals("left"))
            mTextView = optionB!!
        else if (orientation.equals("right"))
            mTextView = optionC!!
        else
            mTextView = optionD!!*/

        val correctanswer = correctAnswer?.text.toString()

        checkout1!!.setText(answer)
        checkout2!!.setText(correctanswer)
        val m: String = checkout1!!.text.toString().trim()
        val n: String = checkout2!!.text.toString().trim()
        if (m == n) {
            if(player == who) {
                Toast.makeText(context, "Right", Toast.LENGTH_SHORT).show()
                myScore += 1
            }
            else
                opponentScore += 1
        } else {
            Toast.makeText(context, "Wrong", Toast.LENGTH_SHORT).show()
        }

        Thread.sleep(1000)

        updateQuestion(this)


    }

    @SuppressLint("SetTextI18n")
    private fun updateQuestion(view: View) {

        optionA?.setBackgroundResource(R.color.option)
        optionB?.setBackgroundResource(R.color.option)
        optionC?.setBackgroundResource(R.color.option)
        optionD?.setBackgroundResource(R.color.option)

        question?.setText("")
        optionA?.setText("")
        optionB?.setText("")
        optionC?.setText("")
        optionD?.setText("")

        currentIndex = (currentIndex + 1) % 5
        if (currentIndex == 0) {
            val alert = AlertDialog.Builder(view.context)
            alert.setTitle("Game Over")
            alert.setCancelable(false)
            alert.setMessage("Your Score: $myScore points")
            alert.setPositiveButton("Close") { dialog, which ->
                view.findNavController()
                    .navigate(R.id.action_gameSinglePlayerFragment_to_gameFragment)
            }
            alert.setNegativeButton("Restart") { dialog, which ->
                myScore = 0
                qn = 1
                progressBar!!.progress = 0
                mScore!!.text = "Score: " + myScore
                questionnumber!!.text = qn.toString() + "/" + 5 + " Question"

                loadQuestions()
            }
            alert.show()
        }
        else
            loadQuestions()

        qn = qn + 1
        if (qn <= 5) {
            questionnumber!!.text = qn.toString() + "/" + 5 + " Question"
        }
        mScore!!.text = "Score: " + myScore
        progressBar!!.incrementProgressBy(PROGRESS_BAR)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    fun checkwinner(){

    }


    fun reset(): Boolean {
        return true
    }

    fun makeAnswer(k: Int) {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.POST, url + "?req=" + ANSWER + "&who=" + who + "&answer=" + k.toString(),
            { response ->
                // Display the first 500 characters of the response string.
                reply = JSONObject(response.toString())
                Log.i("info", "response: " + reply?.toString(2))
            },
            { error: VolleyError? ->
                // Log.i("info", "makeMove: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }


    // Function to check the answers of the players
    fun getAnswer() {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url + "req=" + POLLING + "&who=" + who,
            { response ->
                // Display the first 500 characters of the response string.
                reply = JSONObject(response.toString())
                val k = reply!!.getInt("answer")
                val who = reply!!.getInt("who")

                val answers = arrayOf(optionA?.text.toString(), optionB?.text.toString(), optionC?.text.toString(), optionD?.text.toString())

                checkAnswer(answers[k], who)

                if ((who != 0) and (who != 1)) Log.i("info", "who out of range")

                Log.i("info", "response: " + reply?.toString(2))

                invalidate()

            },
            { error: VolleyError? ->
                Log.i("info", "Polling: " + error.toString())
            })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun poll (){
        if (!Configuration.canPoll) return
        if (winner != -1) return
        Handler(Looper.getMainLooper()).postDelayed({
            getAnswer()
            poll()
        },pollingperiod)

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onFlushCompleted(p0: Sensor?) {
        TODO("Not yet implemented")
    }


}
