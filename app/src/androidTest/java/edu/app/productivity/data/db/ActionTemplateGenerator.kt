package edu.app.productivity.data.db

object ActionTemplateGenerator {

    fun randomTemplateName(lenRange: IntRange = 8..64): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('А'..'Я') + ('а'..'я') + ('0'..'9')
        val len = lenRange.random()
        return (1..len).map { allowedChars.random() }.joinToString("")
    }

    fun generate(
        templateName: String = randomTemplateName(16..32),
        actionsCount: Int = (1..7).random()
    ) = ActionsTemplateEntity(
        templateName,
        Array(actionsCount) { _ -> ActionEntityGenerator.generate().toAction() }.toList()
    )
}
