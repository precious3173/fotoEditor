package com.example.fotoeditor.ui.utils

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedFiniteAnimationSpec
import androidx.compose.animation.core.VectorizedTweenSpec
import androidx.compose.runtime.Immutable


interface DurationBasedAnimationSpec<T>: FiniteAnimationSpec<T> {
    override fun <V : AnimationVector> vectorize(converter: TwoWayConverter<T, V>): VectorizedFiniteAnimationSpec<V>
}

@Immutable
class TweenSpec<T>(
    val durationMillis: Int = AnimationConstants.DefaultDurationMillis,
    val delay: Int = 0,
    val easing: Easing = FastOutSlowInEasing
): DurationBasedAnimationSpec<T> {
    override fun <V : AnimationVector> vectorize(converter: TwoWayConverter<T, V>): VectorizedFiniteAnimationSpec<V> {
        return VectorizedTweenSpec<V>(durationMillis, delay, easing)
    }

    override fun equals(other: Any?): Boolean =
        if (other is TweenSpec<*>) {
            other.durationMillis == this.durationMillis &&
                    other.delay == this.delay &&
                    other.easing == this.easing
        } else {
            false
        }

    override fun hashCode(): Int {
        return (durationMillis * 31 + easing.hashCode()) * 31 + delay
    }
}
