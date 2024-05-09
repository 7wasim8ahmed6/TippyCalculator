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
        mSeekBar = findViewById(R.id.sbTipAdjustBar)

        mEtAmount.setText("$STARTAMT")
        mPercentView.text = "STARTPERCENT%"
        mSeekBar.progress = STARTPERCENT
        computeTipTotalAndSetViews()

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
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
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
        mTipView.text = String.format("%.2f", value) + "$"
        val Total = value + amount
        mTotalView.text = String.format("%.2f", Total) + "$"
    }
}