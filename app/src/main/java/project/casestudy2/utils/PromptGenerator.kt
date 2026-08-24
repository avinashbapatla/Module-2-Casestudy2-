package project.casestudy2.utils

class PromptGenerator {

    fun generateZeroShotPrompt(topic: String): String {
        return """
            Explain the concept of '$topic' in a clear and concise manner. 
            Provide a definition, key characteristics, and real-world applications.
            Format your response with clear sections.
        """.trimIndent()
    }

    fun generateFewShotPrompt(topic: String): String {
        return """
            Here are some examples of well-structured explanations:
            
            Example 1 - Artificial Intelligence:
            Definition: AI is the simulation of human intelligence in machines.
            Key Characteristics:
            - Learning from data
            - Pattern recognition
            - Decision-making
            Applications: Healthcare, Finance, Transportation
            
            Example 2 - Blockchain:
            Definition: A distributed ledger technology for secure transactions.
            Key Characteristics:
            - Decentralization
            - Immutability
            - Transparency
            Applications: Cryptocurrency, Supply Chain, Voting
            
            Now, following this same structure, explain '$topic':
            
            Definition: 
            Key Characteristics:
            - 
            - 
            - 
            Applications:
        """.trimIndent()
    }

    fun generateStructuredPrompt(topic: String): String {
        return """
            TASK: Provide a comprehensive explanation of '$topic'
            
            FORMAT REQUIREMENTS:
            1. Begin with a clear, one-sentence definition
            2. List 3-5 key characteristics with brief explanations
            3. Include 3 real-world applications
            4. Add a conclusion summarizing the importance
            
            CONSTRAINTS:
            - Use professional, academic language
            - Be objective and factual
            - Keep total response under 300 words
            
            OUTPUT STRUCTURE:
            [Definition]
            [Key Characteristics]
                • Characteristic 1
                • Characteristic 2
                • Characteristic 3
            [Applications]
                • Application 1
                • Application 2
                • Application 3
            [Conclusion]
            
            Please proceed with the explanation.
        """.trimIndent()
    }
}