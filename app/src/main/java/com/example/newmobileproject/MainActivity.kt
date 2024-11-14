package com.example.newmobileproject

// ... imports ...
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newmobileproject.databinding.ActivityMainBinding // Importe o Binding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Inicialize o Binding
        setContentView(binding.root) // Defina o conteúdo da view

        // Listener do botão Start Game
        binding.buttonStartGame.setOnClickListener { // Acesse as views pelo Binding
            // Inicie a activity do jogo
            val intent = Intent(this, jogoActivity::class.java)
            startActivity(intent)
        }

        // Listener do botão View Score
        binding.buttonViewScore.setOnClickListener { // Acesse as views pelo Binding
            // Inicie a activity de pontuação
            val intent = Intent(this, jogoActivity::class.java)
            startActivity(intent)
        }
    }
}