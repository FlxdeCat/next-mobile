package edu.bluejack23_1.next.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
class UserThumbnailImageView : AppCompatImageView {

    private val radius = 200.0f
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    override fun onDraw(canvas: Canvas) {
        val clipPath = Path()
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
    }
}