package com.harsh.neontap

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import kotlin.random.Random

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(NeonGameView(this))
    }
}

class NeonGameView(activity: Activity) : View(activity) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private var score = 0
    private var timeLeft = 30
    private var started = false
    private var ended = false
    private var x = 0f
    private var y = 0f
    private val radius = 58f

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    private val timer = object : Runnable {
        override fun run() {
            if (!started || ended) return
            timeLeft--
            if (timeLeft <= 0) {
                timeLeft = 0
                ended = true
            }
            invalidate()
            if (!ended) handler.postDelayed(this, 1000)
        }
    }

    private fun moveTarget() {
        val margin = radius + 15
        x = Random.nextFloat() * (width - 2 * margin).coerceAtLeast(1f) + margin
        y = Random.nextFloat() * (height - 2 * margin).coerceAtLeast(1f) + margin
    }

    private fun startGame() {
        score = 0
        timeLeft = 30
        started = true
        ended = false
        moveTarget()
        handler.removeCallbacks(timer)
        handler.postDelayed(timer, 1000)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        moveTarget()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.rgb(6, 9, 24))

        p.color = Color.WHITE
        p.textAlign = Paint.Align.CENTER
        p.textSize = 48f
        canvas.drawText("NEON TAP", width / 2f, 85f, p)

        p.textSize = 30f
        canvas.drawText("Score: $score    Time: $timeLeft", width / 2f, 140f, p)

        if (!started || ended) {
            p.textSize = 36f
            canvas.drawText(
                if (ended) "TIME UP!" else "TAP TO START",
                width / 2f, height / 2f, p
            )
            p.textSize = 22f
            canvas.drawText(
                if (ended) "Tap anywhere to play again" else "Hit the glowing circle",
                width / 2f, height / 2f + 42f, p
            )
        } else {
            p.color = Color.CYAN
            p.style = Paint.Style.STROKE
            p.strokeWidth = 12f
            canvas.drawCircle(x, y, radius, p)

            p.style = Paint.Style.FILL
            canvas.drawCircle(x, y, radius * 0.65f, p)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (!started || ended) {
                startGame()
                return true
            }

            val dx = event.x - x
            val dy = event.y - y

            if (dx * dx + dy * dy <= radius * radius) {
                score++
                moveTarget()
                invalidate()
            }
            return true
        }
        return true
    }
}
