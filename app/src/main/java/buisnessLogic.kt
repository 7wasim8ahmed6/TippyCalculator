import kotlin.math.ceil

class buisnessLogic(private var totalAmount: Double, private var tipPercentage: Double) {
    private var splitNumbers: UInt = 1u

    // Function to calculate the total amount with tip, rounded up to 2 decimal places
    fun totalWithTip(): Double {
        val total = totalAmount + (totalAmount * (tipPercentage / 100))
        return ceil(total * 100) / 100
    }

    // Function to calculate the amount per person, rounded up to 2 decimal places
    fun amountPerPerson(): Double {
        return if (splitNumbers == 0u) {
            totalWithTip()
        } else {
            val amount = totalWithTip() / splitNumbers.toDouble()
            ceil(amount * 100) / 100
        }
    }
}