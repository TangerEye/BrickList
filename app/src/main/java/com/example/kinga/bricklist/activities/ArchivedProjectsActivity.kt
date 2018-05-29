package com.example.kinga.bricklist.activities

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kinga.bricklist.utilities.Database
import kotlinx.android.synthetic.main.activity_inactive_inventories.*
import com.example.kinga.bricklist.R
import java.io.IOException
import java.sql.SQLException
import com.example.kinga.bricklist.models.Inventory


class ArchivedProjectsActivity : AppCompatActivity() {

    private var adapter: InactiveInventoriesListView? = null
    private var database: Database? = null
    var inventoriesList: ArrayList<Inventory> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inactive_inventories)
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

        val inactiveInventoriesListView: ListView = findViewById(R.id.inactiveInventoriesListView)
        this.inventoriesList = this.database!!.getInactiveInventories()
        this.adapter = InactiveInventoriesListView(this)
        inactiveInventoriesListView.adapter = adapter

        backButton.setOnClickListener{
            super.finish()
        }
    }

    override fun onResume() {
        inventoriesList = this.database!!.getInactiveInventories()
        this.adapter = InactiveInventoriesListView(this)
        inactiveInventoriesListView.adapter = adapter

        this.adapter!!.notifyDataSetChanged()
        super.onResume()
    }

    inner class InactiveInventoriesListView(context: Context):
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
            val rowView = inflater.inflate(R.layout.activity_inactive_inventories_listview, parent, false)

            val idTextView = rowView.findViewById(R.id.Id) as TextView
            val nameTextView = rowView.findViewById(R.id.Name) as TextView
            val activeCheckBox = rowView.findViewById(R.id.archiveCheckBox) as CheckBox
            val deleteCheckBox = rowView.findViewById(R.id.deleteCheckBox) as CheckBox

            val inventory = getItem(position) as Inventory

            idTextView.text = inventory.id.toString()
            nameTextView.text = inventory.name
            activeCheckBox.isChecked = inventory.active == 1

            activeCheckBox.setOnClickListener {
                if (activeCheckBox.isChecked) {
                    inventoriesList[position].active = 1
                    database!!.updateActiveInventories(inventoriesList)
                    inventoriesList = database!!.getInactiveInventories()
                    notifyDataSetChanged()
                }
            }

            deleteCheckBox.setOnClickListener {
                database!!.removeInventory(inventory)
                inventoriesList = database!!.getInactiveInventories()
                notifyDataSetChanged()
            }

            return rowView
        }
    }

}
