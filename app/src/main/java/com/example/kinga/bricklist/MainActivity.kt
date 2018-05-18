package com.example.kinga.bricklist

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log

class MainActivity : AppCompatActivity() {

    private var url = "http://fdcs.put.poznan.pl/MyWeb/BL"
    private val TAG = "StateChange"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Log.i(TAG, "onCreate")

        newProjectButton.setOnClickListener {
            val i = Intent(this, NewProjectActivity::class.java)
            i.putExtra("url", url)
            startActivityForResult(i, 999)
        }

        settingsButton.setOnClickListener {
            val i = Intent(this, SettingsActivity::class.java)
            i.putExtra("url", url)
            startActivityForResult(i, 999)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == 999 && resultCode == Activity.RESULT_OK && data != null){
            url = data.extras.getString("url")
        }
    }


}
