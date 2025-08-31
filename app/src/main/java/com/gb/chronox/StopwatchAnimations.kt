// Animation Helper Functions for MainActivity.kt

import android.animation.*
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.gb.chronox.R

class StopwatchAnimations(private val context: android.content.Context) {

    // Animate the timer card entrance
    fun animateTimerCardEntrance(timerCard: View, delay: Long = 0) {
        val slideIn = AnimationUtils.loadAnimation(context, R.anim.card_enter_animation)
        slideIn.startOffset = delay
        timerCard.startAnimation(slideIn)
    }

    // Animate buttons entrance with staggered effect
    fun animateButtonsEntrance(buttons: List<View>) {
        buttons.forEachIndexed { index, button ->
            val animation = AnimationUtils.loadAnimation(context, R.anim.button_enter_animation)
            animation.startOffset = 300L + (index * 150L) // Staggered animation
            button.startAnimation(animation)
        }
    }

    // Pulse animation for the timer when running
    fun startTimerPulse(pulseView: View) {
        pulseView.visibility = View.VISIBLE
        val animation = AnimationUtils.loadAnimation(context, R.anim.timer_pulse_animation)
        pulseView.startAnimation(animation)
    }

    // Stop pulse animation
    fun stopTimerPulse(pulseView: View) {
        pulseView.clearAnimation()
        pulseView.visibility = View.INVISIBLE
    }

    // Animate number change in timer
    fun animateNumberChange(textView: TextView) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.number_counter_animation)
        textView.startAnimation(animation)
    }

    // Button click feedback animation
    fun animateButtonClick(button: View, onComplete: () -> Unit = {}) {
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat("scaleX", 1f, 0.9f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 0.9f)
        ).apply {
            duration = 100
        }

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1f)
        ).apply {
            duration = 100
        }

        AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onComplete()
                }
            })
            start()
        }
    }

    // Shake animation for invalid actions
    fun shakeView(view: View) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        shake.duration = 500
        shake.start()
    }

    // Color transition animation for timer text
    fun animateColorTransition(textView: TextView, fromColor: Int, toColor: Int) {
        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimator.duration = 300
        colorAnimator.addUpdateListener { animator ->
            textView.setTextColor(animator.animatedValue as Int)
        }
        colorAnimator.start()
    }

    // Reset animation - scale and fade
    fun animateReset(views: List<View>, onComplete: () -> Unit = {}) {
        val animators = views.map { view ->
            ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 1f, 0.7f, 1f)
            ).apply {
                duration = 400
                interpolator = android.view.animation.BounceInterpolator()
            }
        }

        AnimatorSet().apply {
            playTogether(animators)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onComplete()
                }
            })
            start()
        }
    }

    // Floating Action Button reveal animation
    fun animateFabReveal(fab: View) {
        fab.apply {
            scaleX = 0f
            scaleY = 0f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(android.view.animation.OvershootInterpolator())
                .start()
        }
    }
}