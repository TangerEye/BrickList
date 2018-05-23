package com.example.kinga.bricklist

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class Database(context: Context, name: String?,
               factory: SQLiteDatabase.CursorFactory?, version: Int): SQLiteOpenHelper(context,
        "BrickList.db", factory, 1) {

    var database: SQLiteDatabase? = null
    val categoriesTable = "Categories"
    val codesTable = "Codes"
    val colorsTable = "Colors"
    val inventoriesTable = "Inventories"
    val inventoriesPartsTable = "InventoriesParts"
    val itemTypesTable = "ItemTypes"
    val partsTable = "Parts"

    val inventoriesId = "_id"
    val InventoriesName = "Name"
    val InventoriesActive = "Active"
    val InventoriesLastAccessed = "LastAccessed"

    override fun onCreate(db: SQLiteDatabase) {
        this.database = this.writableDatabase
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int,
                           newVersion: Int) {
    }

    fun addNewInventory() {

    }

}