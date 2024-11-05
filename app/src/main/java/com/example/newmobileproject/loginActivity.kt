package com.example.newmobileproject

import com.example.newmobileproject.R
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newmobileproject.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST



class loginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    // apiService
    private val apiService: ApiService = RetrofitClient.getClient().create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa as views

        // Configura o padding para edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Listener do botão Cadastrar


        binding.buttonCadastrar.setOnClickListener {
            val intent = Intent(this, cadastroActivity::class.java)
            startActivity(intent)
        }


        // Listener do botão Login
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val senha = binding.editTextSenha.text.toString()

            // Validação dos dados
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Autenticação do usuário
            val loginRequest = LoginRequest(email, senha)
            val call = apiService.login(loginRequest)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        // Autenticação bem-sucedida
                        val token = response.body()?.token
                        // Salve o token em SharedPreferences ou outro mecanismo de persistência
                        // Navegue para a tela principal do aplicativo
                        // ...
                        Toast.makeText(this@loginActivity, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Autenticação falhou
                        Toast.makeText(this@loginActivity, "Email ou senha inválidos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Erro na requisição
                    Toast.makeText(this@loginActivity, "Erro na requisição", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

// Interface ApiService
interface ApiService {
    @POST("login") // Substitua pelo endpoint correto
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}

// Classe RetrofitClient
object RetrofitClient {
    private const val BASE_URL = "https://sua-api.com/" // Substitua pela URL da sua API

    fun getClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

// Classes de dados
data class LoginRequest(val email: String, val senha: String)
data class LoginResponse(val token: String?)