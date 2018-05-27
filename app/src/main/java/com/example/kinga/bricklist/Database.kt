package com.example.kinga.bricklist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.BitmapFactory
import android.util.Log
import com.example.kinga.bricklist.models.Inventory
import com.example.kinga.bricklist.models.Item
import java.io.FileOutputStream
import java.io.IOException

class Database: SQLiteOpenHelper {

    private var myDataBase: SQLiteDatabase? = null
    private var context2: Context
    private var DATABASE_NAME = "BrickList.db"
    private var DATABASE_PATH = "/data/data/com.example.kinga.bricklist/databases/"

    constructor(context: Context):super(context, "BrickList.db", null, 1){
        this.context2 = context
    }

    override fun onCreate(p0: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    fun createDataBase() {
        val dbExist = checkDatabase()

        if (dbExist) {}
        else {
            this.readableDatabase
            try {
                copyDataBase()
            } catch (e: IOException) {
                throw Error("Error copying database")
            }
        }
    }

    fun openDataBase() {
        val myPath = DATABASE_PATH + DATABASE_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    override fun close() {
        if (myDataBase != null)
            myDataBase!!.close()
        super.close()
    }

    private fun checkDatabase(): Boolean {
        var checkDB: SQLiteDatabase? = null
        try {
            val myPath = DATABASE_PATH + DATABASE_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: SQLiteException) {}
        return if (checkDB != null) {
            checkDB.close()
            true
        }
        else false
    }

    private fun copyDataBase() {
        val myInput = context2.assets.open(DATABASE_NAME)
        val outFileName = DATABASE_PATH + DATABASE_NAME
        val myOutput = FileOutputStream(outFileName)

        //transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length = myInput.read(buffer)
        while (length > 0) {
            myOutput.write(buffer, 0, length)
            length = myInput.read(buffer)
        }
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    fun checkIfInventoryExists(inventoryNumber: String): Boolean {
        val db = this.readableDatabase
        val query = "select * from Inventories where Name = $inventoryNumber"
        val cursor = db.rawQuery(query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    fun addNewInventory(inventoryNumber: String, items: MutableList<Item>) {
        val inventoryID = getLastId("Inventories") + 1

        val db = this.writableDatabase
        db.beginTransaction()
        var values = ContentValues()

        values.put("_id", inventoryID)
        values.put("Name", inventoryNumber)
        values.put("Active", 1)
        values.put("LastAccessed", 1)
        db.insert("Inventories", null, values)

        for (item in items){
            values = ContentValues()
            values.put("_id", getLastId("InventoriesParts") + 1)
            values.put("InventoryID", inventoryID)
            values.put("TypeID", item.itemType)
            values.put("ItemID", item.itemId)
            values.put("QuantityInSet", item.quantityInSet)
            values.put("QuantityInStore", 0)
            values.put("ColorID", item.color)
            values.put("Extra", item.extra)
            db.insert("InventoriesParts", null, values)
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    private fun getLastId(inventoryName: String): Int {
        val query = "select max(_id) from $inventoryName;"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var lastId = 0
        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(0)
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return lastId
    }

    fun getInventories(): ArrayList<Inventory>{
        val query = "select _id, Name, Active from Inventories"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val inventoriesList = ArrayList<Inventory>()
        if(cursor.moveToFirst()) {
            inventoriesList.add(Inventory(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)))
        }
        while(cursor.moveToNext()){
            inventoriesList.add(Inventory(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)))
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }

        return inventoriesList
    }

    fun getCurrentInventoryParts(inventoryId: Int): ArrayList<Item>{
        val query = "select TypeID, ItemID, QuantityInSet, QuantityInStore, ColorID, extra from InventoriesParts where InventoryID = $inventoryId"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val items = ArrayList<Item>()
        if(cursor.moveToFirst()) {
            items.add(Item(cursor.getString(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5) == 1, false))
        }
        while(cursor.moveToNext()) {
            items.add(Item(cursor.getString(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5) == 1, false))
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }

        return items
    }


    fun getItemsIds(items: ArrayList<Item>): ArrayList<Item>{
        items.forEach {
            val query = "select _id from Parts where Code=\"${it.code}\""
            val db = this.readableDatabase
            val cursor = db.rawQuery(query, null)
            if(cursor.moveToFirst()) {
                it.itemId = cursor.getInt(0)
            }
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
        return items
    }

    fun getItemImage(item: Item): Item {
        val query = "select Image from Codes where Code=" + item.designId + ";"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val blob: ByteArray?
        if (cursor.moveToFirst()) {
            blob = cursor.getBlob(0)
            if (blob != null) {
                Log.i("StateChange", "blob: $blob")
                item.image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                Log.i("StateChange", "item.image: " + BitmapFactory.decodeByteArray(blob, 0, blob.size))
            }
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return item
    }

    private fun checkIfDesignIDExists(color: Int, itemId: Int): Boolean {
        val db = this.readableDatabase
        val query = "select Code from Codes where ColorID=$color and ItemID=$itemId"
        val cursor = db.rawQuery(query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return true
    }

    fun getItemsDesignIds(items: ArrayList<Item>): ArrayList<Item>{
        items.forEach {
            if (checkIfDesignIDExists(it.color!!, it.itemId!!)) {
                val query = "select Code from Codes where ColorID=${it.color} and ItemID=${it.itemId}"
                val db = this.readableDatabase
                val cursor = db.rawQuery(query, null)
                if(cursor.moveToFirst()) {
                    it.designId = cursor.getInt(0)
                }
                if (cursor != null && !cursor.isClosed) {
                    cursor.close()
                }
            }
        }
        return items
    }

    fun saveImageToDatabase(item: Item, image:ContentValues){
        val db = writableDatabase
        val selection = "Code=" + item.designId + ";"
        db.update("CODES", image, selection, null)
        db.close()
    }

    fun updateQuantityInStore(inventoryId: String, items: ArrayList<Item>){
        items.forEach {
            val db = this.writableDatabase
            db.beginTransaction()
            val query = "update InventoriesParts set QuantityInStore=" + it.quantityInStore + " where InventoryID=" + inventoryId + " and ItemID=" + it.code + ";"
            Log.i("StateChange", query)
            writableDatabase.execSQL(query)
            writableDatabase.setTransactionSuccessful()
            writableDatabase.endTransaction()
        }
    }
}