package calculator

import java.math.BigInteger

fun main() {

    val varList = mutableMapOf<String, BigInteger>()
    val allCorrect = "[()]*\\s*-?[\\w\\d]+\\s*[()]*\\s*[-+*/]+\\s*[()]*\\s*-?[\\w\\d]+[()]*(\\s*[()]*\\s*[-+*/]+?\\s*[()]*\\s*-?[\\w\\d]+?)*\\s*[()]*".toRegex()


    while (true) {
        val input = readln()
        if (input == "") {
            // val numList = input.split(" ").map {it.toInt()}
            // println()
        } else if (input.matches(Regex(".*[*/]{2,}.*"))) {
            println("Invalid expression")
        } else if (input.matches(Regex("[-+]?[\\d]+"))) {
            println(input.trim().toInt())
        } else if (input.matches(Regex("\\s*[a-zA-Z]+\\s*=\\s*-?([a-zA-Z]+|[0-9]+)\\s*"))) {
            varyMap(input, varList)
        } else if (input.matches(Regex("\\s*[a-zA-Z]+\\s*"))) {
            printVal(input, varList)
        } else if (input.matches(Regex("([a-zA-Z0-9]+\\s*=\\s*-?[a-zA-Z0-9]+)|([a-zA-Z]+\\s*=\\s*.*=.*)"))) {
            println("Invalid assignment")
        } else if (input.matches(Regex("[\\w]+\\s*=\\s*-?([a-zA-Z]+|[0-9]+)"))) {
            println("Invalid identifier")
        } else if (input.matches(allCorrect)) {
            var c = 0
            var d = 0
            for (s in input) {
                if (s == '(') {
                    ++c
                } else if (s == ')') ++d
            }
            try {
                if (c != d) throw Exception("Invalid expression")
            } catch (e: Exception) {
                println("Invalid expression")
                continue
            }

            println(sumWithBrackets(input, varList))
            // println()
        } else if (input.matches(Regex("/help"))) {
            println("The program calculates the sum of numbers")
            // break
        } else if (input.matches(Regex("/exit"))) {
            println("Bye!")
            break
        } else if (input.matches(Regex("/.*"))) {
            println("Unknown command")
        } else {
            println("Invalid expression")
        }
    }
}

fun sumNumbers(inp: String, map: MutableMap<String, BigInteger>): BigInteger {
    var nums = mutableListOf<BigInteger>()
    var signs = mutableListOf<BigInteger>()
    var sum: BigInteger = BigInteger.valueOf(0)
    // var result = 0
    // Todo: Implement for signs other than + and -

    if (inp.contains("[a-zA-Z]".toRegex())) {
        return printWithVal(inp, map)
    } else {
        inp.split(" ").forEach {
            if (it.matches(Regex("-?[0-9]+"))) {
                nums.add(it.toBigInteger())
            } else {
                signs.add(sign(it))
            }
        }

        for (x in 0..nums.size - 2) {
            nums[x + 1] = nums[x + 1] * signs[x]
        }

        for (s in nums) {
            sum += s
        }
        return sum
    }

    return BigInteger.ONE
}

fun sign(sig: String): BigInteger {
    var result = 1
    var list = mutableListOf<Int>()
    for (x in sig) {
        if (x == '+') {
            list.add(1)
        } else if (x == '-') {
            list.add(-1)
        }
    }

    for (w in list) {
        result *= w
    }
    return result.toBigInteger()
}

fun varyMap(sig: String, map: MutableMap<String, BigInteger>) {
    val beta = sig.replace("\\s+".toRegex(), "")
    val (a, b) = beta.split("=")
    // Todo: There should be a provision for letter input already existing
    if (b.matches(Regex("[a-zA-Z]+"))) {
        try {
//            map[b] = 0
//            println("value of b: $b")
            map[a] = map[b]?: throw UnknownVariableException()
        } catch (e: UnknownVariableException) {
            println("Unknown variable")
        }
        // println(map)
    } else if (b.matches(Regex("-?[0-9]+"))) {
        map[a] = b.trim().toBigInteger()
    }

    // return map
}

fun printVal(sig: String, map: MutableMap<String, BigInteger>) {
    var sigs = sig.trim()
    println(map[sigs]?: "Unknown Variable")
}

fun printWithVal(sig: String, map: MutableMap<String, BigInteger>): BigInteger {
    var nums = mutableListOf<BigInteger>()
    var signs = mutableListOf<BigInteger>()
    var sum = BigInteger.valueOf(0)
    // var result = 0
    sig.split(" ").forEach {
        if (it.matches(Regex("-?[0-9]+"))) {
            nums.add(it.toBigInteger())
        }

        if (it.matches(Regex("[a-zA-Z]+"))) {
            nums.add(map.getValue(it))
        }

        if (it.matches(Regex("[-+]+"))) {
            signs.add(sign(it))
        }
    }

    for (x in 0..nums.size - 2) {
        nums[x + 1] = nums[x + 1] * signs[x]
    }

    for (s in nums) {
        sum += s
    }
    return sum
}


fun sumWithBrackets(sig: String, map: MutableMap<String, BigInteger>): BigInteger {
    val sigs = sig.replace("\\s+".toRegex(), " ")
    var line = ""
    var opened = 0
    var closed = 0
    for (s in sigs) {
        if (s == '(') {
            ++opened
        } else if (s == ')') ++closed
    }

    var numReg = "\\w+".toRegex()
    var opera = "[-+*/]".toRegex()
    val brac = "[()]".toRegex()

    for (s in sigs.indices) {

        if (sigs[s].toString().matches(numReg) || sigs[s].toString().matches(opera) || sigs[s].toString().matches(brac)){
            if (sigs[s].toString().matches(Regex("[a-zA-Z]+"))) {
                line += map.getValue(sigs[s].toString())
            } else  line += sigs[s].toString()
        }
        if (sigs[s] == ' ') line += sigs[s].toString()

    }

    /////////////////////////////////////////////////////////

    val op = "[-+]{2,}".toRegex().findAll(line).toMutableList()
    val opLi = mutableListOf<String>() //list of all items in bracket
    for (a in op) {
        opLi.add(solveSign(a.value))
    }
    for (o in opLi.indices) {
        opLi[o] = if (opLi[o] == "1") "+" else "-"
    }
    val reOpli = opLi.iterator()
    var len = 0
    for (a in op) {
        len = a.value.length
        line = line.replace("[${a.value}]{$len}".toRegex(), reOpli.next())
    }


    /***************************************************/
    var count = 0
    var c = 0
    for (li in line) {
        if (li == '(') ++count
    }

    repeat (count) {
        var matchBrac = "[(]\\s*\\w+(\\s*[-+*/]\\s*\\w+\\s*)+[)]".toRegex().findAll(line).toMutableList()
        for (b in matchBrac) {
            var mm = b.value.replace("[()]".toRegex(), "").trim().split(" ")
            var remm = mm.iterator()
//            println(remm.next())
            if (remm.hasNext()) {
                line = line.replace("[(]\\s*${mm[c]}\\s*[${mm[c + 1]}]\\s*${mm[c + 2]}(\\s*[-+*/]\\s*\\w+)*[)]".toRegex(), solve(mm.toMutableList()))
            }
            c = 0

        }
        c = 0
    }


    return solve(line.trim().split(" ").toMutableList()).toBigInteger()
}

fun stringOperator(str: String): (BigInteger, BigInteger) -> BigInteger {
    return when(str) {
        "*" -> {a,b -> a*b}
        "/" -> {a,b -> a/b}
        "+" -> {a,b -> a+b}
        "-" -> {a,b -> a-b}
        else -> throw Exception("Not a supported operator")
    }
}


fun solve (list: MutableList<String>): String {
    var list2 = list

    var fem = ""
    try {
        repeat(list.size) {
            for (y in list.indices) {
                if (list[y] == "*") {
                    fem = stringOperator(list[y])(list[y - 1].toBigInteger(), list[y + 1].toBigInteger()).toString()
                    list2.add(y, fem)
                    list2.removeAt(y - 1)
                    list2.removeAt(y + 1)
                    list2.removeAt(y)
                    break
                }
            }
        }
        repeat(list.size) {
            for (y in list.indices) {
                if (list[y] == "/") {
                    fem = stringOperator(list[y])(list[y - 1].toBigInteger(), list[y + 1].toBigInteger()).toString()
                    list2.add(y, fem)
                    list2.removeAt(y - 1)
                    list2.removeAt(y + 1)
                    list2.removeAt(y)

                    break
                }
            }
        }
        repeat(list.size) {
            for (y in list.indices) {
                if (list[y] == "+" || list[y] == "-") {
                    fem = stringOperator(list[y])(list[y - 1].toBigInteger(), list[y + 1].toBigInteger()).toString()
                    list2.add(y, fem)
                    list2.removeAt(y - 1)
                    list2.removeAt(y + 1)
                    list2.removeAt(y)

                    break
                }
            }
        }

        if (list2.size > 1) {
            solve(list2)
        } else {
            return list2[0]
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return list2[0]
}

fun solveSign(inp: String): String {
    if (inp.matches(Regex("[*/]{2,}"))) throw Exception("Invalid expression")
    return sign(inp).toString()
}

class UnknownVariableException(): Exception() {}

