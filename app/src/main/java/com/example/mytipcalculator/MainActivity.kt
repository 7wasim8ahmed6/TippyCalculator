package com.example.mytipcalculator

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import buisnessLogic

//private const val TAG = "MainActivity"
private const val STARTAMT = 100.00
private const val STARTPERCENT = 15.00

class MainActivity : AppCompatActivity() {
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mEtAmount: EditText
    private lateinit var mSeekBar: SeekBar
    private lateinit var mPercentView: TextView
    private lateinit var mTipView: TextView
    private lateinit var mTotalView: TextView
    private lateinit var mHappynessIndView: TextView
    private lateinit var mCbxSplitBill: CheckBox
    private lateinit var mEtnSplittNos: EditText
    private lateinit var mPerPersonAmt: TextView
    private lateinit var mRoundUp: TextView
    private lateinit var mRoundDown: TextView
    private lateinit var mTipSpinner: Spinner
    private lateinit var mAutoCompleteTextView: AutoCompleteTextView
    private lateinit var mTVSymbol: TextView
    private lateinit var mCurrencySymbols: Array<String>
    private lateinit var mbuisnessLogic: buisnessLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        connectAllWidgets()
        addContentsToSpinner()
        setUpAutoCompleteAndSharedPreferences()
        mEtAmount.setText("$STARTAMT")
        mPerPersonAmt.text = mTotalView.text.toString()
        mPercentView.text = "STARTPERCENT%"
        mSeekBar.progress = convertTipPercentToSeeker(STARTPERCENT)
        computeTipTotalAndSetViews()
        setTheHappinessInd(STARTPERCENT)
        updateAppriciationColor(STARTPERCENT)
        connectSlots()
    }

    private fun setUpAutoCompleteAndSharedPreferences() {
        val currencies = resources.getStringArray(R.array.currency_array)
        mCurrencySymbols = resources.getStringArray(R.array.currency_symbols)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.select_dialog_item, currencies)
        mAutoCompleteTextView.threshold = 1
        mAutoCompleteTextView.setAdapter(adapter)
        mAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            // Retrieve the clicked item and its position
            val clickedItem = parent.getItemAtPosition(position).toString()
            val actualPosition = currencies.indexOf(clickedItem)
            // Use this position to get the corresponding symbol from the currency symbols array
            val symbol = if (actualPosition != -1) mCurrencySymbols[actualPosition] else "Unknown"
            val oldSymbol = mTVSymbol.text.toString()
            changeTextSymbols(mTipView, oldSymbol, symbol)
            changeTextSymbols(mTotalView, oldSymbol, symbol)
            changeTextSymbols(mPerPersonAmt, oldSymbol, symbol)

            // Set the symbol to the TextView
            mTVSymbol.text = symbol

            with(mSharedPreferences.edit()) {
                putString("selected_currency", clickedItem)
                apply()
            }


            mAutoCompleteTextView.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mAutoCompleteTextView.windowToken, 0)
        }
        // Set initial selection from SharedPreferences
        mSharedPreferences = getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
        val selectedCurrency = mSharedPreferences.getString("selected_currency", "EUR")
        if (selectedCurrency != null && currencies.contains(selectedCurrency)) {
            val actualPosition = currencies.indexOf(selectedCurrency)
            mAutoCompleteTextView.setText(selectedCurrency, false)
            val symbol = if (actualPosition != -1) mCurrencySymbols[actualPosition] else "X"
            mTVSymbol.text = symbol
        }
    }

    private fun connectSlots() {
        mEtAmount.addTextChangedListener(/* watcher = */ object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                computeTipTotalAndSetViews()
            }
        })

        mSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                Log.i(/* tag = */ TAG, /* msg = */ "onProgressChanged $progress")
                if(fromUser) {
                    val decimalProgress = convertSeekerToTipPercent(progress)
                    mbuisnessLogic.setTipPercent(decimalProgress)
                    computeTipTotalAndSetViews()
                    setTheHappinessInd(decimalProgress)
                    updateAppriciationColor(decimalProgress)
                    if (mCbxSplitBill.isChecked)
                        updateSplitBill()
                }
                else
                {
                    val decimalProgress = convertSeekerToTipPercent(progress)
                    computeTipTotalAndSetViews()
                    setTheHappinessInd(decimalProgress)
                    updateAppriciationColor(decimalProgress)
                    if (mCbxSplitBill.isChecked)
                        updateSplitBill()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mCbxSplitBill.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mEtnSplittNos.isEnabled = true
                updateSplitBill()
            } else {
                mEtnSplittNos.isEnabled = false
                mPerPersonAmt.text = ""
            }
        }

        mEtnSplittNos.addTextChangedListener(object : TextWatcher {
            /**
             * This method is called to notify you that, within `s`,
             * the `count` characters beginning at `start`
             * are about to be replaced by new text with length `after`.
             * It is an error to attempt to make changes to `s` from
             * this callback.
             */
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            /**
             * This method is called to notify you that, within `s`,
             * the `count` characters beginning at `start`
             * have just replaced old text that had length `before`.
             * It is an error to attempt to make changes to `s` from
             * this callback.
             */
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSplitBill()
            }

            /**
             * This method is called to notify you that, somewhere within
             * `s`, the text has been changed.
             * It is legitimate to make further changes to `s` from
             * this callback, but be careful not to get yourself into an infinite
             * loop, because any changes you make will cause this method to be
             * called again recursively.
             * (You are not told where the change took place because other
             * afterTextChanged() methods may already have made other changes
             * and invalidated the offsets.  But if you need to know here,
             * you can use [Spannable.setSpan] in [.onTextChanged]
             * to mark your place and then look up from here where the span
             * ended up.
             */
            override fun afterTextChanged(s: Editable?) {
            }
        })

        mRoundUp.setOnClickListener {
            val tipAmountText: String = mTipView.text.toString()
            if (tipAmountText.isNotEmpty()) {
                // Convert the tip amount text to a Double
//                val numericStr = tipAmountText.replace(mTVSymbol.text.toString(), "")
                val tipAmount = mbuisnessLogic.getTipAmount()

                // Round up the tip amount
                val roundedTipAmount = kotlin.math.ceil(tipAmount)
                mbuisnessLogic.setTipPercentViaAmountTip(roundedTipAmount)
                if (mEtAmount.text.isNotEmpty()) {
                    val lUnits = convertTipPercentToSeeker(mbuisnessLogic.getTipPercent())
                    mSeekBar.setProgress(lUnits)
                }
            } else {
                // Handle the case where the tip amount text is empty
                Toast.makeText(applicationContext, "Tip amount is empty", Toast.LENGTH_SHORT).show()
            }
        }

        mRoundDown.setOnClickListener {
            val tipAmountText: String = mTipView.text.toString()
            if (tipAmountText.isNotEmpty()) {
                // Convert the tip amount text to a Double
//                val numericStr = tipAmountText.replace(mTVSymbol.text.toString(), "")
                val tipAmount = mbuisnessLogic.getTipAmount()
                // Round up the tip amount
                val roundedTipAmount = kotlin.math.floor(tipAmount)
                mbuisnessLogic.setTipPercentViaAmountTip(roundedTipAmount)
                if (mEtAmount.text.isNotEmpty()) {
                    val lUnits = convertTipPercentToSeeker(mbuisnessLogic.getTipPercent())
                    mSeekBar.setProgress(lUnits)
                }
            } else {
                // Handle the case where the tip amount text is empty
                Toast.makeText(applicationContext, "Tip amount is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun connectAllWidgets() {
        mbuisnessLogic = buisnessLogic(STARTAMT, STARTPERCENT)
        mEtAmount = findViewById(R.id.etAmount)
        mPercentView = findViewById(R.id.tvPercent)
        mTipView = findViewById(R.id.tvTipValue)
        mTotalView = findViewById(R.id.tvTotalAmt)
        mHappynessIndView = findViewById(R.id.tvHappyIndicator)
        mSeekBar = findViewById(R.id.sbTipAdjustBar)
        mCbxSplitBill = findViewById(R.id.cbxSplitBill)
        mEtnSplittNos = findViewById(R.id.etnSplitNos)
        mPerPersonAmt = findViewById(R.id.tvAmtPerPerson)
        mRoundUp = findViewById(R.id.tvUp)
        mRoundDown = findViewById(R.id.tvDn)
        mTipSpinner = findViewById(R.id.spnTip)
        mAutoCompleteTextView = findViewById(R.id.autocomplete_currency)
        mTVSymbol = findViewById(R.id.textview_currency_symbol)
    }

    private fun changeTextSymbols(textView: TextView, oldSymbol: String, newSymbol: String) {
        var text = textView.text.toString()
        text = text.replace(oldSymbol, newSymbol)
        textView.text = text
    }

    private fun addContentsToSpinner() {
        val serviceQualityOptions = listOf("Poor", "Acceptable", "Good", "Great", "Amazing")
        val tipPercentages = mapOf(
            "Poor" to 500,
            "Acceptable" to 1250,
            "Good" to 1750,
            "Great" to 2250,
            "Amazing" to 2750
        )
        // Set up the Spinner for service quality
        val serviceAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, serviceQualityOptions)
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mTipSpinner.adapter = serviceAdapter
        mTipSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedServiceQuality = serviceQualityOptions[position]
                val seekBarVal = tipPercentages[selectedServiceQuality]
                if (seekBarVal != null) {
                    mbuisnessLogic.setTipPercent(convertSeekerToTipPercent(seekBarVal))
                    mSeekBar.progress = seekBarVal
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        mTipSpinner.setSelection(serviceQualityOptions.indexOf("Good")) // Set spinner to "Good"
    }

    private fun updateSplitBill() {
        if (mEtAmount.text.isEmpty()) {
            mPerPersonAmt.text = ""
        } else {
            val lTotalValStr = mTotalView.text.toString()
            if (mEtnSplittNos.text.isEmpty()) {
                mPerPersonAmt.text = lTotalValStr
            } else {
                try {
                    val toInt: Int = mEtnSplittNos.text.toString().toInt()
                    if (toInt == 0) {
                        // Handle divide by zero error
                        mPerPersonAmt.text = getString(/* resId = */ R.string.wash_the_dishes)
                    } else {
                        mbuisnessLogic.setNumberOfPeople(toInt.toUInt())
                        mPerPersonAmt.text = String.format("%.2f", mbuisnessLogic.getTotalWithTipPerPerson()) + mTVSymbol.text.toString()
                    }
                } catch (e: NumberFormatException) {
                    // Handle parsing error
                    mPerPersonAmt.text = getString(R.string.invalid_input)
                }
            }
        }
    }

    private fun updateAppriciationColor(decimalProgress: Double) {
        val progress = convertTipPercentToSeeker(decimalProgress)
        val lFraction = progress.toFloat() / 3000
        // Retrieve start color
        val startColor = ContextCompat.getColor(this, R.color.red)
        // Retrieve end color
        val endColor = ContextCompat.getColor(this, R.color.green)
        val interpolatedColor = ColorUtils.blendARGB(startColor, endColor, lFraction)
        mHappynessIndView.setTextColor(interpolatedColor)
    }

    private fun setTheHappinessInd(decimalProgress: Double) {
        val progress = convertTipPercentToSeeker(decimalProgress)
        val lHappyTxt = when (progress) {
            in 0..999 -> getString(R.string.poor_face) // Poor (sad face)
            in 1000..1499 -> getString(R.string.acceptable_face) // Acceptable (neutral face)
            in 1500..1999 -> getString(R.string.good_face) // Good (slightly smiling face)
            in 2000..2499 -> getString(R.string.great_face) // Great (grinning face)
            else -> getString(R.string.amazing_face) // Amazing (tears of joy)
        }
        mHappynessIndView.text = lHappyTxt
    }

    private fun computeTipTotalAndSetViews() {
        if (mEtAmount.text.isEmpty()) {
            mTipView.text = ""
            mTotalView.text = ""
            return
        }
        mPercentView.text = String.format("%.2f%%", mbuisnessLogic.getTipPercent())
        val amountText: String = mEtAmount.text.toString()
        mbuisnessLogic.setAmount(if (amountText.isNotEmpty()) amountText.toDouble() else 0.0)
        mTipView.text = String.format("%.2f", mbuisnessLogic.getTipAmount()) + mTVSymbol.text.toString()
        mTotalView.text = String.format("%.2f", mbuisnessLogic.getTotalWithTip()) + mTVSymbol.text.toString()
    }

    private fun convertTipPercentToSeeker(aTipPercent: Double): Int {
        return Math.round((3000.0 * aTipPercent) / 30.0).toInt()
    }

    private fun convertSeekerToTipPercent(aSeekerValue: Int): Double {
        return (aSeekerValue.toDouble() * 30.0)/3000.0
    }
}