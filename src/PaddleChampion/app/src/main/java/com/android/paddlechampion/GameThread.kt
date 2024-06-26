package com.android.paddlechampion

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(val surfaceHolder: SurfaceHolder, var gameView: GameView) : Thread() {
    private var running: Boolean = false

    private val targetFPS = 60

    fun setRunning(isRunning: Boolean){

            this.running = isRunning

    }

    override fun run(){
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        val targetTime = (1000/targetFPS).toLong()

        while(running) {
            startTime = System.nanoTime()
            canvas = null

            try {
                // locking the canvas allows us to draw on to it
                canvas = this.surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    this.gameView.update()
                    this.gameView.draw(canvas!!)
                }
            } catch(e: Exception) {
                e.printStackTrace()
            } finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = targetTime-timeMillis

            try {
                sleep(waitTime)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private var canvas: Canvas? = null
    }
}