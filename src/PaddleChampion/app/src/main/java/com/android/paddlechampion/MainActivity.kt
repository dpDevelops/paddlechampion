package com.android.paddlechampion

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

private const val TAG: String = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val gameDataViewModel: GameDataViewModel by lazy {
        ViewModelProvider(this, GameDataViewModelFactory(assets))[GameDataViewModel::class.java]
    }
    private val soundBoxViewModel: SoundBoxViewModel by lazy {
        ViewModelProvider(this, SoundBoxViewModelFactory(assets)).get(SoundBoxViewModel::class.java)
    }
    var mMusicPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // remove notification bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment == null) {
            val fragment = GameStartFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        if(mMusicPlayer != null){
            mMusicPlayer!!.start()
        }
    }
    override fun onPause() {
        super.onPause()
    }
    override fun onStop() {
        super.onStop()
        pauseMusic()
    }
    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }
    val grabGameData: Any
        get() = gameDataViewModel
    val grabSoundBox: Any
        get() = soundBoxViewModel.soundBox

    // MUSIC STUFF
    fun playMusic(song: Int = R.raw.snd_title_music) {
        if (mMusicPlayer == null) {
            mMusicPlayer = MediaPlayer.create(this,song)
            mMusicPlayer!!.isLooping = true
            mMusicPlayer!!.start()
        } else mMusicPlayer!!.start()
    }
    fun pauseMusic() {
        if (mMusicPlayer?.isPlaying == true) mMusicPlayer?.pause()
    }
    fun stopMusic(){
        if (mMusicPlayer != null) {
            mMusicPlayer!!.stop()
            mMusicPlayer!!.release()
            mMusicPlayer = null
        }
    }
}