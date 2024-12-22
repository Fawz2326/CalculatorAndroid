package com.example.calcul

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calcul.R


class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView
    private var expression: StringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)

        val buttons = listOf(
            R.id.buttonAC to "AC",
            R.id.buttonSign to "±",
            R.id.buttonPercent to "%",
            R.id.buttonDivide to "÷",
            R.id.button7 to "7",
            R.id.button8 to "8",
            R.id.button9 to "9",
            R.id.buttonMultiply to "×",
            R.id.button4 to "4",
            R.id.button5 to "5",
            R.id.button6 to "6",
            R.id.buttonMinus to "-",
            R.id.button1 to "1",
            R.id.button2 to "2",
            R.id.button3 to "3",
            R.id.buttonPlus to "+",
            R.id.button0 to "0",
            R.id.buttonDot to ".",
            R.id.buttonEquals to "=",
            // Добавьте все остальные кнопки
        )

        for ((id, value) in buttons) {
            findViewById<Button>(id).setOnClickListener { onButtonClick(value) }
        }
    }

    private fun onButtonClick(value: String) {
        when (value) {
            "AC" -> {
                expression.clear()
                tvExpression.text = ""
                tvResult.text = ""
            }
            "±" -> {
                if (expression.isNotEmpty() && expression[0] == '-') {
                    expression.deleteCharAt(0)
                } else {
                    expression.insert(0, "-")
                }
                tvExpression.text = expression.toString()
            }
            "%" -> {
                if (expression.isNotEmpty()) {
                    try {
                        val value = expression.toString().toDouble() / 100
                        expression.clear().append(value)
                        tvExpression.text = expression.toString()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Invalid format", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            "=" -> {
                calculateResult()
            }
            else -> {
                expression.append(value)
                tvExpression.text = expression.toString()
            }
        }
    }

    private fun calculateResult() {
        try {
            val result = evaluateExpression(expression.toString())
            tvResult.text = result.toString()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка вычисления", Toast.LENGTH_SHORT).show()
        }
    }

    private fun evaluateExpression(expr: String): Double {
        val sanitizedExpr = expr.replace("÷", "/").replace("×", "*")
        val postfix = convertToPostfix(sanitizedExpr)
        return evaluatePostfix(postfix)
    }

    private fun convertToPostfix(expression: String): List<String> {
        val stack = mutableListOf<Char>()
        val output = mutableListOf<String>()
        var numberBuffer = StringBuilder()

        for (char in expression) {
            when {
                char.isDigit() || char == '.' -> {
                    numberBuffer.append(char)
                }
                char in listOf('+', '-', '*', '/') -> {
                    if (numberBuffer.isNotEmpty()) {
                        output.add(numberBuffer.toString())
                        numberBuffer.clear()
                    }
                    while (stack.isNotEmpty() && precedence(stack.last()) >= precedence(char)) {
                        output.add(stack.removeAt(stack.size - 1).toString())
                    }
                    stack.add(char)
                }
                else -> throw IllegalArgumentException("Invalid character: $char")
            }
        }

        if (numberBuffer.isNotEmpty()) {
            output.add(numberBuffer.toString())
        }

        while (stack.isNotEmpty()) {
            output.add(stack.removeAt(stack.size - 1).toString())
        }

        return output
    }

    private fun evaluatePostfix(postfix: List<String>): Double {
        val stack = mutableListOf<Double>()

        for (token in postfix) {
            when {
                token.toDoubleOrNull() != null -> {
                    stack.add(token.toDouble())
                }
                token in listOf("+", "-", "*", "/") -> {
                    if (stack.size < 2) throw IllegalArgumentException("Invalid postfix expression")
                    val b = stack.removeAt(stack.size - 1)
                    val a = stack.removeAt(stack.size - 1)
                    val result = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> a / b
                        else -> throw IllegalArgumentException("Unknown operator: $token")
                    }
                    stack.add(result)
                }
                else -> throw IllegalArgumentException("Invalid token: $token")
            }
        }

        if (stack.size != 1) throw IllegalArgumentException("Invalid postfix expression")
        return stack[0]
    }

    private fun precedence(operator: Char): Int {
        return when (operator) {
            '+', '-' -> 1
            '*', '/' -> 2
            else -> throw IllegalArgumentException("Unknown operator: $operator")
        }
    }
}
