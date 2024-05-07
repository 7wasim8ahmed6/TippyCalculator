package com.example.mytipcalculator

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val TAG = "MainActivity"
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
        mSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                Log.i(/* tag = */ TAG, /* msg = */ "onProgressChanged $progress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}