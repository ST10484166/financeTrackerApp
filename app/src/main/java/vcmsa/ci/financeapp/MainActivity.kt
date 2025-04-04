package vcmsa.ci.financeapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var incomeInput: EditText
    private lateinit var expenseNames: List<EditText>
    private lateinit var expenseAmounts: List<EditText>
    private lateinit var calculateButton: Button
    private lateinit var outputIncome: TextView
    private lateinit var outputExpenses: TextView
    private lateinit var outputBalance: TextView
    private lateinit var feedbackMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        incomeInput = findViewById(R.id.incomeInput)

        expenseNames = listOf(
            findViewById(R.id.expense1),
            findViewById(R.id.expense2),
            findViewById(R.id.expense3),
            findViewById(R.id.expense4)
        )

        expenseAmounts = listOf(
            findViewById(R.id.expenseAmount1),
            findViewById(R.id.expenseAmount2),
            findViewById(R.id.expenseAmount3),
            findViewById(R.id.expenseAmount4)
        )

        calculateButton = findViewById(R.id.calculateButton)
        outputIncome = findViewById(R.id.outputIncome)
        outputExpenses = findViewById(R.id.outputExpenses)
        outputBalance = findViewById(R.id.outputBalance)
        feedbackMessage = findViewById(R.id.feedbackMessage)

        calculateButton.setOnClickListener { calculateBalance() }
    }

    private fun calculateBalance() {
        val incomeText = incomeInput.text.toString()

       //notif for invalid income
        if (incomeText.isEmpty()) {
            showToast("Please enter your income")
            return
        }

        //notif for invalid income
        val income = incomeText.toDoubleOrNull()
        if (income == null) {
            showToast("Income must be a number")
            return
        }

        var totalExpenses = 0.0
        val categoryOutput = StringBuilder()//creates new strings so I don't have to

        for (i in expenseAmounts.indices) {

            val name = expenseNames[i].text.toString().ifEmpty { "Category ${i + 1}" }//no valid name for any category
            val amountText = expenseAmounts[i].text.toString()
            val expenseAmount = amountText.toDoubleOrNull()

            if (expenseAmount == null) {
                showToast("Invalid amount in $name")//no valid number for any category. Private fun below
                return
            }

            totalExpenses = totalExpenses + expenseAmount

            val percent = (expenseAmount / income) * 100//how much income one expense uses
            categoryOutput.append("$name: ")

            if (percent >= 30){
                categoryOutput.append("Too high (${String.format("%.1f", percent)}%)\n")//percentage is shown with one decimal
            }else if (percent <= 5){
                categoryOutput.append("Very low (${String.format("%.1f", percent)}%)\n")
            }else{
                categoryOutput.append("Reasonable (${String.format("%.1f", percent)}%)\n")
            }
        }

        val balance = income - totalExpenses
        val totalPercentage = (totalExpenses / income) * 100

        outputIncome.text = "Income: R${"%.2f".format(income)}"//percentage in 2 decimals
        outputExpenses.text = "Total Expenses: R${"%.2f".format(totalExpenses)}"
        outputBalance.text = "Balance: R${"%.2f".format(balance)}"

        val nonZeroBalance = 0.1
        if (balance > 0){
            feedbackMessage.text = "You are saving money!\n\n$categoryOutput"
            feedbackMessage.setTextColor(Color.GREEN)

        }else if (kotlin.math.abs(balance) < nonZeroBalance)// if balance = 0, .math.abs() to count negative numbers
        {
            feedbackMessage.text = "You are breaking even.\n\n$categoryOutput"
            feedbackMessage.setTextColor(Color.DKGRAY)
        }else{
            feedbackMessage.text = "You are overspending! File for bankrupsy.\n\n$categoryOutput"
            feedbackMessage.setTextColor(Color.RED)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}