package com.example.utils

object MorseCodeHelper {
    private val charToMorse = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
        'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
        'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---",
        'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-",
        'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
        'Z' to "--..",
        '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-", '5' to ".....",
        '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.", '0' to "-----",
        ' ' to "/"
    )

    private val morseToChar = charToMorse.entries.associate { (k, v) -> v to k }

    fun textToMorse(text: String): String {
        return text.uppercase().map { charToMorse[it] ?: "" }.filter { it.isNotEmpty() }.joinToString(" ")
    }

    fun morseToText(morse: String): String {
        return morse.split(" ").map { morseToChar[it] ?: '?' }.joinToString("")
    }
}
