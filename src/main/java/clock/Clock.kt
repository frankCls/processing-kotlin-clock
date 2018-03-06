package clock

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PVector


fun main(args: Array<String>) {
    PApplet.main("clock.ClockRunning")
}

class ClockRunning : PApplet() {
    private val sketchWidth = 600f
    private val sketchHeight = 600f
    private val center: PVector = PVector(sketchWidth / 2, sketchHeight / 2)
    private val secondsRadius: Float = (PApplet.min(sketchWidth, sketchHeight) / 2) * 1.7F
    private val minutesRadius: Float = (PApplet.min(sketchWidth, sketchHeight) / 2) * 1.6F
    private val hoursRadius: Float = (PApplet.min(sketchWidth, sketchHeight) / 2) * 1.5F
    private val innerRadius: Float = (PApplet.min(sketchWidth, sketchHeight) / 2) * 1.4F

    private val secondFrameCounter: FrameCounter = FrameCounter()
    private val minuteFrameCounter: FrameCounter = FrameCounter()
    private val hourFrameCounter: FrameCounter = FrameCounter()


    private val secondsCircle: SegmentCircle = SegmentCircle(center, secondsRadius, 60, this, secondFrameCounter)
    private val minutesCircle: SegmentCircle = SegmentCircle(center, minutesRadius, 60, this, minuteFrameCounter)
    private val hoursCircle: SegmentCircle = SegmentCircle(center, hoursRadius, 12, this, hourFrameCounter)

    override fun setup() {
        size(sketchWidth.toInt(), sketchHeight.toInt())
        stroke(255)
        background(0)
    }

    override fun draw() {
        secondsCircle.draw("second")
        minutesCircle.draw("minute")
        hoursCircle.draw("hour")
    }
}


class Segment(private val center: PVector,
              private val radius: Float,
              private val startAngle: Float,
              private val angleWidth: Float,
              private val segmentWidth: Float,
              var color: Triple<Float, Float, Float>,
              private val applet: PApplet) {
    fun draw() {
        val segmentLength = angleWidth - startAngle
        val segmentSpace = PConstants.TWO_PI / 1000
        applet.stroke(0)
        applet.strokeWeight(3f)
        applet.fill(color.first, color.second, color.third)
        applet.arc(center.x, center.y, radius, radius, startAngle + segmentSpace, angleWidth - segmentSpace, PConstants.PIE)
        applet.fill(255)
        applet.arc(center.x, center.y, radius - segmentWidth, radius + segmentWidth, startAngle + segmentLength / 2 + segmentSpace, angleWidth - segmentLength / 2 - segmentSpace, PConstants.PIE)
    }
}

class SegmentCircle(private val center: PVector,
                    private val radius: Float,
                    private val segmentsCount: Int,
                    private val applet: PApplet,
                    private val frameCounter: FrameCounter) {
    private val frameToggler: FrameToggler = FrameToggler()
    private val minuteframeCounter :FrameCounter = FrameCounter(0)
    private val segments: Array<Segment> = Array(segmentsCount,
            { i ->
                val startAngle = PApplet.map(i.toFloat(), 0f, segmentsCount.toFloat(), 0f, PConstants.TWO_PI) - PConstants.HALF_PI
                val angleWidth = PConstants.TWO_PI / segmentsCount
                val red = PApplet.map(i.toFloat(), 0f, segmentsCount.toFloat(), 0f, 255f)
                val blue = PApplet.map(i.toFloat(), 0f, segmentsCount.toFloat(), 255f, 0f)
                val yellow = PApplet.map(i.toFloat(), 0f, segmentsCount.toFloat(), 255f, 0f)
                val color = Triple(red, 0f, blue)
                Segment(center, radius, startAngle, startAngle + angleWidth, radius / 5, color, applet)
            })

    fun draw(timeunit: String) {
        if(minuteframeCounter.count == 60) minuteframeCounter.reset() else minuteframeCounter.update()
        if (getTimeUnit(timeunit) > 0) {
            this.clearClockCircle(center, radius)
            frameCounter.reset()
            for (i in 0 until getTimeUnit(timeunit)) {
//                if (isMinute(timeunit)) {
//                    if (canbeDividedByFive(i) || toggleIsOff()) {
//                        val frameCount = minuteframeCounter.count
//                        val red = PApplet.map(frameCount.toFloat(), 0f, 60f, 0f, segments[i].color.first)
//                        val blue = PApplet.map(frameCount.toFloat(), 0f, 60f, 0f, segments[i].color.third)
//                        segments[i].color = Triple(red, 0f, blue)
                        segments[i].draw()
//                    }
//                } else segments[i].draw()
            }
        }
        //PApplet.println("$timeunit counter : ${getTimeUnit(timeunit)}")
        if (getTimeUnit(timeunit) == 0) {
            clearClockCircle(center, radius)
            for (i in frameCounter.count until segments.size) {
                segments[i].draw()
            }
            frameCounter.update()
        }
        clearClockCircle(center, radius - 20)
    }

    private fun toggleIsOff() = !frameToggler.getValue()

    private fun canbeDividedByFive(i: Int) = (i + 1) % 5 != 0

    private fun isMinute(timeunit: String) = timeunit == "minute"

    private fun getTimeUnit(timeunit: String): Int {
        return (when (timeunit) {
            "second" -> PApplet.second()
            "minute" -> PApplet.minute()
            else -> if (PApplet.hour() > 12) PApplet.hour() - 12 else PApplet.hour()
        })
    }

    private fun clearClockCircle(center: PVector, radius: Float) {
        applet.fill(0)
        applet.ellipse(center.x, center.y, radius, radius)
    }
}

class FrameCounter(var count: Int = 0) {
    fun update() {
        if (count < 60) {
            count += 1
//            println("time frame counter : $count")
        }

    }

    fun reset() {
        count = 0
    }
}

class FrameToggler() {
    fun getValue(): Boolean {
        return PApplet.second() % 2 == 0
    }
}
