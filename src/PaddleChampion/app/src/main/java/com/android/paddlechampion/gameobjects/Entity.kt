package com.android.paddlechampion.gameobjects

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import kotlin.math.pow
import kotlin.math.sqrt

open class Entity(val sprite: MutableList<Bitmap>, var index: Int, var x: Int, var y: Int) {
    // animation
    var image_index: Double = 0.0
    var image_speed: Double = 0.0
    var image_number: Double = sprite.size.toDouble()
    val xstart = x
    val ystart = y
    var w: Int = sprite[0].width
    var h: Int = sprite[0].height
    lateinit var image: Bitmap
    // basic variables
    var position: Array<Double> = arrayOf(0.0,0.0)
    var velocity: Array<Double> = arrayOf(0.0,0.0)
    var speed_max: Double = 0.0
    var rect: Rect
    var bounds: Rect
    val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    init {
        position[0] = x.toDouble()
        position[1] = y.toDouble()
        rect = Rect(x-w/2,y-h/2,x+w/2,y+h/2) // rect is the bounding box for the entity
        bounds = Rect(w/2,h/2,screenWidth-w/2,screenHeight-h/2) // bounds is the area that the entity is allowed inside of
    }

    open fun update() {
        // velocity check
        limit_velocity()

        // collision detection
//        checkCollisions()

        // update position
        position[0] += velocity[0]
        position[1] += velocity[1]

        // commit new position
        x = position[0].toInt()
        y = position[1].toInt()

        //update image
        image_index += image_speed
        image = sprite[(image_index%image_number).toInt()]
        // update rect for collision purposes
        rect = Rect(x-w/2,y-h/2,x+w/2,y+h/2)
    }

    open fun draw(canvas: Canvas) {
        // draw the assigned sprite at current image frame
        canvas.drawBitmap(image, rect.left.toFloat(), rect.top.toFloat(), null)
    }

    fun limit_velocity(){
        val speed = sqrt(velocity[0].pow(2.0) + velocity[1].pow(2.0))
        if(speed > speed_max){
            val uv = arrayOf(velocity[0]/speed, velocity[1]/speed)
            velocity[0] = uv[0]*speed_max
            velocity[1] = uv[1]*speed_max
        }
    }
    open fun checkCollisions(){
        var xPos = position[0].toInt()
        var yPos = position[1].toInt()
        if(xPos < bounds.left){
            position[0] = (bounds.left+1).toDouble()
            if(velocity[0] < 0) velocity[0] = velocity[0]*-1
        }
        if(xPos > bounds.right) {
            position[0] = (bounds.right-1).toDouble()
            if(velocity[0] > 0) velocity[0] = velocity[0]*-1
        }
        if(yPos < bounds.top){
            position[1] = (bounds.top+1).toDouble()
            if(velocity[1] < 0) velocity[1] = velocity[1]*-1
        }
        if(yPos > bounds.bottom) {
            position[1] = (bounds.bottom-1).toDouble()
            if(velocity[1] > 0) velocity[1] = velocity[1]*-1
        }
    }
}