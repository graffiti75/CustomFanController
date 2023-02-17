package com.example.android.customfancontroller

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private enum class FanSpeed(val label: Int) {
	OFF(R.string.fan_off),
	LOW(R.string.fan_low),
	MEDIUM(R.string.fan_medium),
	HIGH(R.string.fan_high);
}

private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

class DialView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

	private var radius = 0.0f                   // Radius of the circle.
	private var fanSpeed = FanSpeed.OFF         // The active selection.
	// position variable which will be used to draw label and indicator circle position
	private val pointPosition: PointF = PointF(0.0f, 0.0f)

	private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		style = Paint.Style.FILL
		textAlign = Paint.Align.CENTER
		textSize = 55.0f
		typeface = Typeface.create( "", Typeface.BOLD)
	}

	private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
		style = Paint.Style.FILL
		textAlign = Paint.Align.CENTER
		textSize = 55.0f
		typeface = Typeface.create( "", Typeface.BOLD)
		color = Color.BLUE
	}

	override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
		Log.i("dial", "----- onSizeChanged()")
		radius = (min(width, height) / 2.0 * 0.8).toFloat()
		Log.i("dial", "onSizeChanged() -> width : $width, height : $height")
		Log.i("dial", "onSizeChanged() -> radius : $radius")
	}

	private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
		Log.i("dial", "\t----- computeXYForSpeed(${pos.ordinal})")
		Log.i("dial", "\tcomputeXYForSpeed() -> pos: $pos, radius: $radius")

		// Angles are in radians.
		val startAngle = Math.PI * (9 / 8.0)
		val offset = pos.ordinal * (Math.PI / 4)
		val angle = startAngle + offset
		val radiusX = radius * cos(angle)
		val radiusY = radius * sin(angle)
		x = radiusX.toFloat() + width / 2
		y = radiusY.toFloat() + height / 2

		Log.i("dial", "\tcomputeXYForSpeed() -> radiusX: $radiusX")
		Log.i("dial", "\tcomputeXYForSpeed() -> radiusY: $radiusY")
		Log.i("dial", "\tcomputeXYForSpeed() -> offset: ${offset.radiusToDegrees()}")
		Log.i("dial", "\tcomputeXYForSpeed() -> startAngle: ${startAngle.radiusToDegrees()}," +
			" angle: ${angle.radiusToDegrees()}")
		Log.i("dial", "\tcomputeXYForSpeed() -> cos(angle): ${cos(angle)}," +
				" sin(angle): ${sin(angle)}")
		Log.i("dial", "\tcomputeXYForSpeed() -> x: $x, y: $y")
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)

		Log.i("dial", "onDraw()")

		// Set dial background color to green if selection not off.
		paint.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN

		// Draw the dial.
		Log.i("dial", "----- onDraw() -> DRAW THE DIAL")
		Log.i("dial", "onDraw() -> width : $width, height : $height")
		Log.i("dial", "onDraw() -> width/2 : ${width / 2}, height/2 : ${height / 2}")
		Log.i("dial", "onDraw() -> radius : $radius")
		canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
		canvas.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), paintLine)

		// Draw the indicator circle.
		val markerRadius = radius + RADIUS_OFFSET_INDICATOR
		pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
		paint.color = Color.BLACK
		Log.i("dial", "----- onDraw() -> DRAW THE INDICATOR CIRCLE")
		Log.i("dial", "onDraw() -> canvas.drawCircle()")
		Log.i("dial", "onDraw() -> x: ${pointPosition.x}, y: ${pointPosition.y}")
		Log.i("dial", "onDraw() -> radius: $radius")
		Log.i("dial", "onDraw() -> radius/12: ${(radius / 12)}")
		canvas.drawCircle(pointPosition.x, pointPosition.y, radius / 12, paint)

		// Draw the text labels.
		val labelRadius = radius + RADIUS_OFFSET_LABEL
		Log.i("dial", "----- onDraw() -> DRAW THE TEXT LABELS")
		Log.i("dial", "onDraw() -> labelRadius: $labelRadius")
		for (i in FanSpeed.values()) {
			pointPosition.computeXYForSpeed(i, labelRadius)
			val label = resources.getString(i.label)
			Log.i("dial", "onDraw() -> canvas.drawText")
			canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
			Log.i("dial", "onDraw() -> x : ${pointPosition.x}, y : ${pointPosition.y}")
		}
	}

	// Angle in Degrees = Angle in Radians × 180°/π
	private fun Double.radiusToDegrees(): Double = this * 180 / Math.PI
}