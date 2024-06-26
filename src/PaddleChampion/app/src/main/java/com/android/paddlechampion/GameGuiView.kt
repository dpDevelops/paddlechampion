package com.android.paddlechampion

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.SurfaceHolder
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.pow

class GameGuiView(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameView,
) {
    var jsBase_x: Int = 0
    var jsBase_y: Int = 0
    var jsHat_x: Int = 0
    var jsHat_y: Int = 0
    var jsRect: Rect
    var jsWidth: Int
    var baseRadius: Int = 0
    var altRadius: Int = 0
    var hatRadius: Int = 0
    var bounds: Rect = Rect(0,0,0,0)
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    init {
        // get copy of the gui space
        val rect = gameView.guispace
        bounds.left = rect.left
        bounds.top = rect.top
        bounds.right = rect.right
        bounds.bottom = rect.bottom
        // adjust gui element sizes
        jsBase_x = (bounds.left+bounds.right)/2
        jsBase_y = (bounds.top+bounds.bottom)/2
        jsHat_x = jsBase_x
        jsHat_y = jsBase_y
        baseRadius = (bounds.height().toDouble()*0.4).toInt()
        altRadius = (bounds.height().toDouble()*0.15).toInt()
        hatRadius = (bounds.height().toDouble()*0.35).toInt()
        jsWidth = (bounds.width().toFloat()*0.5f).toInt()
        jsRect = Rect(jsBase_x-jsWidth/2,jsBase_y-altRadius/2,jsBase_x+jsWidth/2,jsBase_y+altRadius/2)
    }

    fun update(){
        if(!gameView.touched){
            if(jsHat_x != jsBase_x) jsHat_x = jsBase_x
            if(jsHat_y != jsBase_y) jsHat_y = jsBase_y
        } else {
            val maxLen = (jsRect.width()/2).toDouble()
            val x0 = jsBase_x.toDouble()
            val y0 = jsBase_y.toDouble()
            val x1 = gameView.touched_xstart.toDouble()
            val y1 = gameView.touched_ystart.toDouble()
            val x2 = gameView.touched_xcurrent.toDouble()
            val y2 = gameView.touched_ycurrent.toDouble()
            val joystick_dist: Double = sqrt((x1-x0).pow(2.0) + (y1-y0).pow(2.0))
            val movement_dist: Double = sqrt((x2-x1).pow(2.0) + (y2-y1).pow(2.0))
            if(joystick_dist <= baseRadius) {
                // animate the joystick if the initial touch action was on top of the joystick area
                val uv = arrayOf((x2-x1)/movement_dist, 0.0)// (y2-y1)/movement_dist)
                val len = min(maxLen, movement_dist)
                jsHat_x = jsBase_x + (uv[0]*len).toInt()
                jsHat_y = jsBase_y + (uv[1]*len).toInt()
                gameView.input_movement = arrayOf(uv[0]*(len/maxLen), uv[1]*(len/maxLen))
            }
        }
    }

    fun draw(canvas: Canvas){
        drawJoystick(canvas)
    }

    private fun drawJoystick(canvas: Canvas){
        if(surfaceHolder.surface.isValid)
        {
            val rect = arrayOf(
                (jsBase_x-baseRadius).toFloat(),
                (jsBase_y-altRadius/2),
                (jsBase_x+baseRadius).toFloat(),
                (jsBase_y+altRadius/2))
            val colors = Paint()

            colors.setARGB(255, 50, 50, 50)
            // base
            canvas.drawCircle(jsBase_x.toFloat(), jsBase_y.toFloat(), (baseRadius).toFloat(), colors)
            // base bar
            canvas.drawRect(jsRect.left.toFloat(),jsRect.top.toFloat(),jsRect.right.toFloat(),jsRect.bottom.toFloat(), colors)
            // left side of base
            canvas.drawCircle(jsRect.left.toFloat(), jsBase_y.toFloat(), altRadius.toFloat(), colors)
            // right side of base
            canvas.drawCircle(jsRect.right.toFloat(), jsBase_y.toFloat(), altRadius.toFloat(), colors)
            // hat
            colors.setARGB(255, 0, 0, 255)
            canvas.drawCircle(jsHat_x.toFloat(), jsHat_y.toFloat(), hatRadius.toFloat(), colors)
        }
    }
}