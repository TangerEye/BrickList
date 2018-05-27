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
        inventoryNr.text = "Inventory \"$inventoryName\""

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

        backButton.setOnClickListener {
            Log.i("StateChange", "all: " + inventoryPartsListView.count)

            for (i in 0 until inventoryPartsListView.count - 3) {
                var v = inventoryPartsListView.getChildAt(i)
                var numberPicker = v.QuantityInStore
                inventoryPartsList[i].quantityInStore = numberPicker.value
            }

            inventoryPartsList.forEachIndexed {index, element ->
                Log.i("StateChange", element.itemId.toString() + " "+ element.quantityInStore)
            }
            database.updateQuantityInStore(this.inventoryId.toString(), inventoryPartsList)
            super.finish()
        }
    }
}
