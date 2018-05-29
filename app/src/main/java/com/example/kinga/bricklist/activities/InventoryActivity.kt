package com.example.kinga.bricklist.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kinga.bricklist.utilities.Database
import com.example.kinga.bricklist.R
import com.example.kinga.bricklist.models.Item
import kotlinx.android.synthetic.main.activity_inventory.*
import java.io.IOException
import java.sql.SQLException
import android.support.v7.app.AlertDialog
import android.util.Log
import com.example.kinga.bricklist.utilities.ExportXML


class InventoryActivity : AppCompatActivity() {

    private var inventoryId = 0
    private var inventoryName = ""

    @RequiresApi(Build.VERSION_CODES.O)
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

        val inventoryPartsListView: ListView = this.findViewById(R.id.inactiveInventoriesListView)
        var inventoryPartsList = database.getCurrentInventoryParts(inventoryId)
        inventoryPartsList = database.getItemsDesignIds(inventoryPartsList)
        inventoryPartsList = database.getItemsNames(inventoryPartsList)
        inventoryPartsList = database.getItemsColors(inventoryPartsList)

        for (i: Int in 0 until inventoryPartsList.size) {
            inventoryPartsList[i] = database.getItemImage(inventoryPartsList[i])
        }

        val adapter = InventoryPartsListViewAdapter(this, inventoryPartsList)
        inventoryPartsListView.adapter = adapter

        backButton.setOnClickListener {
            database.updateQuantityInStore(this.inventoryId.toString(), inventoryPartsList)
            database.updateInventoryDate(this.inventoryId)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Export missing bricks")
            builder.setMessage("Do you want to export XML file about missing bricks?")

            builder.setPositiveButton("Yes", { dialog, _ ->
                var exportXML = ExportXML(filesDir)
                exportXML.writeXML(inventoryPartsList, inventoryName)
                dialog.dismiss()
                val toast = Toast.makeText(baseContext, "File was exported.", Toast.LENGTH_LONG)
                toast.show()
                super.finish()
            })

            builder.setNegativeButton("No", { dialog, _ ->
                dialog.dismiss()
                super.finish()
            })

            val alert = builder.create()
            alert.show()
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
            val colorTextView = rowView.findViewById(R.id.Color) as TextView
            val nameTextView = rowView.findViewById(R.id.Name) as TextView
            val quantityInSetTextView = rowView.findViewById(R.id.QuantityInSet) as TextView
            val quantityInStoreNumberPicker = rowView.findViewById(R.id.QuantityInStore) as NumberPicker
            val itemImage = rowView.findViewById(R.id.ItemImage) as ImageView

            val item = getItem(position) as Item

            quantityInStoreNumberPicker.minValue = 0
            quantityInStoreNumberPicker.maxValue = 100

            idTextView.text = item.itemId.toString()
            nameTextView.text = item.name
            colorTextView.text = item.color
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
