package com.example.kinga.bricklist.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kinga.bricklist.R
import com.example.kinga.bricklist.XML.ImportXML
import kotlinx.android.synthetic.main.activity_new_project.*

class NewProjectActivity : AppCompatActivity() {

    private var url = ""
    private var projectNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_project)
        url = intent.getStringExtra("url")

        addProjectButton.setOnClickListener {
            var success = true
            projectNumber = projectNumberValue.text.toString()
            val importXML = ImportXML(url, projectNumber)
            importXML.downloadXml()
            if (success) {
                val toast = Toast.makeText(baseContext, "Project " + projectNumberValue.text.toString() + "  has been successfully added.", Toast.LENGTH_LONG)
                toast.show()
                projectNumberValue.setText("")
            }
            else {
                val toast = Toast.makeText(baseContext, "An error occured while adding the project.", Toast.LENGTH_LONG)
                toast.show()
            }

        }
    }


}
