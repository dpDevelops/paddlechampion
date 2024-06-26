package com.android.paddlechampion

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel

class SoundBoxViewModel(assets: AssetManager) : ViewModel() {
    var soundBox: SoundBox = SoundBox(assets)

    override fun onCleared() {
        super.onCleared()
        soundBox.release()
    }
}