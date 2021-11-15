import java.math.BigInteger

var varsMap = mutableMapOf<String,BigInteger>()
var f = true
fun main() {
    while (f) {
        val input = readLine()!!.replace(Regex("\\s"),"")
        if (input.isNotEmpty()) {
            when {
                isCommand(input) -> executeCommand(input)
                hasEqual(input) -> addToVars(input)
                isSoloVariable(input) -> printValue(input)
                isExpression(input) -> println(calcExpression(input))
                else -> println("Invalid expression")
            }
        }
    }
}


fun isCommand(input: String): Boolean = input.matches(Regex("[/].+"))
fun hasEqual(input: String): Boolean = input.matches(Regex(".+=.+"))
fun isSoloVariable(input: String): Boolean = input.matches(Regex("([a-zA-Z]+)|(-?\\d+)"))
fun isExpression(input: String): Boolean = input.matches(Regex(".+([-+*/].+)"))

fun calcExpression(input0: String): String {
    var input = input0
    if (input.matches(Regex("([a-zA-Z]+[0-9]+])|([0-9]+[a-zA-Z]+)|([*/]{2,})"))) return "Invalid expression"


    val matchResult = Regex("[a-zA-Z]").findAll(input)
        .map { r -> r.value }
        .sortedByDescending { r -> r.length }
    for (match in matchResult) {
        if (varsMap.containsKey(match)) {
            input = input.replace(match, varsMap[match].toString())
        } else {
            return "Unknown variable"
        }
    }

    while (input.matches(Regex("(.+[-+][-+])|(.+[-+][-+].+)|([-+][-+].+)"))) {
        input = input.replace(Regex("[+][+]"),"+")
        input = input.replace(Regex("[-][-]"),"+")
        input = input.replace(Regex("[-][+]"),"-")
        input = input.replace(Regex("[+][-]"),"-")
    }

    when {
        input.matches(Regex(".*[(][-]?[0-9]+([-+*/][-]?[0-9]+)[)].*")) -> {
            val reg = Regex("[(][0-9-+*/]+([-+*/][0-9-+*/]+)[)]")
            val newExp = reg.find(input)!!.value
            return calcExpression(input.replace(newExp,calcExpression(newExp.substring(1,newExp.lastIndex))))
        }
        input.matches(Regex("[-]?[\\d]+[*][-]?[\\d]+")) -> {
            val left = BigInteger(input.substringBefore("*"))
            val right = BigInteger(input.substringAfter("*"))
            return (left * right).toString()
        }
        input.matches(Regex(".*[\\d]+[*][-]?[\\d]+.*")) -> {
            val reg = Regex("[\\d]+[*][-]?([\\d]+)")
            val newExp = reg.find(input)!!.value
            return calcExpression(input.replace(newExp,calcExpression(newExp)))
        }
        input.matches(Regex("[-]?[\\d]+[/][-]?[\\d]+")) -> {
            val left = BigInteger(input.substringBefore("/"))
            val right = BigInteger(input.substringAfter("/"))
            return (left / right).toString()
        }
        input.matches(Regex(".*[\\d]+[/][-]?[\\d]+.*")) -> {
            val reg = Regex("[\\d]+[/][-]?([\\d]+)")
            val newExp = reg.find(input)!!.value
            return calcExpression(input.replace(newExp,calcExpression(newExp)))
        }
        input.matches(Regex("[-]?[\\d]+[+][\\d]+")) -> {
            val left = BigInteger(input.substringBefore("+"))
            val right = BigInteger(input.substringAfter("+"))
            return (left + right).toString()
        }
        input.matches(Regex("[-]?[\\d]+[-][\\d]+")) -> {
            val left = BigInteger(input.substringBefore("-"))
            val right = BigInteger(input.substringAfter("-"))
            return (left - right).toString()
        }
        input.matches(Regex("[-]?[\\d]+[+-][\\d].+")) -> {
            val reg = Regex("[-]?[\\d]+[+-][\\d]+")
            val newExp = reg.find(input)!!.value
            return calcExpression(input.replace(newExp,calcExpression(newExp)))
        }
        else -> return if (Regex("[-]?[\\d]+").matches(input)) input else "Invalid expression"
    }
}

fun addToVars(input: String) {
    val left = input.substringBefore("=").trim()
    val right = input.substringAfter("=").trim()

    when {
        !left.matches(Regex("[a-zA-Z]+")) -> println("Invalid identifier")
        !right.matches(Regex("(-?\\d+)|([a-zA-Z]+)")) -> println("Invalid assignment")
        left.matches(Regex("[a-zA-Z]+")) && right.matches(Regex("-?\\d+")) -> varsMap[left] = BigInteger(right)
        left.matches(Regex("[a-zA-Z]+")) && right.matches(Regex("[a-zA-Z]+")) -> {
            if (varsMap.containsKey(right)) {
                varsMap[left] = varsMap[right]!!
            } else {
                println("Unknown variable")
            }
        }
        else -> println("Invalid assignment")
    }
}
fun printValue(input: String) {
    when {
        input.matches(Regex("-?\\d+")) -> println(input)
        input.matches(Regex("[a-zA-Z]+")) -> {
            if (varsMap.containsKey(input)) {
                println(varsMap[input])
            } else {
                println("Unknown variable")
            }
        }
    }
}

fun executeCommand(input: String) {
    when (input) {
        "/exit" -> {
            println("Bye!")
            f = false
        }
        "/help" -> println("The program calculates the sum of numbers")
        else -> println("Unknown command")
    }
}






