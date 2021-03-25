import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter


fun main() = runBlocking {
    val filepath = "words.txt"
    val words: MutableSet<String> = mutableSetOf()
    try {
        FileReader(filepath).use { reader ->
            reader.forEachLine {
                words.add(it)
            }
        }
    } catch (e: FileNotFoundException) {
        println("Невозможно открыть файл \"$filepath\"")
        exitProcess(0)
    }

    val minLength = 8
    val roots = words.filter { it.length >= minLength }

    val chosenWord = roots.random()
    val endSymbol = "."
    println("Составьте как много слов из букв слова \"$chosenWord\" и введите каждое на новой строке\nВ конце поставьте \"$endSymbol\"")

    val usersWords: MutableSet<String> = mutableSetOf()
    while (true) {
        val userWord = readLine()
        if (userWord == null || userWord == endSymbol) {
            break
        }
        usersWords.add(userWord.toLowerCase())
    }
    val scoreAwait = async {
        countUserScore(usersWords, words)
    }

    val outputFile = "userWords.txt"
    try {
        val outFile = FileWriter(outputFile)
        outFile.use { writer ->
            usersWords.forEach { writer.write(it + '\n') }
        }
    } catch (e: Exception) {
        println("Невозможно записать слова в файл \"$outputFile\"")
    }

    val score = scoreAwait.await()
    println("Ваш счет равен $score баллам")
}

fun countUserScore(src: Set<String>, checkIn: Set<String>): Int {
    var userScore = 0
    src.forEach {
        if (checkIn.contains(it)) {
            userScore += it.length
        }
    }
    return userScore
}