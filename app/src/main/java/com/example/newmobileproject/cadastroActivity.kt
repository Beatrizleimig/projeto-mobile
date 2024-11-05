package com.example.newmobileproject

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class cadastroActivity : AppCompatActivity() {

    private lateinit var editTextNome: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextSenha: EditText
    private lateinit var editTextConfirmarSenha: EditText
    private lateinit var buttonCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        // Inicializa as views
        editTextNome = findViewById(R.id.editTextNome)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextSenha = findViewById(R.id.editTextSenha)
        editTextConfirmarSenha = findViewById(R.id.editTextConfirmarSenha)
        buttonCadastrar = findViewById(R.id.buttonCadastrar)

        // Listener do botão Cadastrar
        buttonCadastrar.setOnClickListener {

            val nome = editTextNome.text.toString()
            val email = editTextEmail.text.toString()
            val senha = editTextSenha.text.toString()
            val confirmarSenha = editTextConfirmarSenha.text.toString()

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

            // Lógica de cadastro (salvar no banco de dados, enviar para servidor, etc.)
            // ...

            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            finish() // Volta para a tela de login
        }
    }
}