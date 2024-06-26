package com.android.paddlechampion.gameobjects

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.Canvas
import android.graphics.Rect
import com.android.paddlechampion.R

class SpriteSheet(context: Context) {
    private var bitmap: Bitmap
    var bg_bitmap: Bitmap
    // var gui_bitmap: Bitmap
    var tile_size: Int = 0
    var play_rect: Rect = Rect(0,0,0,0) // indicated playspace (the rects will be used for scaling game objects)
    var gui_rect: Rect = Rect(0,0,0,0) // indicated gui display area
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    init {
        // get bitmaps
        Options().inScaled = false
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.s_spritesheet)
        /*
        // create the background for play view
        bg_bitmap = getBitmapFromNinePatch(BitmapFactory.decodeResource(context.resources, R.drawable.bg_play),1.0f,screenWidth,screenHeight)
        // create the background for the gui
        gui_bitmap = getBitmapFromNinePatch(BitmapFactory.decodeResource(context.resources, R.drawable.bg_gui),1.0f,screenWidth,(screenHeight.toFloat()*0.3f).toInt())
         */
        // create full background + gui area
        bg_bitmap = GenerateFullBackground(context, screenWidth, screenHeight)
    }

    fun getPlayerSprite(context: Context): MutableList<Bitmap>{
        var x: Int = 7 // x cell index
        var y: Int = 0 // y cell index
        var frameW: Int = 64
        var frameH: Int = 64
        var numImages = 1
        val sizeScaled: Double = (bitmap.width.toDouble()/11.0) / frameW.toDouble()
        // base values for the sprite must be scaled according to the current bitmap
        frameW = (frameW.toDouble()*sizeScaled).toInt()
        frameH = (frameH.toDouble()*sizeScaled).toInt()
        x = x*frameW
        y = y*frameH
        val bmp: MutableList<Bitmap> = mutableListOf()
        for(i in 0 until numImages) {
            bmp += Bitmap.createBitmap(bitmap, x+(frameW*i), y, frameW*3, frameH)
        }
        // scale the image according to tile size
        val desiredWidth = (play_rect.width().toFloat()*0.4f).toInt()
        val desiredHeight = desiredWidth / 6
        for(i in 0 until numImages) {
            bmp[i] = Bitmap.createScaledBitmap(bmp[i], desiredWidth, desiredHeight, true)
        }
        return  bmp
    }
    fun getBallSprite(context: Context): MutableList<Bitmap>{
        var x: Int = 7 // x cell index
        var y: Int = 1 // y cell index
        var frameW: Int = 64
        var frameH: Int = 64
        var numImages = 4
        val sizeScaled: Double = (bitmap.width.toDouble()/11.0) / frameW.toDouble()
        // base values for the sprite must be scaled according to the current bitmap
        frameW = (frameW.toDouble()*sizeScaled).toInt()
        frameH = (frameH.toDouble()*sizeScaled).toInt()
        x = x*frameW
        y = y*frameH
        val bmp: MutableList<Bitmap> = mutableListOf()
        for(i in 0 until numImages) {
            bmp += Bitmap.createBitmap(bitmap, x+(frameW*i), y, frameW, frameH)
        }
        // scale the image according to tile size
        val desiredWidth = (play_rect.width().toFloat()*0.09f).toInt()
        val desiredHeight = desiredWidth
        for(i in 0 until numImages) {
            bmp[i] = Bitmap.createScaledBitmap(bmp[i], desiredWidth, desiredHeight, true)
        }
        return  bmp
    }
    fun getBlockSprite(context: Context): MutableList<Bitmap>{
        var x: Int = 0 // x cell index
        var y: Int = 0 // y cell index
        var frameW: Int = 48
        var frameH: Int = 48
        var numImages = 5
        val sizeScaled: Double = (bitmap.width.toDouble()/704.0) // this indicates how much wider the image should be compared to the source
        // base values for the sprite must be scaled according to the current bitmap
        frameW = (frameW.toDouble()*sizeScaled).toInt()
        frameH = (frameH.toDouble()*sizeScaled).toInt()
        x = (x.toDouble()*sizeScaled).toInt()
        y = (y.toDouble()*sizeScaled).toInt()
        val bmp: MutableList<Bitmap> = mutableListOf()
        for(i in 0 until numImages) {
            bmp += Bitmap.createBitmap(bitmap, x+(frameW*i), y, frameW, frameH)
        }
        // scale the image according to tile size
        val desiredWidth = (play_rect.width().toFloat()*0.1f).toInt() // there needs to be a width of 10 cells, so divide the playspace by 10
        val desiredHeight = desiredWidth
        for(i in 0 until numImages) {
            bmp[i] = Bitmap.createScaledBitmap(bmp[i], desiredWidth, desiredHeight, true)
        }
        return  bmp
    }
    fun getBounceDustSprite(context: Context): MutableList<Bitmap>{
        var x: Int = 0 // x cell index
        var y: Int = 3 // y cell index
        var frameW: Int = 64
        var frameH: Int = 64
        var numImages = 10
        val sizeScaled: Double = (bitmap.width.toDouble()/11.0) / frameW.toDouble()
        // base values for the sprite must be scaled according to the current bitmap
        frameW = (frameW.toDouble()*sizeScaled).toInt()
        frameH = (frameH.toDouble()*sizeScaled).toInt()
        x = x*frameW
        y = y*frameH
        val bmp: MutableList<Bitmap> = mutableListOf()
        for(i in 0 until numImages) {
            bmp += Bitmap.createBitmap(bitmap, x+(frameW*i), y, frameW, frameH)
        }
        return  bmp
    }
    fun getBrickDustSprite(context: Context): MutableList<Bitmap>{
        var x: Int = 0 // x cell index
        var y: Int = 4 // y cell index
        var frameW: Int = 64
        var frameH: Int = 64
        var numImages = 10
        val sizeScaled: Double = (bitmap.width.toDouble()/11.0) / frameW.toDouble()
        // base values for the sprite must be scaled according to the current bitmap
        frameW = (frameW.toDouble()*sizeScaled).toInt()
        frameH = (frameH.toDouble()*sizeScaled).toInt()
        x = x*frameW
        y = y*frameH
        val bmp: MutableList<Bitmap> = mutableListOf()
        for(i in 0 until numImages) {
            bmp += Bitmap.createBitmap(bitmap, x+(frameW*i), y, frameW, frameH)
        }
        return  bmp
    }
    fun getCountdownSprite(context: Context, num: Int): MutableList<Bitmap>{
        var x: Int = num // x cell index
        var y: Int = 1 // y cell index
        var frameW: Int = 64
        var frameH: Int = 64
        var numImages = 10
        val sizeScaled: Double = (bitmap.width.toDouble()/11.0) / frameW.toDouble()
        // base values for the sprite must be scaled according to the current bitmap
        frameW = (frameW.toDouble()*sizeScaled).toInt()
        frameH = (frameH.toDouble()*sizeScaled).toInt()
        x = x*frameW
        y = y*frameH
        val bmp: MutableList<Bitmap> = mutableListOf()
        for(i in 0 until numImages) {
            bmp += Bitmap.createBitmap(bitmap, x, y, frameW, frameH)
        }
        return  bmp
    }
    fun getBitmapFromNinePatch(bmp_9p: Bitmap, scale: Float, maxWidth: Int, maxHeight: Int): Bitmap{
        var bmp = Bitmap.createScaledBitmap(bmp_9p,bmp_9p.width*scale.toInt(),bmp_9p.height*scale.toInt(),true)
        var thirdW = bmp.width/3
        var thirdH = bmp.height/3
        // get the 9-patches of the image
        var bmp0 = Bitmap.createBitmap(bmp,thirdW*0,thirdH*0,thirdW,thirdH)
        var bmp1 = Bitmap.createBitmap(bmp,thirdW*1,thirdH*0,thirdW,thirdH)
        var bmp2 = Bitmap.createBitmap(bmp,thirdW*2,thirdH*0,thirdW,thirdH)
        var bmp3 = Bitmap.createBitmap(bmp,thirdW*0,thirdH*1,thirdW,thirdH)
        var bmp4 = Bitmap.createBitmap(bmp,thirdW*1,thirdH*1,thirdW,thirdH)
        var bmp5 = Bitmap.createBitmap(bmp,thirdW*2,thirdH*1,thirdW,thirdH)
        var bmp6 = Bitmap.createBitmap(bmp,thirdW*0,thirdH*2,thirdW,thirdH)
        var bmp7 = Bitmap.createBitmap(bmp,thirdW*1,thirdH*2,thirdW,thirdH)
        var bmp8 = Bitmap.createBitmap(bmp,thirdW*2,thirdH*2,thirdW,thirdH)
        // scale the background up to fit the screen
        bmp = Bitmap.createScaledBitmap(bmp, maxWidth, maxHeight,true)
        // create a canvas to edit the bitmap
        var canvas = Canvas(bmp)

        // scale the edges before drawing them to the canvas
        bmp1 = Bitmap.createScaledBitmap(bmp1,canvas.width-(bmp0.width+bmp2.width),bmp1.height,true) // top edge
        bmp3 = Bitmap.createScaledBitmap(bmp3,bmp3.width, canvas.height-(bmp0.height+bmp7.height),true) // left edge
        bmp5 = Bitmap.createScaledBitmap(bmp5,bmp5.width, canvas.height-(bmp2.height+bmp8.height),true) // right edge
        bmp7 = Bitmap.createScaledBitmap(bmp7,canvas.width-(bmp6.width+bmp8.width),bmp7.height,true) // bottom edge
        bmp4 = Bitmap.createScaledBitmap(bmp4,canvas.width-(bmp0.width+bmp2.width), canvas.height-(bmp0.height+bmp6.height), true) // middle section
        // overlay all the patches onto the canvas
        canvas.drawBitmap(bmp0,0F,0F,null) // top-left corner
        canvas.drawBitmap(bmp1,bmp0.width.toFloat(),0F,null) // top-middle edge
        canvas.drawBitmap(bmp2,(bmp0.width+bmp1.width).toFloat(),0f,null) // top-right corner
        canvas.drawBitmap(bmp3,0f,bmp0.height.toFloat(),null) // middle_left edge
        canvas.drawBitmap(bmp4,bmp0.width.toFloat(),bmp0.height.toFloat(),null) // middle section
        canvas.drawBitmap(bmp5,(bmp0.width+bmp1.width).toFloat(),bmp0.height.toFloat(),null) // middle-right edge
        canvas.drawBitmap(bmp6,0F,(bmp0.height+bmp3.height).toFloat(),null) // bottom-left corner
        canvas.drawBitmap(bmp7,bmp0.width.toFloat(),(bmp0.height+bmp3.height).toFloat(),null) // bottom-middle edge
        canvas.drawBitmap(bmp8,(bmp0.width+bmp1.width).toFloat(),(bmp0.height+bmp3.height).toFloat(),null) // bottom-right corner
        return bmp
    }
    fun GenerateFullBackground(context: Context, maxWidth: Int, maxHeight: Int): Bitmap{
        // this is used instead of the function above because it combines two 9-patch images at once to avoid any scaling/alignment issues involved with
        // generating them separately
        val scale = 1.0f
        val guiHeightMod = 0.18f
        var bmp_play_9p: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bg_play)
        var bmp_gui_9p: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bg_gui)
        var b_play = Bitmap.createScaledBitmap(bmp_play_9p,bmp_play_9p.width*scale.toInt(),bmp_play_9p.height*scale.toInt(),true)
        var b_gui = Bitmap.createScaledBitmap(bmp_gui_9p,bmp_gui_9p.width*scale.toInt(),bmp_gui_9p.height*scale.toInt(),true)
        var thirdW = b_play.width/3
        var thirdH = b_play.height/3

        // save the size of the squares used for this (needed for scaling other stuff)
        tile_size = thirdW

        var bmp0_play = Bitmap.createBitmap(b_play,thirdW*0,thirdH*0,thirdW,thirdH)
        var bmp1_play = Bitmap.createBitmap(b_play,thirdW*1,thirdH*0,thirdW,thirdH)
        var bmp2_play = Bitmap.createBitmap(b_play,thirdW*2,thirdH*0,thirdW,thirdH)
        var bmp3_play = Bitmap.createBitmap(b_play,thirdW*0,thirdH*1,thirdW,thirdH)
        var bmp4_play = Bitmap.createBitmap(b_play,thirdW*1,thirdH*1,thirdW,thirdH)
        var bmp5_play = Bitmap.createBitmap(b_play,thirdW*2,thirdH*1,thirdW,thirdH)
        var bmp6_play = Bitmap.createBitmap(b_play,thirdW*0,thirdH*2,thirdW,thirdH)
        var bmp7_play = Bitmap.createBitmap(b_play,thirdW*1,thirdH*2,thirdW,thirdH)
        var bmp8_play = Bitmap.createBitmap(b_play,thirdW*2,thirdH*2,thirdW,thirdH)

        var bmp0_gui = Bitmap.createBitmap(b_gui,thirdW*0,thirdH*0,thirdW,thirdH)
        var bmp1_gui = Bitmap.createBitmap(b_gui,thirdW*1,thirdH*0,thirdW,thirdH)
        var bmp2_gui = Bitmap.createBitmap(b_gui,thirdW*2,thirdH*0,thirdW,thirdH)
        var bmp3_gui = Bitmap.createBitmap(b_gui,thirdW*0,thirdH*1,thirdW,thirdH)
        var bmp4_gui = Bitmap.createBitmap(b_gui,thirdW*1,thirdH*1,thirdW,thirdH)
        var bmp5_gui = Bitmap.createBitmap(b_gui,thirdW*2,thirdH*1,thirdW,thirdH)
        var bmp6_gui = Bitmap.createBitmap(b_gui,thirdW*0,thirdH*2,thirdW,thirdH)
        var bmp7_gui = Bitmap.createBitmap(b_gui,thirdW*1,thirdH*2,thirdW,thirdH)
        var bmp8_gui = Bitmap.createBitmap(b_gui,thirdW*2,thirdH*2,thirdW,thirdH)

        var bmp = Bitmap.createScaledBitmap(b_play, maxWidth, maxHeight,true)
        var canvas = Canvas(bmp)

        // scale the edges before drawing them to the canvas
        bmp1_play = Bitmap.createScaledBitmap(bmp1_play,canvas.width-(bmp0_play.width+bmp2_play.width),bmp1_play.height,true) // top edge
        bmp3_play = Bitmap.createScaledBitmap(bmp3_play,bmp3_play.width, canvas.height-(bmp0_play.height+bmp7_play.height),true) // left edge
        bmp5_play = Bitmap.createScaledBitmap(bmp5_play,bmp5_play.width, canvas.height-(bmp2_play.height+bmp8_play.height),true) // right edge
        bmp7_play = Bitmap.createScaledBitmap(bmp7_play,canvas.width-(bmp6_play.width+bmp8_play.width),bmp7_play.height,true) // bottom edge
        bmp4_play = Bitmap.createScaledBitmap(bmp4_play,canvas.width-(bmp0_play.width+bmp2_play.width), canvas.height-(bmp0_play.height+bmp6_play.height), true) // middle section
        // overlay all the patches onto the canvas
        canvas.drawBitmap(bmp0_play,0F,0F,null) // top-left corner
        canvas.drawBitmap(bmp1_play,bmp0_play.width.toFloat(),0F,null) // top-middle edge
        canvas.drawBitmap(bmp2_play,(bmp0_play.width+bmp1_play.width).toFloat(),0f,null) // top-right corner
        canvas.drawBitmap(bmp3_play,0f,bmp0_play.height.toFloat(),null) // middle_left edge
        canvas.drawBitmap(bmp4_play,bmp0_play.width.toFloat(),bmp0_play.height.toFloat(),null) // middle section
        canvas.drawBitmap(bmp5_play,(bmp0_play.width+bmp1_play.width).toFloat(),bmp0_play.height.toFloat(),null) // middle-right edge
        canvas.drawBitmap(bmp6_play,0F,(bmp0_play.height+bmp3_play.height).toFloat(),null) // bottom-left corner
        canvas.drawBitmap(bmp7_play,bmp0_play.width.toFloat(),(bmp0_play.height+bmp3_play.height).toFloat(),null) // bottom-middle edge
        canvas.drawBitmap(bmp8_play,(bmp0_play.width+bmp1_play.width).toFloat(),(bmp0_play.height+bmp3_play.height).toFloat(),null) // bottom-right corner

        // scale the edges before drawing them to the canvas
        bmp1_gui = Bitmap.createScaledBitmap(bmp1_gui,canvas.width-(bmp0_gui.width+bmp2_gui.width),bmp1_gui.height,true) // top edge
        bmp3_gui = Bitmap.createScaledBitmap(bmp3_gui,bmp3_gui.width, (canvas.height.toFloat()*guiHeightMod).toInt()-(bmp0_gui.height*2),true) // left edge
        bmp5_gui = Bitmap.createScaledBitmap(bmp5_gui,bmp5_gui.width, (canvas.height.toFloat()*guiHeightMod).toInt()-(bmp0_gui.height*2),true) // right edge
        bmp7_gui = Bitmap.createScaledBitmap(bmp7_gui,canvas.width-(bmp6_gui.width+bmp8_gui.width),bmp7_gui.height,true) // bottom edge
        bmp4_gui = Bitmap.createScaledBitmap(bmp4_gui,canvas.width-(bmp0_gui.width+bmp2_gui.width), canvas.height-(bmp0_gui.height+bmp6_gui.height), true) // middle section
        // overlay all the patches onto the canvas
        val newY = (canvas.height - (canvas.height.toFloat()*guiHeightMod).toInt()-1).toFloat()
        canvas.drawBitmap(bmp0_gui,0F,newY,null) // top-left corner
        canvas.drawBitmap(bmp1_gui,bmp0_gui.width.toFloat(),newY.toFloat(),null) // top-middle edge
        canvas.drawBitmap(bmp2_gui,(bmp0_gui.width+bmp1_gui.width).toFloat(),newY,null) // top-right corner
        canvas.drawBitmap(bmp3_gui,0f,newY + bmp0_gui.height.toFloat(),null) // middle_left edge
        canvas.drawBitmap(bmp4_gui,bmp0_gui.width.toFloat(),newY + bmp0_gui.height.toFloat(),null) // middle section
        canvas.drawBitmap(bmp5_gui,(bmp0_gui.width+bmp1_gui.width).toFloat(),newY + bmp0_gui.height.toFloat(),null) // middle-right edge
        canvas.drawBitmap(bmp6_gui,0F,newY + (bmp0_gui.height+bmp3_gui.height).toFloat(),null) // bottom-left corner
        canvas.drawBitmap(bmp7_gui,bmp0_gui.width.toFloat(),newY + (bmp0_gui.height+bmp3_gui.height).toFloat(),null) // bottom-middle edge
        canvas.drawBitmap(bmp8_gui,(bmp0_gui.width+bmp1_gui.width).toFloat(),newY + (bmp0_gui.height+bmp3_gui.height).toFloat(),null) // bottom-right corner

        // calculate the playspace rect
        play_rect.top = bmp0_play.height
        play_rect.bottom = newY.toInt()-1
        play_rect.left = bmp0_play.width
        play_rect.right = bmp0_play.width + bmp1_play.width
        // calculate the gui rect
        gui_rect.top = newY.toInt() + bmp0_gui.height
        gui_rect.bottom = newY.toInt() + bmp0_gui.height + bmp3_gui.height
        gui_rect.left = bmp0_play.width
        gui_rect.right = bmp0_play.width + bmp1_play.width
        return bmp
    }
}