package com.jalasoft.routesapp.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import com.jalasoft.routesapp.R

class CustomProgressDialog(context: Context) {
    private var dialog: CustomDialog
    private var cpTitle: TextView
    private var cpCardView: CardView
    private var progressBar: ProgressBar

    fun start(title: String = "") {
        cpTitle.text = title
        dialog.show()
    }

    fun stop() {
        dialog.dismiss()
    }

    init {
        val inflater = (context as Activity).layoutInflater
        val view = inflater.inflate(R.layout.progress_dialog_view, null)

        cpTitle = view.findViewById(R.id.cp_title)
        cpCardView = view.findViewById(R.id.cp_card_view)
        progressBar = view.findViewById(R.id.cp_progress_bar)

        // Card Color
        cpCardView.setCardBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.custom_dialog_background, null))

        // Progress Bar Color
        setColorFilter(
            progressBar.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, R.color.red_color, null)
        )

        // Text Color
        cpTitle.setTextColor(Color.WHITE)

        // Custom Dialog initialization
        dialog = CustomDialog(context)
        dialog.setContentView(view)
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            window?.decorView?.rootView?.setBackgroundResource(R.color.transparent_background)
            window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }
        }
    }
}
