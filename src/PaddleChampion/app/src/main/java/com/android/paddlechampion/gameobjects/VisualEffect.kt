package com.android.paddlechampion.gameobjects

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.rotationMatrix

open class VisualEffect(val sprite: MutableList<Bitmap>, var image_speed: Double, var image_angle: Float, val image_loop: Int = -1, var x: Int, var y: Int) {
    // animation
    var image_index: Double = 0.0
    var image_number: Double = sprite.size.toDouble()
    var wHalf: Int = 0
    var hHalf: Int = 0
    var complete: Boolean = false
    var velocity: Array<Double> = arrayOf(0.0,0.0)
    lateinit var image: Bitmap

    init {
        wHalf = sprite[0].width / 2
        hHalf = sprite[0].height / 2
        for(i in 0 until sprite.size){
            sprite[i] = Bitmap.createBitmap(sprite[i], 0, 0, sprite[i].width, sprite[i].height, rotationMatrix(image_angle, wHalf.toFloat(), hHalf.toFloat()), true)
        }
    }

    open fun update(): Boolean {
        //update image
        x += velocity[0].toInt()
        y += velocity[1].toInt()
        image_index += image_speed
        image = sprite[(image_index%image_number).toInt()]
        if(image_loop != -1 && (image_index / image_number).toInt() >= image_loop) complete = true
        return complete
    }
    fun draw(canvas: Canvas){
        // draw the assigned sprite at current image frame
        canvas.drawBitmap(image, (x-wHalf).toFloat(), (y-hHalf).toFloat(), null)
    }
    fun setVelocity(xVel: Double, yVel: Double){
        velocity[0] = xVel
        velocity[1] = yVel
    }
}