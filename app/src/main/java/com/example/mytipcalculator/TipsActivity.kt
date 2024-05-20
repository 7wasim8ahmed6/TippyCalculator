package com.example.mytipcalculator

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TipsActivity : AppCompatActivity() {
    private lateinit var mListView: ListView
    private val mDataList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tips)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mListView = findViewById(R.id.listView)
//        val amount =  intent.getDoubleExtra(getString(R.string.amountfortipscreen), -1.0)
//        val tipPercent =intent.getDoubleExtra(getString(R.string.tippercentfortipscreen), -1.0)
//        if (amount != -1.0 && tipPercent != -1.0) {
//            mDataList.add("Amount: $amount, Tip Percent: $tipPercent")
//        }
//        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mDataList)
//        mListView.adapter = adapter
        // Retrieve saved data from SharedPreferences
        val sharedPreferences = getSharedPreferences("TipsData", Context.MODE_PRIVATE)
        val savedData = sharedPreferences.getStringSet("dataList", emptySet()) ?: emptySet()

        // Add saved data to mDataList
        mDataList.addAll(savedData)

        // Update ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mDataList)
        mListView.adapter = adapter

        // Get new data from intent and add it to mDataList
        val amount = intent.getDoubleExtra(getString(R.string.amountfortipscreen), -1.0)
        val tipPercent = intent.getDoubleExtra(getString(R.string.tippercentfortipscreen), -1.0)
        if (amount != -1.0 && tipPercent != -1.0) {
            mDataList.add("Amount: $amount, Tip Percent: $tipPercent")
            // Update SharedPreferences with the new data
            val editor = sharedPreferences.edit()
            editor.putStringSet("dataList", mDataList.toSet())
            editor.apply()
        }
    }
}