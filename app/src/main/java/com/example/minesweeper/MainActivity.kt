package com.example.minesweeper

import MineSweeperScreen
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.minesweeper.ui.MS.MineSweeperViewModel
import com.example.minesweeper.ui.theme.MineSweeperTheme

class MainActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize MediaPlayer and start playing
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer.isLooping = true // Loop the music
        mediaPlayer.start()
        val viewModel: MineSweeperViewModel by viewModels()
        setContent {
            val backgroundColor = viewModel.backgroundColor

            MineSweeperTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = backgroundColor) { innerPadding ->
                    MineSweeperScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        if (::mediaPlayer.isInitialized) mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        if (::mediaPlayer.isInitialized) mediaPlayer.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}