package com.example.newmobileproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newmobileproject.databinding.ActivityScoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

class scoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScoreBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura o ViewBinding para acessar os elementos da UI
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        // Recebe a pontuação diária da JogoActivity
        val dailyScore = intent.getIntExtra("NOW_SCORE", 0)

        // Exibe a pontuação na interface do usuário
        binding.UltimaPontuacao.text = dailyScore.toString()
        binding.tvPontuacaoDia.text = dailyScore.toString()

        // Calcula e exibe as pontuações mensais e gerais
        renderScore(dailyScore)

        // Configura o botão de voltar para redirecionar ao MainActivity
        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a atividade atual para evitar empilhamento
        }
    }

    private fun renderScore(dailyScore: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("user_scores")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val scores = calculateMonthlyAndOverallScores(result)
                updateUIWithScores(scores, dailyScore)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar pontuações: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun calculateMonthlyAndOverallScores(result: QuerySnapshot): Pair<Int, Int> {
        var monthlyScore = 0
        var overallScore = 0

        for (document in result) {
            val points = document.getLong("points")?.toInt() ?: 0
            val timestamp = document.getTimestamp("timestamp")?.toDate()

            if (timestamp != null && isCurrentMonth(timestamp)) {
                monthlyScore += points
            }
            overallScore += points
        }
        return Pair(monthlyScore, overallScore)
    }

    private fun updateUIWithScores(scores: Pair<Int, Int>, dailyScore: Int) {
        val (monthlyScore, overallScore) = scores

        // Atualiza as pontuações exibidas no TextView
        binding.tvPontuacaoMes.text = (monthlyScore + dailyScore).toString()
        binding.tvPontuacaoSempre.text = (overallScore + dailyScore).toString()
    }

    private fun isCurrentMonth(date: Date): Boolean {
        val current = Calendar.getInstance()
        val record = Calendar.getInstance().apply { time = date }

        return current.get(Calendar.YEAR) == record.get(Calendar.YEAR) &&
                current.get(Calendar.MONTH) == record.get(Calendar.MONTH)
    }
}
