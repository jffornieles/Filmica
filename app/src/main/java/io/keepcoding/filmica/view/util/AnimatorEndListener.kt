package io.keepcoding.filmica.view.util

import android.animation.Animator

class AnimatorEndListener (
    val callback: (Animator) -> Unit
): Animator.AnimatorListener {
    override fun onAnimationRepeat(p0: Animator?) {
    }

    override fun onAnimationCancel(p0: Animator?) {
    }

    override fun onAnimationStart(p0: Animator?) {
    }

    override fun onAnimationEnd(p0: Animator) {
        callback.invoke(p0)
    }
}