package com.example.newmobileproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newmobileproject.databinding.ActivityScoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

        showScores()

        // Configura o botão de voltar para redirecionar ao MainActivity
        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a atividade atual para evitar empilhamento
        }
    }

    private fun showScores() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (userId.isEmpty()) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        getOverallScore(userId)
    }

    private fun sumPointsForToday(userScores: List<UserScore>): Int {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.time = Date()

        // Define the start and end of the day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.time

        return userScores.filter { it.timestamp in startOfDay..endOfDay }
            .sumOf { it.points }
    }

    private fun sumPointsForCurrentMonth(querySnapshot: QuerySnapshot): Int {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.time = Date()

        // Define the start of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.time

        // Define the end of the month
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.time

        val userScores = querySnapshot.toObjects(UserScore::class.java)
        return userScores.filter { it.timestamp in startOfMonth..endOfMonth }
            .sumOf { it.points }
    }

    private fun getOverallScore(userId: String) {
        db.collection("user_scores")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val userScores = result.toObjects(UserScore::class.java)
                var overallScore = 0

                if (userScores.isEmpty()) {
                    return@addOnSuccessListener
                }

                userScores.forEach {
                    overallScore += it.points
                    Log.d("test", "daily: $it")
                }

                binding.UltimaPontuacao.text = "${userScores.first().points}"
                binding.tvPontuacaoSempre.text = "$overallScore"
                binding.tvPontuacaoDia.text = "${sumPointsForToday(userScores)}"
                binding.tvPontuacaoMes.text = "${sumPointsForCurrentMonth(result)}"
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar pontuações: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("test", e.toString())
            }
    }

    data class UserScore(
        val userId: String = "",
        val points: Int = 0,
        val timestamp: Date = Date(),
    )
}