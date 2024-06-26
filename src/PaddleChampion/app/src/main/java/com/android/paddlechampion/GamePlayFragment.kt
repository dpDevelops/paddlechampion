package com.android.paddlechampion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.android.paddlechampion.databinding.FragmentGamePlayBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.math.log

private const val TAG = "GAMEPLAY_FRAG"

class GamePlayFragment : Fragment() {
    lateinit var gameDataViewModel: GameDataViewModel
    private var _binding: FragmentGamePlayBinding ? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null.  Is the view visible?"
        }
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGamePlayBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    /*
    SOUND INDEXES:
    0 = snd_bounce_0
    1 = snd_bounce_1
    2 = snd_bounce_2
    3 = snd_bounce_3
    4 = snd_bounce_paddle
    5 = snd_bounce_wall
    6 = snd_button_back
    7 = snd_button_forward
    8 = snd_button_startgame
    9 = and_player_dead
    10 = snd_player_defeat
    11 = snd_player_victory
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val main = context as MainActivity
        val soundBox = main.grabSoundBox as SoundBox
        navController = findNavController()
        binding.apply {
            // UI controls
            titlePlayTextView.visibility = View.GONE
            buttonPlayGotoStart.visibility = View.GONE
            buttonPlayGotoEnd.visibility = View.GONE
            buttonPlayTogglePause.visibility = View.VISIBLE

            buttonPlayGotoStart.setOnClickListener {
                binding.gameView.GameReturnToStart()
                soundBox.play(6) // snd_button_startgame
                main.stopMusic()
                navController?.navigate(R.id.action_gameplay_to_gamestart)
            }
            buttonPlayGotoEnd.setOnClickListener {
                navController?.navigate(R.id.action_gameplay_to_gameend)
            }
            buttonPlayTogglePause.setOnClickListener {
                soundBox.play(7)
                if (!binding.gameView.input_toggle_pause) {
                    binding.gameView.input_toggle_pause = true
                    if (gameView.game_state == GAMESTATE_PAUSE) {
                        // game will be unpaused
                        buttonPlayGotoStart.visibility = View.GONE
                        buttonPlayGotoEnd.visibility = View.GONE
                        titlePlayTextView.visibility = View.GONE
                        buttonPlayTogglePause.text = "PAUSE"
                    } else {
                        // game will be paused
                        buttonPlayGotoStart.visibility = View.VISIBLE
                        titlePlayTextView.visibility = View.VISIBLE
                        titlePlayTextView.text = "GAME PAUSED"
                        buttonPlayTogglePause.text = "UNPAUSE"
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withTimeout(18000000L) {
                    repeat(1800000) { i ->
                        binding.gameView.collisionTick()
                        delay(10L)
                    }
               }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



