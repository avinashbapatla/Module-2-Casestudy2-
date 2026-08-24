package project.casestudy2.utils

class PromptEvaluator {

    fun comparePrompts(zeroShot: String, fewShot: String, structured: String): String {
        val zeroScore = evaluateResponse(zeroShot)
        val fewScore = evaluateResponse(fewShot)
        val structuredScore = evaluateResponse(structured)

        // Fix: Compare the score values directly
        val best = when {
            zeroScore.score >= fewScore.score && zeroScore.score >= structuredScore.score -> "Zero-Shot"
            fewScore.score >= zeroScore.score && fewScore.score >= structuredScore.score -> "Few-Shot"
            else -> "Structured"
        }

        return """
            📊 PROMPT STRATEGY COMPARISON RESULTS
            
            ─────────────────────────────────────────────
            
            🔵 Zero-Shot Prompt Score: ${zeroScore.score}/100
               - Length: ${zeroScore.length} characters
               - Structure: ${zeroScore.structure}
               - Clarity: ${zeroScore.clarity}
            
            🟢 Few-Shot Prompt Score: ${fewScore.score}/100
               - Length: ${fewScore.length} characters
               - Structure: ${fewScore.structure}
               - Clarity: ${fewScore.clarity}
            
            🟠 Structured Prompt Score: ${structuredScore.score}/100
               - Length: ${structuredScore.length} characters
               - Structure: ${structuredScore.structure}
               - Clarity: ${structuredScore.clarity}
            
            ─────────────────────────────────────────────
            
            🏆 Best Performing Strategy: $best
            
            💡 Key Insights:
            ${generateInsights(zeroScore, fewScore, structuredScore)}
            
            📈 Recommendation:
            ${generateRecommendation(zeroScore, fewScore, structuredScore)}
            
        """.trimIndent()
    }

    private fun evaluateResponse(response: String): EvaluationResult {
        val length = response.length
        val hasStructure = response.contains("•") ||
                response.contains("\n") ||
                response.matches(Regex(".*\\d+.*")) ||
                response.contains("Key Characteristics") ||
                response.contains("Definition")

        val hasSections = response.contains("Definition") ||
                response.contains("Characteristics") ||
                response.contains("Applications") ||
                response.contains("Conclusion")

        val structureScore = when {
            hasStructure && hasSections -> 35
            hasStructure -> 25
            else -> 15
        }

        val clarityScore = if (response.split(" ").size > 50) 35 else 25
        val lengthScore = when {
            length in 200..800 -> 30
            length in 100..199 || length in 801..1200 -> 20
            else -> 10
        }

        val totalScore = structureScore + clarityScore + lengthScore

        return EvaluationResult(
            score = totalScore,
            length = length,
            structure = if (hasStructure && hasSections) "Excellent" else if (hasStructure) "Good" else "Basic",
            clarity = if (clarityScore >= 30) "Clear" else "Moderate"
        )
    }

    private fun generateInsights(
        zero: EvaluationResult,
        few: EvaluationResult,
        structured: EvaluationResult
    ): String {
        val insights = mutableListOf<String>()

        if (structured.score > zero.score && structured.score > few.score) {
            insights.add("• Structured prompts produce the most comprehensive and well-organized responses")
        }
        if (few.score > zero.score) {
            insights.add("• Few-shot prompting improves response quality over zero-shot")
        }
        if (structured.structure == "Excellent") {
            insights.add("• Explicit formatting instructions significantly enhance output structure")
        }
        if (zero.length > few.length + 100) {
            insights.add("• Zero-shot prompts tend to generate longer, but less focused responses")
        }

        return if (insights.isEmpty()) {
            "• All prompt types show comparable performance\n• Consider using Structured prompts for complex topics"
        } else {
            insights.joinToString("\n")
        }
    }

    private fun generateRecommendation(
        zero: EvaluationResult,
        few: EvaluationResult,
        structured: EvaluationResult
    ): String {
        return when {
            structured.score > few.score && structured.score > zero.score ->
                "Structured prompts are recommended for complex topics requiring detailed, organized explanations."
            few.score > zero.score ->
                "Few-shot prompts provide good balance between guidance and flexibility. Recommended for general use."
            zero.score >= few.score && zero.score >= structured.score ->
                "Zero-shot is sufficient for simple topics. Consider Structured prompts for more complex subjects."
            else ->
                "Mix of strategies recommended based on task complexity."
        }
    }

    data class EvaluationResult(
        val score: Int,
        val length: Int,
        val structure: String,
        val clarity: String
    )
}