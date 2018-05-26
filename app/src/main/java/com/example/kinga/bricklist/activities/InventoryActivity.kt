package com.example.kinga.bricklist.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.example.kinga.bricklist.Database
import com.example.kinga.bricklist.R
import kotlinx.android.synthetic.main.activity_inventory.*
import java.io.IOException
import java.sql.SQLException

class InventoryActivity : AppCompatActivity() {

    private var inventoryId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        this.inventoryId = intent.extras.getInt("inventoryId")
        inventoryNr.text = "Inventory nr $inventoryId"

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

        var inventoryPartsListView: ListView = findViewById(R.id.inventoryPartsListView)
        var inventoryPartsList = database.getCurrentInventoryParts(inventoryId)
        var adapter = InventoryPartsListViewAdapter(this, inventoryPartsList)
        inventoryPartsListView.adapter = adapter

        saveButton.setOnClickListener {
            super.finish()
        }
    }
}
