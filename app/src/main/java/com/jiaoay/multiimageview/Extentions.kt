package com.jiaoay.multiimageview

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.sqrt

val mainScope = CoroutineScope(Dispatchers.Main)

val Float.dp2px: Float
    get() {
        val displayMetrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            displayMetrics
        )
    }

val Int.dp2px: Int
    get() {
        val displayMetrics = Resources.getSystem().displayMetrics
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            displayMetrics
        ).toInt()
    }

val Context.x2Activity: AppCompatActivity?
    get() {
        if (this is AppCompatActivity) {
            return this
        }

        if (this is ContextWrapper) {
            val baseContext = this.baseContext
            if (baseContext is AppCompatActivity) {
                return baseContext
            }
        }

        return null
    }

val Context.scope: CoroutineScope
    get() {
        return x2Activity?.lifecycleScope ?: mainScope
    }

val Context.scopeOrNull: CoroutineScope?
    get() {
        return x2Activity?.lifecycleScope
    }

suspend fun Context?.loadImageUri(
    uri: Uri,
    width: Int,
    height: Int,
) = suspendCancellableCoroutine { continuation ->
    val context: Context = this ?: let {
        continuation.safeResume(null)
        return@suspendCancellableCoroutine
    }
    Glide.with(context)
        .asDrawable()
        .load(uri)
        .override(width, height)
        .priority(Priority.NORMAL)
        .into(
            object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    continuation.safeResume(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    continuation.safeResume(null)
                }
            }
        )
}

private fun <T> CancellableContinuation<T>.safeResume(value: T) {
    val continuation = this
    if (continuation.isActive.not() || continuation.isCancelled) {
        return
    }
    continuation.resume(value)
}


private var sqrtCache: Pair<Int, Int> = Pair(1, 1)

fun Int.cacheSqrt(): Int {
    if (this == sqrtCache.first) {
        return sqrtCache.second
    }

    val sqrtNum = this.sqrt()
    sqrtCache = this to sqrtNum
    return sqrtNum
}

private var perfectSquareCache = 1

fun Int.sqrt(): Int {
    return sqrt(toDouble()).toInt()
}

fun Int.isPerfectSquare(): Boolean {
    if (this == perfectSquareCache) {
        return true
    }
    val num = this
    val squareRoot = sqrt(num.toDouble())
    val roundedSquareRoot = squareRoot.toInt()

    val isPerfectSquare = roundedSquareRoot * roundedSquareRoot == num
    if (isPerfectSquare) {
        perfectSquareCache = num
    }
    return isPerfectSquare
}