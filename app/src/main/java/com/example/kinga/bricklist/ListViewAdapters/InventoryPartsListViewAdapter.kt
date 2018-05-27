package com.example.kinga.bricklist.ListViewAdapters

import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.widget.*
import com.example.kinga.bricklist.R
import com.example.kinga.bricklist.models.Item

class InventoryPartsListViewAdapter(context: Context, private val inventoryPartsList: ArrayList<Item>):
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.activity_inventory_listview, parent, false)

        val idTextView = rowView.findViewById(R.id.ItemId) as TextView
        val colorTextView = rowView.findViewById(R.id.ColorId) as TextView
        val quantityInSetTextView = rowView.findViewById(R.id.QuantityInSet) as TextView
        val quantityInStoreNumberPicker = rowView.findViewById(R.id.QuantityInStore) as NumberPicker

        quantityInStoreNumberPicker.minValue = 0
        quantityInStoreNumberPicker.maxValue = 100
        quantityInStoreNumberPicker.setOnValueChangedListener{_, _, newVal ->
            quantityInStoreNumberPicker.tag = newVal
        }

        val item = getItem(position) as Item

        idTextView.text = item.code.toString()
        colorTextView.text = item.color.toString()
        quantityInSetTextView.text = item.quantityInSet.toString()
        quantityInStoreNumberPicker.value = item.quantityInStore!!

        return rowView
    }
}