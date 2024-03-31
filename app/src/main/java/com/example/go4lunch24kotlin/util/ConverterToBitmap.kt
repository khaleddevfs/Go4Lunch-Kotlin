package com.example.go4lunch24kotlin.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

class ConverterToBitmap {
    companion object {
        fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
            var drawable = ContextCompat.getDrawable(context, drawableId)
            drawable = DrawableCompat.wrap(drawable!!).mutate()

            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }
    }
}
