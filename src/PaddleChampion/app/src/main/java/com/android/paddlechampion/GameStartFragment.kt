package com.android.paddlechampion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.android.paddlechampion.databinding.FragmentGameStartBinding

private const val TAG = "GAMESTART_FRAG"

class GameStartFragment : Fragment() {
    var gameDataViewModel: GameDataViewModel? = null
    private var _binding: FragmentGameStartBinding ? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot accss binding because it is null.  Is the view visible?"
        }
    private var navController: NavController ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameStartBinding.inflate(layoutInflater, container, false)
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
        main.playMusic(R.raw.snd_title_music)
        navController = findNavController()
        binding.apply {
            buttonStartGotoPlay.setOnClickListener{
                soundBox.play(8) // snd_button_startgame
                main.stopMusic()
                navController?.navigate(R.id.action_gamestart_to_gameplay)
            }
            buttonStartGotoEnd.setOnClickListener{
                soundBox.play(6) // snd_button_back
                main.stopMusic()
                navController?.navigate(R.id.action_gamestart_to_gameend)
            }
            buttonStartGotoEnd.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}