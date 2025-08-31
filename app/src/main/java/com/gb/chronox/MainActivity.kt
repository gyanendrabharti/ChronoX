package com.gb.chronox

import StopwatchAnimations
import android.animation.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var timerDisplay: TextView
    private lateinit var millisecondsDisplay: TextView
    private lateinit var startButton: MaterialButton
    private lateinit var stopButton: MaterialButton
    private lateinit var resetButton: MaterialButton
    private lateinit var lapButton: FloatingActionButton
    private lateinit var timerCard: MaterialCardView
    private lateinit var pulseView: View

    private lateinit var animations: StopwatchAnimations
    private var handler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    private var elapsedTime = 0L
    private var isRunning = false

    private val updateTimeTask = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentTime = System.currentTimeMillis()
                elapsedTime = currentTime - startTime
                updateTimerDisplay()
                handler.postDelayed(this, 10) // Update every 10ms for smooth animation
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupAnimations()
        setupClickListeners()
        animateInitialLoad()
    }

    private fun initializeViews() {
        timerDisplay = findViewById(R.id.timerDisplay)
        millisecondsDisplay = findViewById(R.id.millisecondsDisplay)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        resetButton = findViewById(R.id.resetButton)
        lapButton = findViewById(R.id.lapButton)
        timerCard = findViewById(R.id.timerCard)
        pulseView = findViewById(R.id.pulseView)

        animations = StopwatchAnimations(this)
    }

    private fun setupAnimations() {
        // Initially hide views for entrance animation
        timerCard.alpha = 0f
        startButton.alpha = 0f
        stopButton.alpha = 0f
        resetButton.alpha = 0f
        lapButton.scaleX = 0f
        lapButton.scaleY = 0f
    }

    private fun animateInitialLoad() {
        // Animate timer card entrance
        animations.animateTimerCardEntrance(timerCard, 200)
        timerCard.animate().alpha(1f).setDuration(600).start()

        // Animate buttons with stagger
        val buttons = listOf(startButton, stopButton, resetButton)
        animations.animateButtonsEntrance(buttons)

        buttons.forEachIndexed { index, button ->
            button.animate()
                .alpha(1f)
                .setStartDelay(500L + (index * 150L))
                .setDuration(600)
                .start()
        }

        // Animate FAB
        lapButton.postDelayed({
            animations.animateFabReveal(lapButton)
        }, 1200)
    }

    private fun setupClickListeners() {
        startButton.setOnClickListener {
            animations.animateButtonClick(it) {
                startStopwatch()
            }
        }

        stopButton.setOnClickListener {
            animations.animateButtonClick(it) {
                stopStopwatch()
            }
        }

        resetButton.setOnClickListener {
            animations.animateButtonClick(it) {
                resetStopwatch()
            }
        }

        lapButton.setOnClickListener {
            if (isRunning) {
                recordLapTime()
                // Add subtle FAB animation
                it.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(100)
                    .withEndAction {
                        it.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start()
                    }
                    .start()
            } else {
                animations.shakeView(it)
            }
        }
    }

    private fun startStopwatch() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime
            isRunning = true
            handler.post(updateTimeTask)

            // Start pulse animation
            animations.startTimerPulse(pulseView)

            // Update button states with color animation
            animations.animateColorTransition(
                timerDisplay,
                ContextCompat.getColor(this, R.color.timer_text),
                ContextCompat.getColor(this, R.color.start_button)
            )

            startButton.isEnabled = false
            stopButton.isEnabled = true
            resetButton.isEnabled = false
        }
    }

    private fun stopStopwatch() {
        if (isRunning) {
            isRunning = false
            handler.removeCallbacks(updateTimeTask)

            // Stop pulse animation
            animations.stopTimerPulse(pulseView)

            // Reset timer color
            animations.animateColorTransition(
                timerDisplay,
                ContextCompat.getColor(this, R.color.start_button),
                ContextCompat.getColor(this, R.color.timer_text)
            )

            startButton.isEnabled = true
            stopButton.isEnabled = false
            resetButton.isEnabled = true
        }
    }

    private fun resetStopwatch() {
        if (!isRunning) {
            elapsedTime = 0L

            // Animate reset with bounce effect
            animations.animateReset(listOf(timerDisplay, millisecondsDisplay)) {
                updateTimerDisplay()
            }

            startButton.isEnabled = true
            stopButton.isEnabled = false
            resetButton.isEnabled = false
        }
    }

    private fun updateTimerDisplay() {
        val totalMilliseconds = elapsedTime
        val seconds = (totalMilliseconds / 1000) % 60
        val minutes = (totalMilliseconds / (1000 * 60)) % 60
        val hours = (totalMilliseconds / (1000 * 60 * 60)) % 24
        val milliseconds = (totalMilliseconds % 1000) / 10

        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        val millisecondsString = String.format(".%02d", milliseconds)

        // Animate number changes
        if (timerDisplay.text != timeString) {
            animations.animateNumberChange(timerDisplay)
            timerDisplay.text = timeString
        }

        millisecondsDisplay.text = millisecondsString
    }

    private fun recordLapTime() {
        // Animate lap recording
        val lapTime = String.format(
            "%02d:%02d:%02d.%02d",
            (elapsedTime / (1000 * 60 * 60)) % 24,
            (elapsedTime / (1000 * 60)) % 60,
            (elapsedTime / 1000) % 60,
            (elapsedTime % 1000) / 10
        )

        // You can expand this to show lap times in a RecyclerView
        // For now, just animate the FAB feedback
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeTask)
    }
}