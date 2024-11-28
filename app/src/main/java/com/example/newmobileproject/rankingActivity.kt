package com.example.newmobileproject

import UserAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RankingActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Lista de usuários
        val userList = mutableListOf<User>()

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            // Fecha a activity e volta para a tela anterior
            onBackPressed()
        }


        // Recupera os pontos de todos os usuários no Firestore
        getUserScores { sortedUserList ->
            // Ordena os usuários por pontuação (decrescente)
            val topUsers = sortedUserList.sortedByDescending { it.score }

            // Configura o RecyclerView com os usuários (a partir do 4º colocado)
            val recyclerView = findViewById<RecyclerView>(R.id.rv_ranking)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = UserAdapter(topUsers.drop(3)) // Exibe a partir do 4º lugar

            // Configura os três primeiros colocados
            setupTopThree(topUsers)
        }
    }

    // Função para obter as pontuações dos usuários do Firestore
    private fun getUserScores(callback: (List<User>) -> Unit) {
        db.collection("user_scores")
            .get()
            .addOnSuccessListener { result ->
                val userList = mutableListOf<User>()
                for (document in result) {
                    val userId = document.getString("userId") ?: ""
                    val name = document.getString("name") ?: "Nome não disponível"
                    val points = document.getLong("points")?.toInt() ?: 0

                    // Cria um objeto User para cada usuário
                    userList.add(User(name, points))
                }
                callback(userList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar o ranking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    // Configura os 3 primeiros colocados
    private fun setupTopThree(sortedUserList: List<User>) {
        if (sortedUserList.isNotEmpty()) {
            findViewById<TextView>(R.id.top1Name).text = sortedUserList[0].name
            findViewById<TextView>(R.id.top1Score).text = "${sortedUserList[0].score} Pontos"
        }
        if (sortedUserList.size > 1) {
            findViewById<TextView>(R.id.top2Name).text = sortedUserList[1].name
            findViewById<TextView>(R.id.top2Score).text = "${sortedUserList[1].score} Pontos"
        }
        if (sortedUserList.size > 2) {
            findViewById<TextView>(R.id.top3Name).text = sortedUserList[2].name
            findViewById<TextView>(R.id.top3Score).text = "${sortedUserList[2].score} Pontos"
        }
    }
}


