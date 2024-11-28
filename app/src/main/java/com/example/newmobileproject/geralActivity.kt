package com.example.newmobileproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class geralActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geral) // Certifique-se de usar o layout correto

        // Referências para os TextViews
        val scoreTextView = findViewById<TextView>(R.id.scoreText)
        val correctAnswersTextView = findViewById<TextView>(R.id.correctAnswersText)
        val incorrectAnswersTextView = findViewById<TextView>(R.id.incorrectAnswersText)

        // Recebe os dados passados pela JogoActivity
        val finalScore = intent.getIntExtra("FINAL_SCORE", 0)
        val correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)
        val incorrectAnswers = intent.getIntExtra("INCORRECT_ANSWERS", 0)

        // Exibe os dados recebidos
        Toast.makeText(this, "Sua pontuação final é: $finalScore", Toast.LENGTH_LONG).show()

        // Atualiza os TextViews com os valores recebidos
        scoreTextView.text = "$finalScore"
        correctAnswersTextView.text = "Acertos: $correctAnswers"
        incorrectAnswersTextView.text = "Erros: $incorrectAnswers"

        // Botão Jogar Novamente
        val btnJogarNovamente: Button = findViewById(R.id.btn_jogar_novamente)
        btnJogarNovamente.setOnClickListener {
            val intent = Intent(this, JogoActivity::class.java) // Substitua com sua activity de jogo
            startActivity(intent)
        }

        // Botão Líderes
        val btnLideres: Button = findViewById(R.id.btn_lideres)
        btnLideres.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java) // Substitua com sua activity de ranking
            startActivity(intent)
        }

        // Botão Tela Inicial
        val btnTelaInicial: Button = findViewById(R.id.btn_tela_inicial)
        btnTelaInicial.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Substitua com sua activity principal
            startActivity(intent)
        }
    }
}
