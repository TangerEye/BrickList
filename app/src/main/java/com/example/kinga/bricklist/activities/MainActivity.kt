package com.example.kinga.bricklist.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import com.example.kinga.bricklist.utilities.Database
import kotlinx.android.synthetic.main.activity_main.*
import com.example.kinga.bricklist.R
import java.io.IOException
import java.sql.SQLException
import android.widget.ListView
import android.widget.TextView
import com.example.kinga.bricklist.models.Inventory


class MainActivity : AppCompatActivity() {

    private var url = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"

    private var adapter: InventoriesListViewAdapter? = null
    private var database: Database? = null
    var inventoriesList: ArrayList<Inventory> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.database = Database(this)
        try {
            this.database!!.createDataBase()
        } catch (e: IOException) {
            throw Error("Error creating database")
        }
        try {
            this.database!!.openDataBase()
        } catch (e: SQLException) {
            throw e
        }

        val inventoriesListView: ListView = findViewById(R.id.inventoriesListView)
        this.inventoriesList = this.database!!.getActiveInventories()
        this.adapter = InventoriesListViewAdapter(this, this.inventoriesList)
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

        archivedProjectsButton.setOnClickListener {
            val i = Intent(this, ArchivedProjectsActivity::class.java)
            startActivity(i)
        }
    }

    override fun onResume() {
        inventoriesList = this.database!!.getActiveInventories()
        this.adapter = InventoriesListViewAdapter(this, inventoriesList)
        inventoriesListView.adapter = adapter

        this.adapter!!.notifyDataSetChanged()
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == 999 && resultCode == Activity.RESULT_OK && data != null){
            url = data.extras.getString("url")
        }
    }

    inner class InventoriesListViewAdapter(context: Context, private var inventoriesList: ArrayList<Inventory>):
            BaseAdapter() {

        private var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return inventoriesList.size
        }

        override fun getItem(position: Int): Any {
            return inventoriesList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @SuppressLint("ViewHolder", "SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val rowView = inflater.inflate(R.layout.activity_inventories_listview, parent, false)

            val idTextView = rowView.findViewById(R.id.Id) as TextView
            val nameTextView = rowView.findViewById(R.id.Name) as TextView
            val activeCheckBox = rowView.findViewById(R.id.archiveCheckBox) as CheckBox
            val lastAccessed = rowView.findViewById(R.id.lastAccessed) as TextView

            val inventory = getItem(position) as Inventory

            idTextView.text = inventory.id.toString()
            nameTextView.text = inventory.name
            activeCheckBox.isChecked = inventory.active == 1
            val date: String = inventory.date.toString()
            Log.i("StateChange", "date: " + date + " length: " + date.length)
            lastAccessed.text = date.substring(6, 8) + "/" + date.substring(4, 6) + "/" + date.substring(0, 4)

            idTextView.setOnClickListener {
                this.inventoriesList = database!!.getActiveInventories()
                val selectedInventory = this.inventoriesList[position]
                val i = Intent(baseContext, InventoryActivity::class.java)
                i.putExtra("inventoryId", selectedInventory.id)
                i.putExtra("inventoryName", selectedInventory.name)
                startActivity(i)
            }

            nameTextView.setOnClickListener {
                this.inventoriesList = database!!.getActiveInventories()
                val selectedInventory = this.inventoriesList[position]
                val i = Intent(baseContext, InventoryActivity::class.java)
                i.putExtra("inventoryId", selectedInventory.id)
                i.putExtra("inventoryName", selectedInventory.name)
                startActivity(i)
            }

            lastAccessed.setOnClickListener {
                this.inventoriesList = database!!.getActiveInventories()
                val selectedInventory = this.inventoriesList[position]
                val i = Intent(baseContext, InventoryActivity::class.java)
                i.putExtra("inventoryId", selectedInventory.id)
                i.putExtra("inventoryName", selectedInventory.name)
                startActivity(i)
            }

            activeCheckBox.setOnClickListener {
                if (!activeCheckBox.isChecked) {
                    inventoriesList[position].active = 0
                    database!!.updateActiveInventories(inventoriesList)
                    inventoriesList = database!!.getActiveInventories()
                    notifyDataSetChanged()
                }
            }
            return rowView
        }
    }

}
