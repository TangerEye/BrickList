package com.example.kinga.bricklist.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kinga.bricklist.Database
import com.example.kinga.bricklist.R
import com.example.kinga.bricklist.models.Item
import kotlinx.android.synthetic.main.activity_inventory.*
import java.io.IOException
import java.sql.SQLException

class InventoryActivity : AppCompatActivity() {

    private var inventoryId = 0
    private var inventoryName = ""
    var inventoryPartsList = null

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
            database.updateQuantityInStore(this.inventoryId.toString(), inventoryPartsList)
            super.finish()
        }
    }

    inner class InventoryPartsListViewAdapter(context: Context, private val inventoryPartsList: ArrayList<Item>):
            BaseAdapter() {

        private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return inventoryPartsList.size
        }

        override fun getItem(position: Int): Any {
            return inventoryPartsList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(R.layout.activity_inventory_listview, parent, false)

            val idTextView = rowView.findViewById(R.id.ItemId) as TextView
            val colorTextView = rowView.findViewById(R.id.ColorId) as TextView
            val quantityInSetTextView = rowView.findViewById(R.id.QuantityInSet) as TextView
            val quantityInStoreNumberPicker = rowView.findViewById(R.id.QuantityInStore) as NumberPicker
            val itemImage = rowView.findViewById(R.id.ItemImage) as ImageView

            val item = getItem(position) as Item

            quantityInStoreNumberPicker.minValue = 0
            quantityInStoreNumberPicker.maxValue = 100

            idTextView.text = item.itemId.toString()
            colorTextView.text = item.color.toString()
            quantityInSetTextView.text = item.quantityInSet.toString()
            quantityInStoreNumberPicker.value = item.quantityInStore!!
            itemImage.setImageBitmap(item.image)

            quantityInStoreNumberPicker.setOnValueChangedListener{_, _, newVal ->
                inventoryPartsList[position].quantityInStore = newVal
                quantityInStoreNumberPicker.tag = newVal
                if (item.quantityInSet!! <= newVal)
                    rowView.setBackgroundColor(Color.LTGRAY)
                else
                    rowView.setBackgroundColor(Color.TRANSPARENT)
            }
            if (item.quantityInSet!! <= item.quantityInStore!!)
                rowView.setBackgroundColor(Color.LTGRAY)
            else
                rowView.setBackgroundColor(Color.TRANSPARENT)

            return rowView
        }
    }
}
