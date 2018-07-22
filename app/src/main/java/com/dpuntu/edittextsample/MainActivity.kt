package com.dpuntu.edittextsample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dpuntu.editui.UtilResource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UtilResource.init(this.resources)

        inputEditBtn.setOnClickListener { startActivity(Intent(this, InputActivity::class.java)) }

        formatEditBtn.setOnClickListener { startActivity(Intent(this, FormatActivity::class.java)) }
    }
}
