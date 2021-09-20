package com.conexa.challenge.view

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.conexa.challenge.R

class Modal(context: Context) : LinearLayout(context) {

    private val dialog: Dialog = Dialog(context)
    private var onButtonClick: (() -> Unit)? = null

    fun show() {
        dialog.show()
    }

    fun setIcon(icon: Drawable?) {
        dialog.findViewById<ImageView>(R.id.modal_image)?.setImageDrawable(icon)
    }

    fun setTitle(title: String?) {
        dialog.findViewById<TextView>(R.id.modal_title)?.text = title
    }

    fun setMessage(detail: String?) {
        dialog.findViewById<TextView>(R.id.modal_message)?.text = detail
    }

    fun setButton(button: String?) {
        dialog.findViewById<AppCompatButton>(R.id.modal_button)?.apply {
            text = button
            setOnClickListener { _ ->
                dialog.dismiss()
                onButtonClick?.invoke()
            }
        }
    }

    fun setOnClickListener(onClick: () -> Unit) {
        onButtonClick = onClick
    }

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.modal)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}