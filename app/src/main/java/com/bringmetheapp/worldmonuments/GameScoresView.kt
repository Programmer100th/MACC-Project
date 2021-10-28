package com.bringmetheapp.worldmonuments

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_single_site.view.*
import org.json.JSONException

class GameScoresView(context: Context?) : View(context) {

    /*val nicknameList = args.nicknameList
    val gamesWonList = args.gamesWonList
    val gamesLostList = args.gamesLostList*/

    var dx = 0f //Distance among vertical lines
    var dy = 0f //Distance among horizontal lines

    val vLines = 3f
    val hLines = 11f

    var r: RectF = RectF()

    val mPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#FFFFFF")
        textSize = 100f
    }

    val whitePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        strokeWidth = 2f
        textSize = 50f
    }

    init {
        Log.d("init", Configuration.nicknameList.toString())

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        dx = width / vLines
        dy = height / hLines

        r.bottom = dy
        r.top = height.toFloat()
        r.left = 0f
        r.right = width.toFloat()
        canvas.drawRect(r, mPaint)

        //Draw  horizontal lines
        for (y in 0..hLines.toInt()) {
            canvas.drawLine(
                0f, height - y * dy,
                width.toFloat(), height - y * dy,
                whitePaint
            )
        }
        //Draw vertical lines
        for (x in 0..vLines.toInt()) {
            canvas.drawLine(
                x * dx, dy,
                x * dx, height.toFloat(),
                whitePaint
            )
        }

        drawScores(canvas)

    }

    fun drawScores(canvas: Canvas) {
        var elem = ""

        var rr = Rect()

        var maxY = 9

        var nX = 0f

        if (Configuration.nicknameList.size < 10)
            maxY = Configuration.nicknameList.size - 1

        for(x in 0..2){
            if(x == 0){
                elem = "Nickname"
            }
            else if(x == 1){
                elem = "Games Won"
            }
            else{
                elem = "Games Lost"
            }

            whitePaint.getTextBounds(elem, 0, 1, rr)
            val offx = (rr.right - rr.left) / 2
            val offy = (rr.top - rr.bottom) / 2

            whitePaint.color = Color.parseColor("#FF8C00")

            canvas.drawText(
                elem,
                x * dx + 3 * offx,
                dy - dy / 2 - offy,
                whitePaint
            )

        }

        whitePaint.color = Color.parseColor("#000000")

        for (y in 0..maxY) {
            for (x in 0..2) {
                if (x == 0) {
                    elem = Configuration.nicknameList[y]
                    whitePaint.getTextBounds(elem, 0, 1, rr)
                    val offx = (rr.right - rr.left) / 2
                    nX = x * dx + 3 * offx
                }
                else if (x == 1) {
                    elem = Configuration.gamesWonList[y].toString()
                    whitePaint.getTextBounds(elem, 0, 1, rr)
                    val offx = (rr.right - rr.left) / 2
                    nX = x * dx + dx / 2 - offx
                }
                else {
                    elem = Configuration.gamesLostList[y].toString()
                    whitePaint.getTextBounds(elem, 0, 1, rr)

                    val offx = (rr.right - rr.left) / 2
                    nX = x * dx + dx / 2 - offx
                }

                val offy = (rr.top - rr.bottom) / 2

                canvas.drawText(
                    elem,
                    nX,
                    (y + 2) * dy - dy / 2 - offy,
                    whitePaint
                )
            }
        }
    }
}