package mastermind

data class Evaluation(val rightPosition: Int, val wrongPosition: Int)

fun evaluateGuess_old(secret: String, guess: String): Evaluation {
    val secretMap : HashMap<Int, Char> = HashMap()
    val guessMap : HashMap<Int, Char> = HashMap()

    for ((index, c) in secret.withIndex()) secretMap.put(index, c)
    for ((index, c) in guess.withIndex()) guessMap.put(index, c)

    for ((index, c) in guess.withIndex()) {
        secretMap[index] = if (secret[index] == c) 'R' else secretMap[index]!!
        guessMap[index] = if (secret[index] == c) 'X' else guessMap[index]!!
    }

    // 후보1
    for (c in guessMap.values) {
        if (secretMap.values.contains(c)) {
            secretMap[secretMap.values.indexOf(c)] = 'W'
        }
    }

    // 후보2
//    for ((index, c) in secretMap.values.withIndex()) {
//        val (guessIndex, replacedSecretValue) =
//                if (guessMap.values.contains(c)) guessMap.values.indexOf(c) to 'W'
//                else -1 to secretMap[index]!!
//
//        secretMap[index] = replacedSecretValue
//        guessMap[guessIndex] = 'X'
//    }

//    val rightPosition = secretMap.values.filter { it == 'R' }.count()
//    val wrongPosition = secretMap.values.filter { it == 'W' }.count()

    // 이터레이션으로 바꾸면
    var (rightPosition, wrongPosition) = 0 to 0
    for (c in secretMap.values) {
        rightPosition += if (c == 'R') 1 else 0
        wrongPosition += if (c == 'W') 1 else 0
    }

    return Evaluation(rightPosition, wrongPosition)
}


fun evaluateGuess(secret: String, guess: String): Evaluation {

    val rightPositions = secret.zip(guess).count { it.first == it.second }

    val commonLetters = "ABCDEF".sumBy { ch ->
        Math.min(secret.count { it == ch }, guess.count { it == ch })
    }
    return Evaluation(rightPositions, commonLetters - rightPositions)
}
