package com.example.kinga.bricklist.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kinga.bricklist.Database
import kotlinx.android.synthetic.main.activity_main.*
import com.example.kinga.bricklist.R
import java.io.IOException
import java.sql.SQLException
import android.widget.ListView
import com.example.kinga.bricklist.ListViewAdapters.InventoriesListViewAdapter


class MainActivity : AppCompatActivity() {

    private var url = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val database = Database(this)
        try {
            database.createDataBase()
        } catch (e: IOException) {
            throw Error("Error creating database")
        }
        try {
            database.openDataBase()
        } catch (e: SQLException) {
            throw e
        }

        var inventoriesListView: ListView = findViewById(R.id.inventoriesListView)
        var inventoriesList = database.getInventories()
        var adapter = InventoriesListViewAdapter(this, inventoriesList)
        inventoriesListView.adapter = adapter

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

        inventoriesListView.setOnItemClickListener { _, _, position, _ ->
            val selectedInventory = inventoriesList[position]
            val i = Intent(this, InventoryActivity::class.java)
            i.putExtra("inventoryId", selectedInventory.id)
            startActivity(i)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == 999 && resultCode == Activity.RESULT_OK && data != null){
            url = data.extras.getString("url")
        }
    }


}
