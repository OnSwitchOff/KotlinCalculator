
var varsMap = mutableMapOf<String,Int>()

fun main() {
    var f = true

    while (f) {
        val input = readLine()!!.trim()
        if (!input.isNullOrEmpty()) {
            when {
                hasEqual(input) -> addToVars(input)
                isSoloVariable(input) -> printValue(input)
                isExpression(input) -> println(calcExpression(input))
                else -> println("Invalid expression")
            }
        }
    }
}



fun hasEqual(input: String): Boolean = input.matches(Regex(".+=.+"))
fun isSoloVariable(input: String): Boolean = input.matches(Regex("([a-zA-Z]+)|(-?\\d+)"))
fun isExpression(input: String): Boolean = input.matches(Regex(".+([-+*/].+)"))

fun calcExpression(input0: String): String {
    var input = input0
    if (input.matches(Regex("([a-zA-Z]+[0-9]+])|([0-9]+[a-zA-Z]+)"))) return "Invalid expression"

    while (input.matches(Regex("([-+][-+])"))) {
        input = input.replace(Regex("[+][+]"),"+")
        input = input.replace(Regex("[-][-]"),"+")
        input = input.replace(Regex("[-][+]"),"-")
    }

    when {
        input.matches(Regex(".*[(].+([-+*/].+)[)].*")) -> {
            val reg = Regex("[(].+([-+*/].+)[)]")
            return calcExpression(input.replace(reg,calcExpression(reg.find(input)!!.value)))
        }
        input.matches(Regex(".+[-]?([\\d]+)|([a-zA-Z]+)[*][-]?([\\d]+)|([a-zA-Z]+).+")) -> {
            val reg = Regex("[-]?([\\d]+)|([a-zA-Z]+)[*][-]?([\\d]+)|([a-zA-Z]+)")
            return calcExpression(input.replace(reg,calcExpression(reg.find(input)!!.value)))
        }
        input.matches(Regex("[-]?([\\d]+)|([a-zA-Z]+)[*][-]?([\\d]+)|([a-zA-Z]+)")) -> {
            val left = input.substringBefore("*")
            var l: Int? = null
            if (left.matches(Regex("[a-zA-Z]+"))) {
                if (varsMap.containsKey(left.replace("-",""))) {
                    l = if (left.contains("-")) {
                        - (varsMap[left.replace("-","")]!!)
                    } else {
                        varsMap[left.replace("-","")]
                    }
                }
            } else {
                l = left.toInt()
            }
            val right = input.substringAfter("*")
            var r: Int? = null
            if (right.matches(Regex("[a-zA-Z]+"))) {
                if (varsMap.containsKey(right.replace("-",""))) {
                    r = if (right.contains("-")) {
                        - (varsMap[right.replace("-","")]!!)
                    } else {
                        varsMap[right.replace("-","")]
                    }
                }
            } else {
                r = right.toInt()
            }
            if (left == null || right == null) return "Unknown variable"
        }
    }
}

fun addToVars(input: String) {
    val left = input.substringBefore("=").trim()
    val right = input.substringAfter("=").trim()

    when {
        !left.matches(Regex("[a-zA-Z]+")) -> println("Invalid identifier")
        !right.matches(Regex("(-?\\d+)|([a-zA-Z]+)")) -> println("Invalid assignment")
        left.matches(Regex("[a-zA-Z]+")) && right.matches(Regex("-?d+")) -> varsMap[left] = right.toInt()
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


