package com.example.kinga.bricklist.activities

import android.view.View
import android.view.ViewGroup
import com.example.kinga.bricklist.models.Inventory
import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.BaseAdapter
import com.example.kinga.bricklist.R


class ListViewAdapter(context: Context, private val inventoriesList: ArrayList<Inventory>):
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.activity_listview, parent, false)

        val idTextView = rowView.findViewById(R.id.Id) as TextView
        val nameTextView = rowView.findViewById(R.id.Name) as TextView
        val activeTextView = rowView.findViewById(R.id.Active) as TextView

        val inventory = getItem(position) as Inventory

        idTextView.text = inventory.id.toString()
        nameTextView.text = inventory.name
        if (inventory.active == 0)
            activeTextView.text = "False"
        else if (inventory.active == 1)
            activeTextView.text = "True"

        return rowView
    }
}