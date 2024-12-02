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

            // Limita a lista para os 10 primeiros colocados
            val top10Users = topUsers.take(10)

            // Configura o RecyclerView com os 10 primeiros usuários
            val recyclerView = findViewById<RecyclerView>(R.id.rv_ranking)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = UserAdapter(top10Users) // Exibe os 10 primeiros colocados

            // Configura os três primeiros colocados
            setupTopThree(topUsers)
        }
    }

    // Função para obter as pontuações dos usuários do Firestore
//    private fun getUserScores(callback: (List<User>) -> Unit) {
//        db.collection("user_scores")
//            .get()
//            .addOnSuccessListener { result ->
//                val userList = mutableListOf<User>()
//                for (document in result) {
//                    val userId = document.getString("userId") ?: ""
//                    val name = document.getString("name") ?: "Nome não disponível"
//                    val points = document.getLong("points")?.toInt() ?: 0
//
//                    // Cria um objeto User para cada usuário
//                    userList.add(User(name, points))
//                }
//                // Chama a função callback passando a lista de usuários
//                callback(userList)
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Erro ao carregar o ranking: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }

    // Função para calcular a pontuação total de cada usuário
    private fun getUserScores(callback: (List<User>) -> Unit) {
        db.collection("user_scores")
            .get()
            .addOnSuccessListener { result ->
                val userMap = mutableMapOf<String, Int>() // Mapeia o userId para a pontuação total

                // Itera sobre os documentos e soma as pontuações por usuário
                for (document in result) {
                    val userId = document.getString("userId") ?: ""
                    val points = document.getLong("points")?.toInt() ?: 0

                    // Soma a pontuação do usuário no mapa
                    userMap[userId] = userMap.getOrDefault(userId, 0) + points
                }

                // Cria uma lista de usuários com as pontuações somadas
                val userList = mutableListOf<User>()
                val totalUsers = userMap.size // Total de usuários que precisamos buscar

                var usersLoaded = 0 // Contador de usuários carregados

                // Busca o nome de cada usuário
                val userIds = userMap.keys
                for (userId in userIds) {
                    getUserName(userId) { name ->
                        val score = userMap[userId] ?: 0
                        userList.add(User(userId, name , score))  // Cria o objeto User com o nome e a pontuação

                        // Verifica se todos os usuários foram carregados
                        usersLoaded++
                        if (usersLoaded == totalUsers) {
                            // Quando todos os usuários tiverem sido carregados, chama o callback
                            callback(userList)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar o ranking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun getUserName(userId: String, callback: (String) -> Unit) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "Nome não disponível"
                    callback(name)  // Retorna o nome do usuário
                } else {
                    callback("Nome não encontrado")  // Caso o nome não exista
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar o nome do usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                callback("Erro ao carregar nome")  // Caso ocorra algum erro
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
