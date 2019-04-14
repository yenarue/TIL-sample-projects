package nicestring

fun String.isNice(): Boolean {
    return (this.none { this.contains("b[uae]".toRegex()) }.toInt()
            + this.any { this.contains("(.*[aeiou].*){3}".toRegex()) }.toInt()
            + this.any { this.contains("(.)\\1".toRegex()) }.toInt()
            >= 2)
}

fun Boolean.toInt(): Int = if (this) 1 else 0