package pwr.mobilne.l3.utils

import android.graphics.Canvas
import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView


class BounceEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(recyclerView.context) {
            var anim: SpringAnimation? = null

            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            private fun handlePull(deltaDistance: Float) {
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationYDelta = sign * recyclerView.width * deltaDistance / 2
                recyclerView.translationY += translationYDelta
                anim?.cancel()
            }

            override fun onRelease() {
                super.onRelease()
                if (recyclerView.translationY != 0f) anim = createAnim()?.also { it.start() }
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationVelocity = (sign * velocity / 2).toFloat()
                anim?.cancel()
                anim = createAnim().setStartVelocity(translationVelocity)?.also { it.start() }
            }

            private fun createAnim() =
                SpringAnimation(recyclerView, SpringAnimation.TRANSLATION_Y)
                    .setSpring(
                        SpringForce()
                            .setFinalPosition(0f)
                            .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                            .setStiffness(SpringForce.STIFFNESS_LOW)
                    )

            override fun draw(canvas: Canvas?): Boolean = false
            override fun isFinished(): Boolean = anim?.isRunning?.not() ?: true
        }
    }
}