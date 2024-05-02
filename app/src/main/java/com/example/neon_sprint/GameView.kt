package com.example.neon_sprint

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.MotionEvent
import android.view.View
import kotlin.collections.ArrayList
import kotlin.random.Random

class GameView(var c: Context, var gameTask: GameTask) : View(c) {
    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myPlayerPosition = 0
    private val otherPlayers = ArrayList<HashMap<String, Any>>()
    private var viewWidth = 0
    private var viewHeight = 0

    init {
        myPaint = Paint()
    }

    private fun addParticles(x: Float, y: Float, color: Int) {
        val particle = ShapeDrawable(OvalShape())
        particle.paint.color = color
        particle.setBounds(x.toInt(), y.toInt(), (x + 10).toInt(), (y + 10).toInt())

        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewHeight = this.measuredHeight
        viewWidth = this.measuredWidth

        // Update background color
        val bgColor = time % 360
        canvas.drawColor(Color.HSVToColor(floatArrayOf(bgColor.toFloat(), 1.0f, 1.0f)))

        if (time % 700 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherPlayers.add(map)
        }
        time = time + 10 + speed
        val playerWidth = viewWidth / 5
        val playerHeight =  playerWidth + 10
        myPaint!!.style = Paint.Style.FILL
        val gemDrawable = resources.getDrawable(R.drawable.gem, null)

        gemDrawable.setBounds(
            myPlayerPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - playerHeight,
            myPlayerPosition * viewWidth / 3 + viewWidth / 15 + playerWidth - 25,
            viewHeight - 2
        )
        gemDrawable.draw(canvas!!)
        myPaint!!.color = Color.GREEN
        var highScore = 0

        for (i in otherPlayers.indices){
            try {
                val playerLane = otherPlayers[i]["lane"] as Int
                val playerX = playerLane * viewWidth / 3 + viewWidth / 15
                var gemPosition = time - otherPlayers[i]["startTime"] as Int
                val playerDrawable = resources.getDrawable(R.drawable.player, null)

                playerDrawable.setBounds(
                    playerX + 25, gemPosition - playerHeight, playerX + playerWidth - 25, gemPosition
                )
                playerDrawable.draw(canvas)

                if (playerLane == myPlayerPosition){
                    if (gemPosition > viewHeight - 2 - playerHeight && gemPosition < viewHeight - 2){
                        gameTask.closeGame(score)
                    }
                    if (gemPosition > viewHeight - 2 - playerHeight && gemPosition < viewHeight - 2){
                        addParticles(playerX.toFloat(), (gemPosition - playerHeight).toFloat(), Color.YELLOW) // Add yellow particles at gem position
                    }
                }

                if (gemPosition > viewHeight + playerHeight) {
                    otherPlayers.removeAt(i)
                    score++
                    speed = 1 + score / 8
                    if (score > highScore) {
                        highScore = score
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score :$score", 80f, 80f, myPaint!!)
        canvas.drawText("Speed :$speed", 380f, 80f, myPaint!!)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                if (x1 < viewWidth / 2) {
                    if (myPlayerPosition > 0) {
                        myPlayerPosition--
                    }
                }
                if (x1 > viewWidth / 2) {
                    if (myPlayerPosition < 2) {
                        myPlayerPosition++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {}
        }
        return true
    }
}