package com.android.paddlechampion.gameobjects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.android.paddlechampion.GameView
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.pow

const val EAST = 0
const val NORTH = 1
const val WEST = 2
const val SOUTH = 3

class BlockManager(private val gameView: GameView, sprite: MutableList<Bitmap>, index: Int): Entity(sprite, index,0,0) {
    private val soundBox = gameView.soundBox
    private var blockW: Int = 0
    private var blockH: Int = 0
    private var gridW: Int = 10 // if you want the grid width to change, make sure the spritesheet is altered to resize images accordingly
    private var gridH: Int = 8
    private val nodeMap: MutableMap<String, Node> = mutableMapOf()
    private val nodeList: MutableList<Node> = mutableListOf()
    private var blockCount: Int = 0
    init {
        // get block size and grid size based on the sprite
        blockW = sprite[0].width
        blockH = sprite[0].height
        // gridW = (bounds.width()/blockW) - ((bounds.width()/blockW) % 2)  // width and height will always be even values
        // gridH = (bounds.height()/blockH) - ((bounds.height()/blockH) % 2)
        // set the bounds in accordance with block size
        val rect = gameView.playspace
        bounds = Rect(rect.left,rect.top,rect.right,rect.bottom)
        bounds.left = (bounds.left+bounds.right)/2 - blockW*(gridW+2)/2
        bounds.right = bounds.left + blockW*(gridW+2)
        bounds.top = bounds.top-blockH
        bounds.bottom = bounds.top + blockH*(gridH+2)
        for(j in 0..gridH+1){
            for(i in 0..gridW+1){
                val node = Node(i, j)
                nodeMap["$i.$j"] = node
                nodeList += node
                node.validateNeighbors()
            }
        }
    }

    // function for entities to get a node based on position
    fun getNode(x: Int, y: Int) : Node? {
        val cellX = (x-bounds.left)/blockW
        val cellY = (y-bounds.top)/blockH
        val index = cellX + cellY*(gridW+2)
        if(index < 0 || index >= nodeList.size) return null
        return nodeList[index]
    }

    override fun update() {

    }

    override fun draw(canvas: Canvas) {
        // draw the assigned sprite at current image frame
        for (node in nodeList) {
            node.draw(canvas)
        }
    }
    override fun checkCollisions(){
        val entities = gameView.entities
        // keep track of balls
        for(i in 0 until entities.size) {
            val entity = entities[i]
            if(entity is Ball) {
                val node = getNode(entity.x, entity.y)
                if(node != null) {
                    // get neighbor nodes, adjacent and diagonal, indexed from 0 to 7 starting with east going ccw

                    // check each neighbor for a collision with the ball
                    /*
                        for the collision the block will have 8 points of contact:
                        the midpoints of each face, and the corners

                        based on the ball's velocity vector, determine the 3 points that the ball is likely to hit first and do collision checks for these points
                     */
                    for (j in 0 until 8) {
                        if (node.neighbors[j] == -1) continue
                        val neighbor: Node = nodeList[node.neighbors[j]]
                        if (neighbor.blockHP == -1) continue

                        val dx = (neighbor.x + blockW/2 - entity.x).toFloat()
                        val dy = (neighbor.y + blockH/2 - entity.y).toFloat()
                        val dist = sqrt(dx.pow(2f) + dy.pow(2f))
                        val ang = atan(dy / dx)
                        var cardinalDir = ((ang*180/PI)+405).toInt() / 360 // 405 = 360 + 45
                        if(cardinalDir > 3) cardinalDir -= 4
                        val uvBlock = arrayOf(dx / dist, dy / dist) // unit vector pointing to the block
                        val uvBall = arrayOf(entity.velocity[0] / entity.speed_max, entity.velocity[1] / entity.speed_max) // unit vector of ball velocity
                        val velAlignment = uvBlock[0]*uvBall[0] + uvBlock[1]*uvBall[1]
                        var col = -1

                        when(j){
                            0 -> { // check right edge of current node
                                val point = (neighbor.vertices[4][0] - entity.radius).toInt()
                                if(entity.x > point){
                                    if(velAlignment > 0) entity.velocity[0] *= -1.0
                                    entity.x = point
                                    entity.position[0] = point.toDouble()
                                    col = j
                                }
                            }
                            4 -> { // check left edge of current node
                                val point = (neighbor.vertices[0][0] + entity.radius).toInt()
                                if(entity.x < point){
                                    if(velAlignment > 0) entity.velocity[0] *= -1.0
                                    entity.x = point
                                    entity.position[0] = point.toDouble()
                                    col = j
                                }
                            }
                            2 -> { // check top edge of current node
                                val point = (neighbor.vertices[6][1] + entity.radius).toInt()
                                if(entity.y < point){
                                    if(velAlignment > 0) entity.velocity[1] *= -1.0
                                    entity.y = point
                                    entity.position[1] = point.toDouble()
                                    col = j
                                }
                            }
                            6 -> { // check bottom edge of current node
                                val point = (neighbor.vertices[2][1] - entity.radius).toInt()
                                if(entity.y > point){
                                    if(velAlignment > 0) entity.velocity[1] *= -1.0
                                    entity.y = point
                                    entity.position[1] = point.toDouble()
                                    col = j
                                }
                            }
                            else -> {
                                var ind = j + 4
                                if(ind > 7) ind -= 8
                                val ddx = (neighbor.vertices[ind][0] - entity.x)
                                val ddy = (neighbor.vertices[ind][1] - entity.y)
                                if(sqrt(ddx.pow(2f) + ddy.pow(2f)) <= entity.radius){
                                    col = j
                                    when(cardinalDir){
                                        0 -> {if(velAlignment > 0) entity.velocity[0] *= -1.0}
                                        1 -> {if(velAlignment > 0) entity.velocity[1] *= -1.0}
                                        2 -> {if(velAlignment > 0) entity.velocity[0] *= -1.0}
                                        3 -> {if(velAlignment > 0) entity.velocity[1] *= -1.0}
                                    }
                                }
                            }
                        }
                        // do collision effects
                        if(col > -1){
                            // other collision effects
                            soundBox.play(min(neighbor.blockHP, 3))
                            // reduce block health
                            damageBlock(neighbor)
                        }
                    }
                }
            }
        }
    }
    fun damageBlock(node: Node){
        if(node.blockHP != 0){
            node.blockHP -= 1
            if(node.blockHP == 0) {
                blockCount -= 1
                node.blockHP = -1
                gameView.gameData.gainScore(10)
                gameView.CreateBrickDust(node.x+node.rect.width()/2, node.y+node.rect.height()/2)
                if(blockCount == 0){
                    gameView.gameNextLevelTransition()
                }
            }
        }
    }
    fun levelLoad(level_index: Int) {
        // initialize the level using a 2D array representing node health values
        blockCount = 0
        var levelLayout = Array(gridH) { Array(gridW) { -1 } }
        when(level_index) {
            6 -> {
                levelLayout[0] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[1] = arrayOf(-1, -1, -1, 1, -1, -1, 1, -1, -1, -1)
                levelLayout[2] = arrayOf(-1, -1, -1, -1, 3, 3, -1, -1, -1, -1)
                levelLayout[3] = arrayOf(-1, 1, -1, -1, -1, -1, -1, -1, 1, -1)
                levelLayout[4] = arrayOf(-1, 1, -1, -1, 4, 4, -1, -1, 1, -1)
                levelLayout[5] = arrayOf(-1, -1, -1, 1, 0, 0, 1, -1, -1, -1)
                levelLayout[6] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[7] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
            }
            5 -> {
                levelLayout[0] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[1] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[2] = arrayOf(-1, -1, -1, -1, 1, 1, -1, -1, -1, -1)
                levelLayout[3] = arrayOf(-1, -1, -1, -1, 2, 2, -1, -1, -1, -1)
                levelLayout[4] = arrayOf(0, -1, -1, -1, 3, 3, -1, -1, -1, 0)
                levelLayout[5] = arrayOf(0, -1, -1, -1, -1, -1, -1, -1, -1, 0)
                levelLayout[6] = arrayOf(0, 0, -1, -1, -1, -1, -1, -1, 0, 0)
                levelLayout[7] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
            }
            4 -> {
                levelLayout[0] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[1] = arrayOf(-1, -1, -1, 1, 1, 1, 1, -1, -1, -1)
                levelLayout[2] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[3] = arrayOf(-1, 3, -1, -1, -1, -1, -1, -1, 3, -1)
                levelLayout[4] = arrayOf(-1, -1, 0, 0, 0, 0, 0, 0, -1, -1)
                levelLayout[5] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[6] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[7] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
            }
            3 -> {
                levelLayout[0] = arrayOf(0, -1, -1, -1, -1, -1, -1, -1, -1, 0)
                levelLayout[1] = arrayOf(0, -1, -1, 1, -1, -1, 1, -1, -1, 0)
                levelLayout[2] = arrayOf(0, -1, -1, -1, 1, 1, -1, -1, -1, 0)
                levelLayout[3] = arrayOf(0, -1, -1, -1, -1, -1, -1, -1, -1, 0)
                levelLayout[4] = arrayOf(-1, 2, -1, -1, 1, 1, -1, -1, 2, -1)
                levelLayout[5] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[6] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[7] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
            }
            2 -> {
                levelLayout[0] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[1] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[2] = arrayOf(-1, -1, 1, -1, -1, -1, -1, 1, -1, -1)
                levelLayout[3] = arrayOf(-1, -1, -1, 1, -1, -1, 1, -1, -1, -1)
                levelLayout[4] = arrayOf(-1, -1, -1, -1, 2, 2, -1, -1, -1, -1)
                levelLayout[5] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[6] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[7] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
            }
            1 -> {
                levelLayout[0] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[1] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[2] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[3] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[4] = arrayOf(-1, 1, -1, -1, 1, 1, -1, -1, 1, -1)
                levelLayout[5] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[6] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                levelLayout[7] = arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
            }
            else -> {}
        }
        for (j in 1..gridH) {
        for (i in 1..gridW) {
            val node = nodeMap["$i.$j"]
            val hp = levelLayout[j-1][i-1]
            if(hp > 0) blockCount += 1
            node?.blockHP = hp
        }}
    }


    inner class Edge(var x1: Int, var y1: Int, var x2: Int, var y2: Int)

    inner class Node(val xx: Int, val yy: Int) {
        val index: Int = xx + (gridW+2)*yy
        val x = ((bounds.left+bounds.right)/2 - blockW - (blockW*gridW/2)) + blockW*xx
        val y = (bounds.top) + blockH*yy
        val spr = sprite
        var blockHP = -1 //(0 until spr.size).random()
        val rect: Rect = Rect(x,y,x+blockW,y+blockH)
        val vertices = arrayOf( // this stores x-y coordinates for vertices to check collisions with the balls
            arrayOf((x+blockW  ).toFloat(), (y+blockH/2).toFloat()),
            arrayOf((x+blockW  ).toFloat(), (y         ).toFloat()),
            arrayOf((x+blockW/2).toFloat(), (y         ).toFloat()),
            arrayOf((x         ).toFloat(), (y         ).toFloat()),
            arrayOf((x         ).toFloat(), (y+blockH/2).toFloat()),
            arrayOf((x         ).toFloat(), (y+blockH).toFloat()),
            arrayOf((x+blockW/2).toFloat(), (y+blockH).toFloat()),
            arrayOf((x+blockW).toFloat(), (y+blockH).toFloat()),
        )
        val neighbors: Array<Int> = arrayOf( // this stores indexes of adjacent and diagonal neighbor nodes.  will be -1 if node is not valid
            0,0,0,0,0,0,0,0)

        fun validateNeighbors(){
            // calculate all the indexes then set them inside of the neighbors array
            val listSize = (gridH+2)*(gridW+2)
            for(i in 0 until 8){
                var ind: Int = -1
                val gridWidth = gridW+2
                val myRow = index/gridWidth
                when(i){
                    0 -> { // east
                        ind = index+1
                        if(ind/gridWidth != myRow) ind = -1
                        if(ind >= listSize) ind = -1
                    }
                    1 -> { // northeast
                        ind = index-gridWidth+1
                        if(ind/gridWidth != myRow-1) ind = -1
                        if(ind < 0) ind = -1
                    }
                    2 -> { // north
                        ind = index-gridWidth
                        if(ind < 0) ind = -1
                    }
                    3 -> { // northwest
                        ind = index-gridWidth-1
                        if(ind/gridWidth != myRow-1) ind = -1
                        if(ind < 0) ind = -1
                    }
                    4 -> { // west
                        ind = index-1
                        if(ind/gridWidth != myRow) ind = -1
                        if(ind < 0) ind = -1
                    }
                    5 -> { // southwest
                        ind = index+gridWidth-1
                        if(ind/gridWidth != myRow+1) ind = -1
                        if(ind >= listSize) ind = -1
                    }
                    6 -> { // south
                        ind = index+gridWidth
                        if(ind >= listSize) ind = -1
                    }
                    7 -> { // southeast
                        ind = index+gridWidth+1
                        if(ind/gridWidth != myRow+1) ind = -1
                        if(ind >= listSize) ind = -1
                    }
                }
                neighbors[i] = ind
            }
        }

        fun update() {

        }

        fun draw(canvas: Canvas) {
            // draw the assigned sprite at current image frame
            if(blockHP > -1) canvas.drawBitmap(spr[blockHP], x.toFloat(), y.toFloat(), null)

            val showBlocks = false
            if(showBlocks) {
                val colors = Paint()

                if (((xx - (yy % 2)) % 2) == 0) {
                    colors.setARGB(20, 255, 255, 255)
                } else {
                    colors.setARGB(20, 0, 0, 0)
                }
                canvas.drawRect(rect, colors)

                colors.setARGB(255, 0, 0, 0)
                colors.textSize = 38F
                //canvas.drawText(state_string, 40f,(yBot-increment*4.0).toFloat(),colors)
                canvas.drawText("$index", (x).toFloat(), (y + blockH / 2).toFloat(), colors)
            }
        }
    }
}
