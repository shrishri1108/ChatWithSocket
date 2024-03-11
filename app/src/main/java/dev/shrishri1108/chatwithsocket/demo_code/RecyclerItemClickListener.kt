package dev.shrishri1108.chatwithsocket.demo_code

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener


class RecyclerItemClickListener(
    context: Context?,
    recyclerView: RecyclerView,
    private val listener: OnItemClickListener?
) :
    OnItemTouchListener {
    private val gestureDetector: GestureDetector

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
        fun onSwipeLeft(view: View?, position: Int)
        fun onSwipeRight(view: View?, position: Int)
    }

    init {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null && listener != null) {
                    listener.onItemClick(child, recyclerView.getChildAdapterPosition(child))
                }
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val deltaX = e2.x - e1!!.x
                val deltaY = e2.y - e1.y
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > 100 && Math.abs(velocityX) > 100) {
                        if (deltaX > 0) {
                            val child = recyclerView.findChildViewUnder(e1.x, e1.y)
                            if (child != null && listener != null) {
                                listener.onSwipeRight(
                                    child,
                                    recyclerView.getChildAdapterPosition(child)
                                )
                            }
                        } else {
                            val child = recyclerView.findChildViewUnder(e1.x, e1.y)
                            if (child != null && listener != null) {
                                listener.onSwipeLeft(
                                    child,
                                    recyclerView.getChildAdapterPosition(child)
                                )
                            }
                        }
                    }
                }
                return false
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        return child != null && gestureDetector.onTouchEvent(e)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}

