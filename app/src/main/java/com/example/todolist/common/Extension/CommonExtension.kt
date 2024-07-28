package com.example.todolist.common.Extension

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.example.todolist.R
import com.example.todolist.common.utils.ProgressDialog

fun TextView.setClickableSpan(
        text: String,
        underline: Boolean = true,
        vararg clickableItems: Pair<String, () -> Unit>
    ) {
        val spannableString = SpannableString(text)

        // Create and set clickable spans for each clickable item
        for (item in clickableItems) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    item.second.invoke() // Execute the provided click action
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color =
                        context.resources.getColor(R.color.medium) // Set the text color using a color resource
                    ds.isUnderlineText = underline // Disable underline
                }
            }

            val start = text.indexOf(item.first)
            val end = start + item.first.length

            // Set clickable spans for each item
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Set the text with clickable spans
        this.text = spannableString

        // Enable link movement
        movementMethod = LinkMovementMethod.getInstance()
    }

fun Context.hideProgressDialog(){
    ProgressDialog.instance?.dismiss()
}
fun Context.showProgressDialog(){
    ProgressDialog.instance?.show(this)
}

fun runDelayedOnMainThread(delayMillis: Long, task: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        task()
    }, delayMillis)
}