package com.ironlog.app.ui.feature.summary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.compose.ui.graphics.toArgb
import com.ironlog.app.ui.theme.regionColor

/**
 * M5-T5.3 share card drawn with a plain [View] so it renders identically off-screen
 * (AndroidView + View.draw fallback from docs/audit/M0.md; GraphicsLayer is not testable here).
 */
class ShareCardView(context: Context) : View(context) {

    var data: ShareCardData? = null
        set(value) {
            field = value
            invalidate()
        }

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#201A18")
        textSize = 56f
        isFakeBoldText = true
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#534341")
        textSize = 34f
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#201A18")
        textSize = 42f
    }
    private val exercisePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#201A18")
        textSize = 36f
    }
    private val prPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B3261E")
        textSize = 36f
        isFakeBoldText = true
    }
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F5DDDA")
        strokeWidth = 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(CARD_WIDTH, CARD_HEIGHT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#FBF8FF"))
        val card = data ?: return

        var y = 110f
        titlePaint.color = Color.parseColor("#24104F")
        labelPaint.color = Color.parseColor("#514A58")
        valuePaint.color = Color.parseColor("#1C1A20")
        dividerPaint.color = Color.parseColor("#E9E1F2")
        prPaint.color = Color.parseColor("#7652D8")
        canvas.drawText(card.appName, MARGIN, y, titlePaint)
        y += 60f
        canvas.drawText(card.date, MARGIN, y, labelPaint)
        y += 80f
        canvas.drawLine(MARGIN, y, CARD_WIDTH - MARGIN, y, dividerPaint)
        y += 60f
        canvas.drawText("TIME", MARGIN, y, labelPaint)
        canvas.drawText(card.durationText, MARGIN + 180f, y, valuePaint)
        y += 60f
        canvas.drawText("VOLUME", MARGIN, y, labelPaint)
        canvas.drawText(card.volumeText, MARGIN + 180f, y, valuePaint)
        y += 80f
        canvas.drawLine(MARGIN, y, CARD_WIDTH - MARGIN, y, dividerPaint)
        y += 60f

        card.exercises.take(MAX_EXERCISES).forEach { exercise ->
            canvas.drawText(exercise.name, MARGIN, y, exercisePaint)
            canvas.drawText(exercise.bestSetText, MARGIN + 520f, y, exercisePaint)
            if (exercise.isPr) {
                canvas.drawText("PR", MARGIN + 860f, y, prPaint)
            }
            y += 52f
        }

        // PRD R-4.2 region colour bar at the bottom.
        val barTop = CARD_HEIGHT - 120f
        val barHeight = 18f
        card.bodyRegions.take(4).forEachIndexed { index, region ->
            val left = MARGIN + index * 140f
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = regionColor(region).toArgb() }
            canvas.drawRoundRect(RectF(left, barTop, left + 120f, barTop + barHeight), 9f, 9f, paint)
        }
    }

    companion object {
        const val CARD_WIDTH = 1080
        const val CARD_HEIGHT = 1350
        private const val MARGIN = 64f
        private const val MAX_EXERCISES = 12
    }
}
