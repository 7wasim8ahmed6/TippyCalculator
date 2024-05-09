package com.example.mytipcalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        mEtAmount.setText("$STARTAMT")
        mPercentView.text = "STARTPERCENT%"
        mSeekBar.progress = STARTPERCENT
        computeTipTotalAndSetViews()
        setTheHappinessInd(STARTPERCENT)

        mEtAmount.addTextChangedListener(/* watcher = */ object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                computeTipTotalAndSetViews()
            }
        })

        mSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
//                Log.i(/* tag = */ TAG, /* msg = */ "onProgressChanged $progress")
                computeTipTotalAndSetViews()
                setTheHappinessInd(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setTheHappinessInd(progress: Int) {
        val lHappyTxt = when(progress)
        {
            in 0..9 ->"Poor"
            in 10..14 ->"Acceptable"
            in 15..19 ->"Good"
            in 20..24 ->"Great"
            else -> "Amazing"
        }
        mHappynessIndView.text = lHappyTxt
        /*
        Todo
        *   Add best and worst colors to themes file and interpolate between them
        * to give a color to text
        */
    }

    private fun computeTipTotalAndSetViews() {
        if(mEtAmount.text.isEmpty())
        {
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
        val Total = value + amount
        mTotalView.text = String.format("%.2f", Total) + "€"
    }
}