package com.example.kinga.bricklist

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity()  {

    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        url = intent.getStringExtra("url")
        urlValue.setText(url)

        saveButton.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("url", urlValue.text.toString())
            setResult(Activity.RESULT_OK, i)
            super.finish()
        }

    }
}
