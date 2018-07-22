package com.dpuntu.edittextsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_format.*

/**
 * Created  by fangmingxing on 2018/7/13.
 */
class FormatActivity : AppCompatActivity() {

    private val TAG = "FormatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_format)
        phoneMoneyEdit.setText("123.78")
        showContent.setOnClickListener {
            Log.d(TAG, phoneEdit.unFormatterString())
        }
    }
}
