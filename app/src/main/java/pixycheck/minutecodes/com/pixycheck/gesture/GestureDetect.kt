package pixycheck.minutecodes.com.pixycheck.gesture

import android.app.Activity
import android.view.GestureDetector
import android.view.MotionEvent
import pixycheck.minutecodes.com.pixycheck.`interface`.DoubleTapEventForwarder


open class GestureDetect(context: Activity) : GestureDetector.SimpleOnGestureListener() {
    private val tapListener = context as DoubleTapEventForwarder

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        tapListener.onDoubleTapped()
        return super.onDoubleTap(e)
    }
}