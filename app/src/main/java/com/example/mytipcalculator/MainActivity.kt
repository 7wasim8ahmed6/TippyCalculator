package com.example.mytipcalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

//private const val TAG = "MainActivity"
private const val STARTAMT = 100
private const val STARTPERCENT = 15

class MainActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

        mEtAmount.setText("$STARTAMT")
        mPerPersonAmt.text = mTotalView.text.toString()
        mPercentView.text = "STARTPERCENT%"
        mSeekBar.progress = STARTPERCENT
        computeTipTotalAndSetViews()
        setTheHappinessInd(STARTPERCENT)
        updateAppriciationColor(STARTPERCENT)

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
                computeTipTotalAndSetViews()
                setTheHappinessInd(progress)
                updateAppriciationColor(progress)
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
            if(tipAmountText.isNotEmpty())
            {
                // Convert the tip amount text to a Double
                val numericStr = tipAmountText.replace("€", "")
                val tipAmount = numericStr.toDouble()

                // Round up the tip amount
                val roundedTipAmount = kotlin.math.ceil(tipAmount)
                if(!mEtAmount.text.isEmpty())
                {
                    val lBillAmt = mEtAmount.text.toString()
                    val percentage = roundedTipAmount * 100 / lBillAmt.toDouble()
//                    mSeekBar.setProgress(percentage.toInt())
                }
            }
            else
            {
                // Handle the case where the tip amount text is empty
                Toast.makeText(applicationContext, "Tip amount is empty", Toast.LENGTH_SHORT).show()
            }
        }

        mRoundDown.setOnClickListener {
            val tipAmountText: String = mTipView.text.toString()
            if(tipAmountText.isNotEmpty())
            {
                // Convert the tip amount text to a Double
                val numericStr = tipAmountText.replace("€", "")
                val tipAmount = numericStr.toDouble()
                // Round up the tip amount
                val roundedTipAmount = kotlin.math.floor(tipAmount)
            }
            else
            {
                // Handle the case where the tip amount text is empty
                Toast.makeText(applicationContext, "Tip amount is empty", Toast.LENGTH_SHORT).show()
            }
        }
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
                    val numericString = lTotalValStr.replace("€", "") // Remove the euro symbol
                    val toDouble: Double = numericString.toDouble()
                    val d: Double = toDouble / toInt
                    mPerPersonAmt.text = String.format("%.2f", d) + "€"
                }
            } catch (e: NumberFormatException) {
                // Handle parsing error
                mPerPersonAmt.text = getString(R.string.invalid_input)
            }
        }
    }
}

    private fun updateAppriciationColor(progress: Int) {
        val lFraction = progress.toFloat() / 30
//        Log.i(TAG, "Current progress $i")
        // Retrieve start color
        val startColor = ContextCompat.getColor(this, R.color.red)
        // Retrieve end color
        val endColor = ContextCompat.getColor(this, R.color.green)
        val interpolatedColor = ColorUtils.blendARGB(startColor, endColor, lFraction)
        mHappynessIndView.setTextColor(interpolatedColor)
    }

    private fun setTheHappinessInd(progress: Int) {
        val lHappyTxt = when (progress) {
            in 0..9 -> getString(R.string.poor_face) // Poor (sad face)
            in 10..14 -> getString(R.string.acceptable_face) // Acceptable (neutral face)
            in 15..19 -> getString(R.string.good_face) // Good (slightly smiling face)
            in 20..24 -> getString(R.string.great_face) // Great (grinning face)
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
        val progress = mSeekBar.progress
        mPercentView.text = "$progress%"
        val amountText: String = mEtAmount.text.toString()
        val amount = if (amountText.isNotEmpty()) amountText.toDouble() else 0.0
        val value = amount * progress / 100
        mTipView.text = String.format("%.2f", value) + "€"
        val lTotal = value + amount
        mTotalView.text = String.format("%.2f", lTotal) + "€"
    }
}