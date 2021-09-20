package com.conexa.challenge.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.conexa.challenge.R
import com.google.android.material.snackbar.BaseTransientBottomBar

enum class Type {
    INFO, POSITIVE, WARNING, NEGATIVE
}

open class Toast protected constructor(
    parent: ViewGroup,
    content: View,
    contentViewCallback: ContentViewCallback
) :
    BaseTransientBottomBar<Toast?>(parent, content, contentViewCallback) {

    companion object {
        fun show(
            view: View,
            @StringRes resId: Int,
            type: Type
        ) {
            val toast = create(view, view.resources.getText(resId), type)
            toast.show()
        }

        fun show(
            view: View,
            text: CharSequence,
            type: Type
        ) {
            val toast = create(view, text, type)
            toast.show()
        }

        private fun create(
            view: View,
            text: CharSequence,
            type: Type
        ): Toast {
            val parent = findSuitableParent(view)
                ?: throw IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.")
            val content = ToastLayout(view.context, null)
            content.setType(type)
            content.setText(text)
            val contentViewCallback: ContentViewCallback = object : ContentViewCallback {
                override fun animateContentIn(delay: Int, duration: Int) {
                    content.animate().alpha(1f).setDuration(duration.toLong())
                        .setStartDelay(delay.toLong()).start()
                }

                override fun animateContentOut(delay: Int, duration: Int) {
                    content.animate().alpha(0f).setDuration(duration.toLong())
                        .setStartDelay(delay.toLong()).start()
                }
            }
            val toast = Toast(parent, content, contentViewCallback)
            toast.getView().setPadding(0, 0, 0, 0)
            toast.duration = 3000
            return toast
        }

        private fun findSuitableParent(view: View): ViewGroup? {
            var view: View? = view
            var fallback: ViewGroup? = null
            do {
                if (view is CoordinatorLayout) {
                    return view
                }
                if (view is FrameLayout) {
                    fallback = view
                }
                if (view != null) {
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }
            } while (view != null)
            return fallback
        }

        private class ToastLayout(context: Context, attrs: AttributeSet?) :
            LinearLayout(context, attrs) {

            private val TYPE_INFO = intArrayOf(R.attr.type_info)
            private val TYPE_POSITIVE = intArrayOf(R.attr.type_positive)
            private val TYPE_WARNING = intArrayOf(R.attr.type_warning)
            private val TYPE_NEGATIVE = intArrayOf(R.attr.type_negative)

            private lateinit var type: Type

            override fun onCreateDrawableState(extraSpace: Int): IntArray {
                val drawableState = super.onCreateDrawableState(extraSpace + 1)
                when (type) {
                    Type.INFO -> mergeDrawableStates(drawableState, TYPE_INFO)
                    Type.POSITIVE -> mergeDrawableStates(drawableState, TYPE_POSITIVE)
                    Type.WARNING -> mergeDrawableStates(drawableState, TYPE_WARNING)
                    Type.NEGATIVE -> mergeDrawableStates(drawableState, TYPE_NEGATIVE)
                }
                return drawableState
            }

            fun setType(type: Type) {
                this.type = type
                refreshDrawableState()
            }

            fun setText(text: CharSequence?) {
                val textView = findViewById<TextView>(R.id.toast_text)
                textView.text = text
            }

            fun setText(resId: Int) {
                val textView = findViewById<TextView>(R.id.toast_text)
                textView.text = context.resources.getString(resId)
            }

            init {
                val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                inflater.inflate(R.layout.toast, this, true)
            }
        }
    }
}