package com.android.paddlechampion

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.paddlechampion.gameobjects.Entity
import com.android.paddlechampion.gameobjects.VisualEffect
import kotlin.math.max
import kotlin.math.min

const val GAMESTATE_START: Int = 0
const val GAMESTATE_PLAY: Int = 1
const val GAMESTATE_PAUSE: Int = 2
const val GAMESTATE_LEVELCOMPLETE: Int = 3
const val GAMESTATE_DEAD: Int = 4
const val GAMESTATE_DEFEAT: Int = 5
const val GAMESTATE_VICTORY: Int = 6

class GameDataViewModel(assets: AssetManager): ViewModel() {
    private var game_state = GAMESTATE_START
    private var game_state_prev = game_state
    private var player_health_max = 10
    private var player_health = 3
    private var player_score_streak = 0
    private var player_score = 0
    private var player_high_score = 0
    private var player_level = 0
    var ballCount: Int = 0
    val ballCountMax: Int = 5

    private val game_entities: MutableList<Entity> = mutableListOf()
    private val game_visual_effects: MutableList<VisualEffect> = mutableListOf()

    val getEntities: MutableList<Entity>
        get() = game_entities
    val getVisualEffects: MutableList<VisualEffect>
        get() = game_visual_effects
    val gameState: Int
        get() = game_state
    val playerHealth: Int
        get() = player_health
    var playerScoreStreak: Int
        get() = player_score_streak
        set(value: Int) { player_score_streak = value }
    val playerScore: Int
        get() = player_score
    var playerHighScore: Int
        get() = player_high_score
        set(value: Int) { player_high_score = value }
    var playerLevel: Int
        get() = player_level
        set(value: Int) { player_level = value }
    fun changeGameState(state: Int): Int{
        game_state_prev = game_state
        game_state = state
        return game_state
    }
    fun gamePause(){
        if(gameState != GAMESTATE_PAUSE) changeGameState(GAMESTATE_PAUSE)
    }
    fun gameUnpause(){
        if(gameState == GAMESTATE_PAUSE) changeGameState(game_state_prev)
    }
    fun damagePlayer(value: Int): Boolean{
        // return true if the player has lost the game
        player_health -= value
        return player_health <= 0
    }
    fun healPlayer(value: Int){
        // return true if the player has lost the game
        player_health = min(player_health+value, player_health_max)
    }
    fun gainScore(value: Int){
        player_score_streak = min(player_score_streak+1, 10)
        player_score += value*player_score_streak
    }
    fun clear(){
        while(game_entities.size > 0) game_entities.removeAt(0)
        while(game_visual_effects.size > 0) game_visual_effects.removeAt(0)
        game_state = GAMESTATE_START
        game_state_prev = game_state
        player_health_max = 10
        player_health = 3
        player_score = 0
        player_level = 0
        ballCount = 0
    }
}

