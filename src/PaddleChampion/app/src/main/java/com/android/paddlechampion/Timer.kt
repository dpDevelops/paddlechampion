package com.android.paddlechampion

class Timer(private var duration: Long) {
    private var remainingTime = duration
    private var prevTime: Long = System.nanoTime()
    private var isRunning: Boolean = true
    fun update(): Boolean {
        if(isRunning) {
            remainingTime -= (System.nanoTime() - prevTime) / 1000000
            prevTime = System.nanoTime()
        }
        return timeIsUp()
    }

    fun timeIsUp() = remainingTime <= 0

    fun resume(){
        isRunning = true
        prevTime = System.nanoTime()
    }
    fun pause(){
        isRunning = false
    }
    fun reset(newTime: Long? = null) {
        if(newTime != null) duration = newTime
        remainingTime = duration
        isRunning = true
    }
}
