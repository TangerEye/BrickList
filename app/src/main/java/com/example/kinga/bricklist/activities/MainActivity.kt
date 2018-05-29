package com.example.kinga.bricklist.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kinga.bricklist.utilities.Database
import kotlinx.android.synthetic.main.activity_main.*
import com.example.kinga.bricklist.R
import java.io.IOException
import java.sql.SQLException
import android.widget.ListView
import com.example.kinga.bricklist.ListViewAdapters.InventoriesListViewAdapter
import com.example.kinga.bricklist.models.Inventory


class MainActivity : AppCompatActivity() {

    private var url = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"

    private var adapter: InventoriesListViewAdapter? = null
    private var database: Database? = null
    private var inventoriesList: ArrayList<Inventory>? = null

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
        this.inventoriesList = this.database!!.getInventories()
        this.adapter = InventoriesListViewAdapter(this, this.inventoriesList!!)
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
            this.inventoriesList = this.database!!.getInventories()
            val selectedInventory = this.inventoriesList!![position]
            val i = Intent(this, InventoryActivity::class.java)
            i.putExtra("inventoryId", selectedInventory.id)
            i.putExtra("inventoryName", selectedInventory.name)
            startActivity(i)
        }
    }

    override fun onResume() {
        val inventoriesList = this.database!!.getInventories()
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
}
