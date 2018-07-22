package com.dpuntu.edittextsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.dpuntu.editui.CustomItemInputView
import kotlinx.android.synthetic.main.activity_input.*

/**
 * Created  by fangmingxing on 2018/7/4.
 */
class InputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        centerPinEdit2.onInputComplete = { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        changeBtn.setOnClickListener {
            textview.visibility = View.GONE
            centerPwdEdit3.setUpInputView(CustomItemInputView.PWD_KEY, CustomItemInputView.SQUARE_INPUT, 6)
        }
    }
}
