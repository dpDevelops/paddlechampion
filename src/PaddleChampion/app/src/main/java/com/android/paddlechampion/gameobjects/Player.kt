package com.android.paddlechampion.gameobjects

import android.graphics.Bitmap
import android.util.Log
import com.android.paddlechampion.GameView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.pow

private const val TAG = "PLAYER"

class Player(val gameView: GameView, spr: MutableList<Bitmap>, index: Int, xstart: Int, ystart: Int):
        Entity(spr, index, xstart, ystart-spr[0].height/2){
    init {
        speed_max = 14.0
        image_speed = 0.1
        val rect = gameView.playspace
        bounds.left = rect.left + w/2
        bounds.top = rect.top + h/2
        bounds.right = rect.right - w/2
        bounds.bottom = rect.bottom - h/2
    }

    override fun update() {
        // respond to player input
        val movement = gameView.input_movement
        velocity[0] = movement[0]*speed_max
        velocity[1] = movement[1]*speed_max

        // normal entity update
        super.update()
    }
    override fun checkCollisions(){
        val entities = gameView.entities
        val posX = position[0].toInt()
        val posY = position[1].toInt()

        // collision with boundary
        if(posX < bounds.left){
            position[0] = (bounds.left+1).toDouble()
            velocity[0] = max(0.0, velocity[0])
            if(velocity[0] < 0.0) velocity[0] = 0.0
        } else if(posX > bounds.right) {
            position[0] = (bounds.right).toDouble()
            if(velocity[0] > 0.0) velocity[0] = 0.0
        }
        if(posY < bounds.top){
            position[1] = (bounds.top).toDouble()
            if(velocity[1] < 0.0) velocity[1] = 0.0
        } else if(posX > bounds.bottom) {
            position[1] = (bounds.bottom).toDouble()
            if(velocity[1] > 0.0) velocity[1] = 0.0
        }

        // collision with balls
        for(i in 1 until entities.size) {
            val entity = entities[i]
            var col = false
            if (entity is Ball) {
                val other_rect = entity.rect
                val cornerDistL = sqrt((entity.x-rect.left).toFloat().pow(2f) + (entity.y-rect.top).toFloat().pow(2f))
                val cornerDistR = sqrt((entity.x-rect.right).toFloat().pow(2f) + (entity.y-rect.top).toFloat().pow(2f))

// collision order
                /* colTypes
                    0 = ball is above the paddle
                    1 = ball is above the ground
                    2 = ball is above the corner of paddle (extra collision calculation)
                 */
                val relativePos = x - entity.x;
                val paddleHalfWidth = w/2;
                var collisionY = y-(h/2)-entity.radius
                var colType = if(abs(relativePos) > paddleHalfWidth){ 1 } else { 0 }
                if(colType == 1 && relativePos-paddleHalfWidth <= entity.radius) colType = 2
                when(colType) {
                    0 -> { // ball is above the paddle
                        if(entity.y > collisionY){
                            val magnitude = 0.6f// entity.speed_max/2   // max(3f, abs(velocity[0]).toFloat())
                            val dx = (entity.x-x).toFloat()
                            val dy = (entity.y-y).toFloat()
                            var dist = sqrt(dx.pow(2f)+dy.pow(2f))
                            val uv = arrayOf(dx/dist, dy/dist) // unit vector pointing at the ball from the paddle
                            val uvVel = arrayOf((entity.velocity[0]/entity.speed_max).toFloat(), (-entity.velocity[1]/entity.speed_max).toFloat()) // unit vector of ball's velocity ,y-velocity is inverted to get the velocity after bouncing
                            uv[0] = uv[0]*magnitude + uvVel[0]
                            uv[1] = uv[1]*magnitude + uvVel[1]
                            dist = sqrt(uv[0].pow(2f)+uv[1].pow(2f))
                            uv[0] = (uv[0]/dist)*entity.speed_max.toFloat()
                            uv[1] = uv[1]/dist*entity.speed_max.toFloat()

                            entity.velocity[0] = uv[0].toDouble()
                            entity.velocity[1] = uv[1].toDouble()

                            entity.position[1] = collisionY.toDouble()
                            entity.y = entity.position[1].toInt()

                            gameView.gameData.playerScoreStreak = 0
                            gameView.soundBox.play(4)
                        }
                    }
                    1 -> { // ball is above the ground
                        // do nothing, the ball will do this check
                    }
                    2 -> { // ball is above corner of paddle

                    }
                }
            }
        }
    }
}