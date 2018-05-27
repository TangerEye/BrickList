package com.example.kinga.bricklist.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.example.kinga.bricklist.Database
import com.example.kinga.bricklist.ListViewAdapters.InventoryPartsListViewAdapter
import com.example.kinga.bricklist.R
import kotlinx.android.synthetic.main.activity_inventory.*
import kotlinx.android.synthetic.main.activity_inventory_listview.view.*
import java.io.IOException
import java.sql.SQLException

class InventoryActivity : AppCompatActivity() {

    private var inventoryId = 0
    private var inventoryName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        this.inventoryId = intent.extras.getInt("inventoryId")
        this.inventoryName = intent.extras.getString("inventoryName")
        inventoryNr.text = "Inventory: \"" + inventoryName + "\""

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

        val inventoryPartsListView: ListView = findViewById(R.id.inventoryPartsListView)
        var inventoryPartsList = database.getCurrentInventoryParts(inventoryId)
        inventoryPartsList = database.getItemsDesignIds(inventoryPartsList)

        for (i: Int in 0 until inventoryPartsList.size) {
            inventoryPartsList[i] = database.getItemImage(inventoryPartsList[i])
        }

        val adapter = InventoryPartsListViewAdapter(this, inventoryPartsList)
        inventoryPartsListView.adapter = adapter

        saveButton.setOnClickListener {
            Log.i("StateChange", "all: " + inventoryPartsList.size)

            for (i in 0 until inventoryPartsList.size - 3) {
                Log.i("StateChange", "i: $i")
                val v = inventoryPartsListView.getChildAt(i)
                val numberPicker = v.QuantityInStore
                inventoryPartsList[i].quantityInStore = numberPicker.value
            }

            database.updateQuantityInStore(this.inventoryId.toString(), inventoryPartsList)
            super.finish()
        }
    }
}
