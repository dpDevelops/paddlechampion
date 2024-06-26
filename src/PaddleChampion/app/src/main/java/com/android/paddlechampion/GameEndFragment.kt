package com.android.paddlechampion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.android.paddlechampion.databinding.FragmentGameEndBinding
import java.util.*

private const val TAG = "GAMEEND_FRAG"


class GameEndFragment : Fragment() {
    lateinit var gameDataViewModel: GameDataViewModel
    private var _binding: FragmentGameEndBinding ? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot accss binding because it is null.  Is the view visible?"
        }
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val main = context as MainActivity
        gameDataViewModel = main.grabGameData as GameDataViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameEndBinding.inflate(layoutInflater, container, false)
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
            val score = gameDataViewModel.playerScore
            val highscore = gameDataViewModel.playerHighScore
            var string: String
            when(gameDataViewModel.gameState) {
                GAMESTATE_VICTORY -> string = "----------------------\nCONGRATULATIONS\n\nYOU'VE WON!!!\n----------------------"
                else -> string = "----------------------\n- GAME OVER -\n\nBetter luck next time\n----------------------"
            }
            if(score <= highscore) {
                string += "\n\nYour Score: $score\nHigh Score: $highscore\n\n"
            } else {
                string += "\n\nNEW HIGH SCORE!!!\nYour Score: $score\nOld High Score: $highscore\n\n"
                gameDataViewModel.playerHighScore = score
            }
                titleEndTextView.text = string
            buttonEndGotoStart.setOnClickListener{
                gameDataViewModel.clear()
                soundBox.play(6)
                navController?.navigate(R.id.action_gameend_to_gamestart)
            }
            buttonEndGotoPlay.setOnClickListener{
                gameDataViewModel.clear()
                soundBox.play(8)
                navController?.navigate(R.id.action_gameend_to_gameplay)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}