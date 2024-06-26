package com.android.paddlechampion.gameobjects

import android.graphics.Bitmap
import android.text.BoringLayout
import com.android.paddlechampion.GameView
import com.android.paddlechampion.Timer

class Ball(private val gameView: GameView, private val player: Entity, spr: MutableList<Bitmap>, index: Int, xstart: Int, ystart: Int):
    Entity(spr, index, xstart, ystart) {
    val soundBox = gameView.soundBox
    var attached: Boolean = false
    var attach_x: Int = 0
    var attach_y: Int = 0
    var radius: Int = 0
    val bounceEffectTimer: Timer = Timer(1000)
    var bounceEffectAvailable: Boolean = true
    init {
        image_speed = 0.0
        speed_max = 10.0 + gameView.gameData.playerLevel
        velocity = arrayOf(0.0,4.0)
        radius = w/2
        // get bounds (shrink it by the radius of the ball)
        val rect = gameView.playspace
        bounds.left = rect.left + radius
        bounds.top = rect.top + radius
        bounds.right = rect.right - radius
        bounds.bottom = rect.bottom - radius
    }
    override fun update() {
        if (attached) {
            velocity[0] = 0.0
            velocity[1] = 0.0
            position[0] = (player.x + attach_x).toDouble()
            position[1] = (player.y + attach_y).toDouble()
        }
        super.update()
        if(!bounceEffectAvailable) {
            bounceEffectAvailable = bounceEffectTimer.update()
            if(bounceEffectAvailable) bounceEffectTimer.reset()
        }
        if (image_index >= image_number) {  // animation will stop after playing once
            image_index = 0.0
            image_speed = 0.0
        }
    }
    override fun checkCollisions(){
        val posX = position[0].toInt()
        val posY = position[1].toInt()

        var col: Boolean = false
        var colX: Int = position[0].toInt()
        var colY: Int = position[1].toInt()
        var colAngle: Float = 0f

        if(posX < bounds.left){
            soundBox.play(5)
            col = true
            colX = bounds.left-radius
            colAngle = -90f
            position[0] = (bounds.left).toDouble()
            if(velocity[0] < 0) velocity[0] = velocity[0]*-1
        } else if(posX > bounds.right) {
            soundBox.play(5)
            col = true
            colX = bounds.right+radius
            colAngle = 90f
            position[0] = (bounds.right).toDouble()
            if(velocity[0] > 0) velocity[0] = velocity[0]*-1
        }
        if(posY < bounds.top){
            soundBox.play(5)
            col = true
            colY = bounds.top-radius
            colAngle = 180f
            position[1] = (bounds.top).toDouble()
            if(velocity[1] < 0) velocity[1] = velocity[1]*-1
        } else if(posY > bounds.bottom) {
            col = true
            colY = bounds.bottom+radius
            colAngle = 0f
            position[1] = (bounds.bottom).toDouble()
            if(velocity[1] > 0) velocity[1] = velocity[1]*-1
            // hurt player and remove this ball from play
            gameView.HurtPlayer(1)
            gameView.KillEntity(index)
        }
        if(col) {
            // start animation
            image_index = 1.0
            if(image_speed == 0.0) image_speed = 0.2
            // create a dust effect when ball collides with boundaries
            if(bounceEffectAvailable) { gameView.CreateBounceDust(colX, colY, colAngle) }
        }
    }
}