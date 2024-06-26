package com.android.paddlechampion

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import androidx.navigation.findNavController
import com.android.paddlechampion.gameobjects.Ball
import com.android.paddlechampion.gameobjects.BlockManager
import com.android.paddlechampion.gameobjects.Entity
import com.android.paddlechampion.gameobjects.Player
import com.android.paddlechampion.gameobjects.SpriteSheet
import com.android.paddlechampion.gameobjects.VisualEffect
import kotlinx.coroutines.Job
import kotlin.math.log
import kotlin.math.max
import kotlin.random.Random
import kotlin.random.nextInt

//private const val TRANSTYPE_NULL = -1
//private const val TRANSTYPE_START = 0
//private const val TRANSTYPE_HURT = 1
//private const val TRANSTYPE_GAMEOVER = 2
//private const val TRANSTYPE_NEXTLEVEL = 3
private const val TAG = "GAMEPLAY_VIEW"

class GameView(context: Context, attributes: AttributeSet) :
    SurfaceView(context, attributes), SurfaceHolder.Callback {
    private val main = context as MainActivity
    private val gameDataViewModel: GameDataViewModel = main.grabGameData as GameDataViewModel
    private val gameSoundBox: SoundBox = main.grabSoundBox as SoundBox
    var thread: GameThread
    private val guiView: GameGuiView
    private lateinit var blockManager: BlockManager
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    private val spriteSheet: SpriteSheet = SpriteSheet(context)
    var touched: Boolean = false
    var touched_xstart: Int = 0
    var touched_ystart: Int = 0
    var touched_xcurrent: Int = 0
    var touched_ycurrent: Int = 0
    var playCountdown: Int = -1
    lateinit var playCountdownTimer: Timer
    // player inputs
    var input_movement:Array<Double> = arrayOf(0.0,0.0)
    var input_toggle_pause: Boolean = false

    init{
        holder.addCallback(this)
        thread = GameThread(holder, this)
        guiView = GameGuiView(holder, this)
    }

    val gameData: GameDataViewModel
        get() = gameDataViewModel
    var game_state: Int?
        get() { return gameDataViewModel.gameState }
        set(value: Int?) { gameDataViewModel.changeGameState(value!!) }
    val soundBox: SoundBox
        get() = gameSoundBox
    val playspace
        get(): Rect = spriteSheet.play_rect

    val guispace
        get(): Rect = spriteSheet.gui_rect
    val entities: MutableList<Entity>
        get() = gameDataViewModel.getEntities
    val visualEffects: MutableList<VisualEffect>
        get() = gameDataViewModel.getVisualEffects

    override fun surfaceCreated(holder: SurfaceHolder) {
        if(entities.size == 0){
            //create the player and the blockManager
            CreatePlayer(screenWidth/2,playspace.bottom)
            blockManager = CreateBlockManager()
            gameStartLevel(1)
        }

        // ensure a fresh thread is started
        thread = GameThread(holder, this)
        thread.setRunning(true)
        thread.start()
    }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread.setRunning(false)
                thread.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            retry = false
        }
    }
    /**
     * Function to update the positions of player and game objects
     */
    fun update() {
        if(input_toggle_pause){
            input_toggle_pause = false
            when(game_state){
                GAMESTATE_PAUSE -> {
                    playCountdownTimer.resume()
                    gameDataViewModel.gameUnpause()
                    if(game_state == GAMESTATE_PLAY) main.playMusic(R.raw.snd_play_music)
                }
                else -> {
                    playCountdownTimer.pause()
                    gameDataViewModel.gamePause()
                    main.pauseMusic()
                }
            }
        }
        // countdown timer (this only matters at the start of the view)
        if(playCountdown > -1){
            if(playCountdownTimer.update()){
                // show countdown effect only when starting a level
                if(game_state == GAMESTATE_START) CreateCountDownNumber(playCountdown)
                // increment countdown
                playCountdown -= 1
                // reset timer, or perform action when countdown reaches -1
                if (playCountdown > -1) {
                    playCountdownTimer.reset()
                    soundBox.play(7)
                } else {
                    when(game_state) {
                        GAMESTATE_START -> {
                            game_state = GAMESTATE_PLAY
                            main.playMusic(R.raw.snd_play_music)
                        } // finish countdown, allowing the game loop to start
                        GAMESTATE_LEVELCOMPLETE -> {
                            soundBox.play(4)
                            gameDataViewModel.playerLevel += 1
                            gameStartLevel(gameDataViewModel.playerLevel)
                        }
                        GAMESTATE_DEAD -> gameRespawn() // this will reset the playspace, maintaining level progress
                        GAMESTATE_DEFEAT -> GameOver(GAMESTATE_DEFEAT) // go to game over screen, use ViewModel's state to decide on loss screen or a victory screen
                        GAMESTATE_VICTORY -> GameOver(GAMESTATE_VICTORY)
                        else -> {} // do nothing
                    }
                }
            }
        }
        if(game_state != GAMESTATE_PAUSE) {
            // update the gui
            guiView.update()

            // update visual effects
            for (i in visualEffects.size - 1 downTo 0) {
                if (visualEffects[i].update()) visualEffects.removeAt(i)
            }
            // don't update entities while transitioning
            if (game_state == GAMESTATE_PLAY) {
                // update entites
                for (i in entities.size - 1 downTo 0) {
                    entities[i].update()
                }
            }
        }
    }
    /**
     * Everything that has to be drawn on Canvas
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        // draw background
        canvas.drawBitmap(spriteSheet.bg_bitmap,0F,0F,null)

        // draw entites
        for(i in 0 until entities.size) {
            entities[i].draw(canvas)
        }
        // draw visual effects
        for(i in 0 until visualEffects.size) {
            visualEffects[i].draw(canvas)
        }
        // draw the gui
        guiView.draw(canvas)
        val colors = Paint()
        colors.setARGB(255,0,0,0)
        /*
        val state_string = when(gameDataViewModel.gameState){
            GAMESTATE_START -> "Game State = START"
            GAMESTATE_PLAY -> "Game State = PLAY"
            GAMESTATE_PAUSE -> "Game State = PAUSE"
            GAMESTATE_LEVELCOMPLETE -> "Game State = LEVELCOMPLETE"
            GAMESTATE_DEAD -> "Game State = DEAD"
            GAMESTATE_DEFEAT -> "Game State = DEFEAT"
            GAMESTATE_VICTORY -> "Game State = VICTORY"
            else -> "Game State = NULL"
        }
         */
        val textPos: Array<Float> = arrayOf(0f,0f)
        val yBot: Float = (guispace.bottom-(guispace.height()*0.04f))
        val increment: Float = (guispace.height()*0.2f)
        val num = guispace.height()

        // get the font
        val font = Typeface.createFromAsset(context.assets, "fonts/toriko.ttf")
        colors.setTypeface(font)
        // size the font
        val fontBounds: Rect = Rect()
        val desiredWidth = (playspace.width()*0.1).toFloat()
        val textSize = 32f
        var text: String = "SCORE = 0"
        colors.getTextBounds(text, 0, text.length, fontBounds)
        colors.textSize = textSize * desiredWidth / fontBounds.width()


        // draw the text strings, showing game data
        for(i in 0 until 3){
            when(i){
                0 -> {
                    text = "SCORE = ${gameDataViewModel.playerScore}"
                    colors.getTextBounds(text, 0, text.length, fontBounds)
                    textPos[0] = guispace.left.toFloat()
                    textPos[1] = guispace.top.toFloat()*1.01f
                }
                1 -> {
                    text = "LIVES = ${gameDataViewModel.playerHealth}"
                    colors.getTextBounds(text, 0, text.length, fontBounds)
                    textPos[0] = (guispace.right - fontBounds.width()).toFloat()// 40f
                    textPos[1] = guispace.top.toFloat()*1.01f
                }
                2 -> {
                    text = "LEVEL = ${gameDataViewModel.playerLevel}/6"
                    colors.getTextBounds(text, 0, text.length, fontBounds)
                    textPos[0] = guispace.left.toFloat() // (guispace.left + guispace.width()/2 - fontBounds.width()/2).toFloat()
                    textPos[1] = guispace.bottom.toFloat()
                }
            }
            canvas.drawText(text, textPos[0], textPos[1], colors)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // when ever there is a touch on the screen,
        // we can get the position of touch
        // which we may use it for tracking some of the game objects
        touched_xcurrent = event.x.toInt()
        touched_ycurrent = event.y.toInt()

        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> touched = true
            MotionEvent.ACTION_MOVE -> touched = true
            MotionEvent.ACTION_UP -> touched = false
            MotionEvent.ACTION_CANCEL -> touched = false
            MotionEvent.ACTION_OUTSIDE -> touched = false
        }
        if(action == MotionEvent.ACTION_DOWN)
        {
            touched_xstart = touched_xcurrent
            touched_ystart = touched_ycurrent
            touched_xcurrent += 1
            touched_ycurrent += 1
        } else if(action == MotionEvent.ACTION_UP) {
            input_movement[0] = 0.0
            input_movement[1] = 0.0
        }
        return true
    }
    fun gameNextLevelTransition(){
        if(gameDataViewModel.playerLevel < 6){
            game_state = GAMESTATE_LEVELCOMPLETE
        } else {
            game_state = GAMESTATE_VICTORY
        }
        playCountdown = 0
        playCountdownTimer = Timer(2000)
        // ADD SOME VISUAL EFFECT CREATION AND SOUNDS HERE /////////////////////////
    }
    fun HurtPlayer(damage: Int){
        if(gameDataViewModel.damagePlayer(damage)){
            game_state = GAMESTATE_DEFEAT
        } else {
            game_state = GAMESTATE_DEAD
        }
        // healthTextView.text = "LIVES = ${gameDataViewModel.playerHealth}"
        playCountdown = 0
        playCountdownTimer = Timer(2000)
        soundBox.play(9)
        // ADD SOME VISUAL EFFECT CREATION AND SOUNDS HERE /////////////////////////
    }
    fun gameStartLevel(levelIndex: Int){
        gameDataViewModel.playerLevel = max(levelIndex, 1)
        blockManager.levelLoad(gameDataViewModel.playerLevel)
        gameRespawn()
    }
    fun collisionTick(){
        for (i in entities.size - 1 downTo 0) {
            entities[i].checkCollisions()
        }
    }
    fun gameRespawn(){
        // change state
        playCountdown = 3
        playCountdownTimer = Timer(1000)
        game_state = GAMESTATE_START
        // set up entites for game start
        if(gameDataViewModel.ballCount == 0){
            // if size == 2 then that means there is no ball in the entities list, se we need to make one
            CreateBall(screenWidth/2, screenHeight/2)
        }
        for(i in 0 until entities.size){
            if(i > 2) {
                entities.removeAt(3)
            } else {
                val entity = entities[i]
                if (entity is Ball || entity is Player) {
                    entity.x = entity.xstart
                    entity.y = entity.ystart
                    entity.position[0] = entity.x.toDouble()
                    entity.position[1] = entity.y.toDouble()
                    entity.velocity[0] = 0.0
                    entity.velocity[1] = 0.0
                    if (entity is Ball) {
                        val uv = arrayOf(((-10..10).random().toFloat()*.0001f),1f)
                        entity.velocity[0] = uv[0]*entity.speed_max*0.5
                        entity.velocity[1] = uv[1]*entity.speed_max*0.5
                    }
                    entity.update()
                }
            }
        }
    }
    fun GameReturnToStart(){
        gameDataViewModel.clear()
    }
    fun GameOver(nextGameState: Int = GAMESTATE_DEFEAT){ // nextGameState should be either Defeat of Victory
        when(nextGameState){
            GAMESTATE_DEFEAT -> {soundBox.play(10)}
            else -> {soundBox.play(11)}
        }
        game_state = nextGameState
        main.stopMusic()
        findNavController().navigate(R.id.action_gameplay_to_gameend)
    }
    fun KillEntity(index: Int){
        // update ball count if applicable
        if(entities[index] is Ball){gameDataViewModel.ballCount -= 1}
        // remove reference to entity
        entities.removeAt(index)
        // update index values for remaining entities
        for(i in 0 until entities.size){
            entities[i].index = i
        }
    }
    // entity creation
    fun CreatePlayer(x: Int, y: Int): Entity {
        val entities = gameDataViewModel.getEntities
        val player = Player(this, spriteSheet.getPlayerSprite(context), entities.size, x,y)
        player.update()
        entities += player
        return player
    }
    fun CreateBall(x: Int, y: Int): Entity? {
        if(gameDataViewModel.ballCount < gameDataViewModel.ballCountMax) {
            val entities = gameDataViewModel.getEntities
            val ball =
                Ball(this, entities[0], spriteSheet.getBallSprite(context), entities.size, x, y)
            ball.update()
            entities += ball
            gameDataViewModel.ballCount += 1
            return ball
        }
        return null
    }
    fun CreateBlockManager(): BlockManager {
        val entities = gameDataViewModel.getEntities
        val mngr = BlockManager(this, spriteSheet.getBlockSprite(context), entities.size)
        mngr.update()
        entities += mngr
        return mngr
    }
    fun CreateBounceDust(x: Int, y: Int, angle: Float): VisualEffect {
        val visEffects = gameDataViewModel.getVisualEffects
        val effect = VisualEffect(spriteSheet.getBounceDustSprite(context),0.5, angle, 1, x, y)
        effect.update()
        visEffects += effect
        return effect
    }
    fun CreateBrickDust(x: Int, y: Int): VisualEffect {
        val visEffects = gameDataViewModel.getVisualEffects
        val effect = VisualEffect(spriteSheet.getBrickDustSprite(context),0.5, Random.nextInt(360).toFloat(), 1, x, y)
        effect.update()
        visEffects += effect
        return effect
    }
    fun CreateCountDownNumber(num: Int): VisualEffect {
        val visEffects = gameDataViewModel.getVisualEffects
        val effect = VisualEffect(spriteSheet.getCountdownSprite(context, num),0.4, 0f, 1, screenWidth/2, screenHeight/2)
        effect.update()
        effect.image_index = playCountdown.toDouble()
        effect.setVelocity(0.0,-4.0)
        visEffects += effect
        return effect
    }
}