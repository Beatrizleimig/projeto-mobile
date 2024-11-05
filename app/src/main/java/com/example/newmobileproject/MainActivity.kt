package com.example.newmobileproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.google.androidgamesdk.GameActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonStartGame = findViewById<Button>(R.id.button_start_game)
        val buttonViewScore = findViewById<Button>(R.id.button_view_score)

        buttonStartGame.setOnClickListener {
            // Inicie a activity do jogo
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        buttonViewScore.setOnClickListener {
            // Inicie a activity de pontuação
            val intent = Intent(this, jogoActivity::class.java)
            startActivity(intent)
        }
    }
}