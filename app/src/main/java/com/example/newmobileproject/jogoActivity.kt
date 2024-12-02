package com.example.newmobileproject

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newmobileproject.databinding.ActivityJogoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class JogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJogoBinding
    private lateinit var db: FirebaseFirestore
    private var questions = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0
    private var correctAnswers = 0
    private var incorrectAnswers = 0
    private var countDownTimer: CountDownTimer? = null
    private var isTimeUp = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJogoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore

        // Inicializa o texto da pontuação
        binding.scoreText.text = "Pontuação: $score"

        loadQuestionsFromFirestore()

        binding.option1.setOnClickListener { checkAnswer(0) }
        binding.option2.setOnClickListener { checkAnswer(1) }
        binding.option3.setOnClickListener { checkAnswer(2) }
        binding.option4.setOnClickListener { checkAnswer(3) }



    }

    private fun loadQuestionsFromFirestore() {
        db.collection("hp")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val question = document.toObject(Question::class.java)
                    val correctOptionIndex = question.options.indexOf(question.correct_option)

                    // Apenas adiciona se encontrar uma resposta válida
                    if (correctOptionIndex >= 0 && question.options.size == 4) {
                        questions.add(
                            Question(
                                question = question.question,
                                options = question.options,
                                correct_option = correctOptionIndex.toString()
                            )
                        )
                    }
                }
                // Limita para as primeiras 10 questões e embaralha
                if (questions.isNotEmpty()) {
                    questions = questions.shuffled().take(10).toMutableList()
                    loadQuestion() // Carrega a primeira pergunta
                } else {
                    Toast.makeText(this, "Nenhuma pergunta encontrada!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar perguntas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadQuestion() {
        if (currentQuestionIndex >= questions.size) return // Previne erro se o índice ultrapassar
        val question = questions[currentQuestionIndex]
        binding.questionCounter.text = "Questão ${currentQuestionIndex + 1} de ${questions.size}"
        binding.questionText.text = question.question
        binding.option1.text = question.options[0]
        binding.option2.text = question.options[1]
        binding.option3.text = question.options[2]
        binding.option4.text = question.options[3]

        resetProgressBar() // Reseta o progresso do ProgressBar
        startTimer(12) // 20 segundos por questão


        // Resetando a variável isTimeUp antes de carregar a nova pergunta
        isTimeUp = false
    }
    private fun resetProgressBar() {
        // Cancela o temporizador atual (se estiver ativo) e reseta o progresso
        countDownTimer?.cancel()
        binding.timerProgressBar.progress = 0
    }

    private fun startTimer(durationInSeconds: Int) {
        val progressBarMax = durationInSeconds * 1000
        binding.timerProgressBar.max = progressBarMax

        // Cancela o temporizador anterior (se existir)
        countDownTimer?.cancel()

        // Inicia um novo temporizador
        countDownTimer = object : CountDownTimer(progressBarMax.toLong(), 100) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerProgressBar.progress = (progressBarMax - millisUntilFinished).toInt()
            }

            override fun onFinish() {
                // Atualiza o progresso para o máximo e avisa o usuário
                binding.timerProgressBar.progress = progressBarMax
                Toast.makeText(this@JogoActivity, "Tempo esgotado!", Toast.LENGTH_SHORT).show()

                // Marca a resposta como errada quando o tempo acabar
                isTimeUp = true
                loadNextQuestionOrEndQuiz()
            }
        }
        countDownTimer?.start() // Inicia o temporizador
    }


//    private fun startTimer(durationInSeconds: Int) {
//        val progressBarMax = durationInSeconds * 1000
//        binding.timerProgressBar.max = progressBarMax
//
//        // Cancela o temporizador anterior (se existir)
//        countDownTimer?.cancel()
//
//        // Inicia um novo temporizador
//        countDownTimer = object : CountDownTimer(progressBarMax.toLong(), 100) {
//            override fun onTick(millisUntilFinished: Long) {
//                binding.timerProgressBar.progress = (progressBarMax - millisUntilFinished).toInt()
//            }
//
//            override fun onFinish() {
//                // Atualiza o progresso para o máximo e avisa o usuário
//                binding.timerProgressBar.progress = progressBarMax
//                Toast.makeText(this@JogoActivity, "Tempo esgotado!", Toast.LENGTH_SHORT).show()
//
//                // Carrega a próxima pergunta ou finaliza o quiz
//                loadNextQuestionOrEndQuiz()
//            }
//        }
//        countDownTimer?.start() // Inicia o temporizador
//    }



    private fun checkAnswer(selectedIndex: Int) {
        val correctIndex = questions[currentQuestionIndex].correct_option.toInt()

        // Cancela o temporizador atual, já que o jogador respondeu
        countDownTimer?.cancel()

        if (isTimeUp) {
            incorrectAnswers++
            Toast.makeText(this, "Tempo esgotado! Resposta errada.", Toast.LENGTH_SHORT).show()
        }

        // Verifica se a resposta está correta
        if (selectedIndex == correctIndex) {
            correctAnswers++
            score += 10
            Toast.makeText(this, "Correto! Pontuação: $score", Toast.LENGTH_SHORT).show()

            // Atualiza o texto da pontuação no TextView
            binding.scoreText.text = "Pontuação: $score"

            when (selectedIndex) {
                0 -> binding.option1.setBackgroundColor(resources.getColor(R.color.holo_green_light))
                1 -> binding.option2.setBackgroundColor(resources.getColor(R.color.holo_green_light))
                2 -> binding.option3.setBackgroundColor(resources.getColor(R.color.holo_green_light))
                3 -> binding.option4.setBackgroundColor(resources.getColor(R.color.holo_green_light))
            }
        } else {
            incorrectAnswers++
            Toast.makeText(
                this,
                "Errado! A resposta correta é: ${questions[currentQuestionIndex].options[correctIndex]}",
                Toast.LENGTH_SHORT
            ).show()

            // Atualiza as cores conforme a resposta incorreta
            when (selectedIndex) {
                0 -> binding.option1.setBackgroundColor(resources.getColor(R.color.holo_red_light))
                1 -> binding.option2.setBackgroundColor(resources.getColor(R.color.holo_red_light))
                2 -> binding.option3.setBackgroundColor(resources.getColor(R.color.holo_red_light))
                3 -> binding.option4.setBackgroundColor(resources.getColor(R.color.holo_red_light))
            }
            when (correctIndex) {
                0 -> binding.option1.setBackgroundColor(resources.getColor(R.color.holo_green_light))
                1 -> binding.option2.setBackgroundColor(resources.getColor(R.color.holo_green_light))
                2 -> binding.option3.setBackgroundColor(resources.getColor(R.color.holo_green_light))
                3 -> binding.option4.setBackgroundColor(resources.getColor(R.color.holo_green_light))
            }
        }

        // Atraso antes de carregar a próxima pergunta
        Handler(Looper.getMainLooper()).postDelayed({
            loadNextQuestionOrEndQuiz()
            resetButtonColors()
        }, 2000)
    }





    private fun resetButtonColors() {
        // Restaura a cor original dos botões
        binding.option1.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        binding.option2.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        binding.option3.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        binding.option4.setBackgroundColor(resources.getColor(android.R.color.darker_gray))

    }

    private fun saveScoreToFirestore(score: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()

            // Cria um mapa com os dados da pontuação
            val scoreData = mapOf(
                "userId" to userId,
                "points" to score,
                "timestamp" to FieldValue.serverTimestamp()
            )

            // Adiciona os dados a uma nova entrada na coleção "user_scores"
            db.collection("user_scores")
                .add(scoreData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pontuação salva com sucesso!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar pontuação: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }
    }



    private fun loadNextQuestionOrEndQuiz() {
        resetProgressBar() // Reseta o progresso e cancela o temporizador
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            loadQuestion()
        } else {
            Toast.makeText(this, "Fim do quiz!", Toast.LENGTH_SHORT).show()

            // Salvar a pontuação no Firebase antes de mudar de atividade
            saveScoreToFirestore(score)

            // Redirecionar para as atividades com os dados necessários
            val scoreIntent = Intent(this, scoreActivity::class.java)
            scoreIntent.putExtra("NOW_SCORE", score)
            startActivity(scoreIntent)


            val geralIntent = Intent(this, geralActivity::class.java)
            geralIntent.putExtra("FINAL_SCORE", score)
            geralIntent.putExtra("CORRECT_ANSWERS", correctAnswers)
            geralIntent.putExtra("INCORRECT_ANSWERS", incorrectAnswers)
            startActivity(geralIntent)

            finish()
        }
    }




    data class Question(
        val question: String = "",
        val options: List<String> = emptyList(),
        val correct_option: String = "" // A resposta correta como string


    )}