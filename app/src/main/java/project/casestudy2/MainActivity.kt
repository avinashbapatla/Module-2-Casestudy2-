package project.casestudy2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.casestudy2.api.ApiClient
import project.casestudy2.models.PromptRequest
import project.casestudy2.utils.PromptEvaluator
import project.casestudy2.utils.PromptGenerator
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var etTopic: EditText
    private lateinit var btnZeroShot: Button
    private lateinit var btnFewShot: Button
    private lateinit var btnStructured: Button
    private lateinit var tvResults: TextView
    private lateinit var tvComparison: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnClear: Button

    private val apiService by lazy { ApiClient.apiService }
    private val promptGenerator = PromptGenerator()
    private val promptEvaluator = PromptEvaluator()

    private val results = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etTopic = findViewById(R.id.etTopic)
        btnZeroShot = findViewById(R.id.btnZeroShot)
        btnFewShot = findViewById(R.id.btnFewShot)
        btnStructured = findViewById(R.id.btnStructured)
        tvResults = findViewById(R.id.tvResults)
        tvComparison = findViewById(R.id.tvComparison)
        progressBar = findViewById(R.id.progressBar)
        btnClear = findViewById(R.id.btnClear)
    }

    private fun setupClickListeners() {
        btnZeroShot.setOnClickListener {
            callApiWithPromptType("zero-shot")
        }

        btnFewShot.setOnClickListener {
            callApiWithPromptType("few-shot")
        }

        btnStructured.setOnClickListener {
            callApiWithPromptType("structured")
        }

        btnClear.setOnClickListener {
            clearResults()
        }
    }

    private fun callApiWithPromptType(promptType: String) {
        val topic = etTopic.text.toString().trim()
        if (topic.isEmpty()) {
            Toast.makeText(this, "Please enter a topic", Toast.LENGTH_SHORT).show()
            return
        }

        val prompt = when (promptType) {
            "zero-shot" -> promptGenerator.generateZeroShotPrompt(topic)
            "few-shot" -> promptGenerator.generateFewShotPrompt(topic)
            "structured" -> promptGenerator.generateStructuredPrompt(topic)
            else -> return
        }

        setLoadingState(true)

        lifecycleScope.launch {
            try {
                val request = PromptRequest(
                    messages = listOf(
                        mapOf("role" to "user", "content" to prompt)
                    ),
                    model = "grok-beta",
                    temperature = 0.7
                )

                val response = withContext(Dispatchers.IO) {
                    apiService.getPromptResponse(request)
                }

                // All UI updates must be on the Main thread
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val responseText = body?.choices?.firstOrNull()?.message?.content
                            ?: "No response received"
                        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                        results[promptType] = responseText
                        updateResultsDisplay(promptType, responseText, timestamp)

                        // Auto-compare when all three responses are received
                        if (results.size == 3) {
                            showComparison()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "No error details"
                        showError("API Error ${response.code()}: $errorBody")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Error: ${e.message ?: "Unknown error occurred"}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    setLoadingState(false)
                }
            }
        }
    }

    private fun updateResultsDisplay(promptType: String, response: String, timestamp: String) {
        val promptLabel = when (promptType) {
            "zero-shot" -> "🔵 Zero-Shot Prompt"
            "few-shot" -> "🟢 Few-Shot Prompt"
            "structured" -> "🟠 Structured Prompt"
            else -> promptType
        }

        val displayText = """
            $promptLabel (${timestamp})
            ${response.take(300)}${if (response.length > 300) "..." else ""}
            
            ${"─".repeat(40)}
            
        """.trimIndent()

        val currentText = tvResults.text.toString()
        tvResults.text = if (currentText.isEmpty() || currentText == "Results will appear here...") {
            displayText
        } else {
            "$currentText\n$displayText"
        }
    }

    private fun showComparison() {
        val comparison = promptEvaluator.comparePrompts(
            results["zero-shot"] ?: "",
            results["few-shot"] ?: "",
            results["structured"] ?: ""
        )

        tvComparison.text = comparison
        tvComparison.visibility = View.VISIBLE
    }

    private fun clearResults() {
        results.clear()
        tvResults.text = "Results will appear here..."
        tvComparison.visibility = View.GONE
        tvComparison.text = ""
    }

    private fun setLoadingState(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnZeroShot.isEnabled = !isLoading
        btnFewShot.isEnabled = !isLoading
        btnStructured.isEnabled = !isLoading
        btnClear.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        setLoadingState(false)
    }
}