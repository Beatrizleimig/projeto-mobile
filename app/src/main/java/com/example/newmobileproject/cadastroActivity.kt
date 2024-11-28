package com.example.newmobileproject

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newmobileproject.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class cadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance() // Inicializa o Firestore
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener do botão Cadastrar
        binding.buttonCadastrar.setOnClickListener {
            val nome = binding.editTextNome.text.toString()
            val email = binding.editTextEmail.text.toString()
            val senha = binding.editTextSenha.text.toString()
            val confirmarSenha = binding.editTextConfirmarSenha.text.toString()

            // Validação dos dados
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lógica de cadastro (criar usuário no Firebase Auth)
            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Cadastro realizado com sucesso
                        Log.d("teste", "createUserWithEmail:success")

                        // Recupera o usuário autenticado
                        val user = auth.currentUser
                        if (user != null) {
                            // Salva o nome e o ID do usuário no Firestore
                            val userData = hashMapOf(
                                "userId" to user.uid,
                                "name" to nome,
                                "points" to 0  // Pontuação inicial do usuário
                            )

                            // Salva no Firestore
                            db.collection("user_scores")
                                .document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                    finish() // Volta para a tela de login ou para outra atividade
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        // Se o cadastro falhar, mostra uma mensagem de erro
                        Log.w("teste", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Autenticação falhou.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
