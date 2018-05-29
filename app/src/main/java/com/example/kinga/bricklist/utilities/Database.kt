package com.example.kinga.bricklist.utilities

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.example.kinga.bricklist.models.Inventory
import com.example.kinga.bricklist.models.Item
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNewInventory(inventoryNumber: String, items: MutableList<Item>) {
        val inventoryID = getLastId("Inventories") + 1

        val db = this.writableDatabase
        db.beginTransaction()
        var values = ContentValues()

        values.put("_id", inventoryID)
        values.put("Name", inventoryNumber)
        values.put("Active", 1)
        values.put("LastAccessed", Inventory().getCurrentDate())
        db.insert("Inventories", null, values)

        for (item in items){
            values = ContentValues()
            values.put("_id", getLastId("InventoriesParts") + 1)
            values.put("InventoryID", inventoryID)
            values.put("TypeID", item.itemType)
            values.put("ItemID", item.itemId)
            values.put("QuantityInSet", item.quantityInSet)
            values.put("QuantityInStore", 0)
            values.put("ColorID", item.colorCode)
            values.put("Extra", item.extra)
            db.insert("InventoriesParts", null, values)
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateInventoryDate(inventoryId: Int){
        val db = this.writableDatabase
        db.beginTransaction()
        val query = "update Inventories set LastAccessed=" + Inventory().getCurrentDate() + " where _id=" + inventoryId + ";"
        writableDatabase.execSQL(query)
        writableDatabase.setTransactionSuccessful()
        writableDatabase.endTransaction()
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

    fun getInactiveInventories(): ArrayList<Inventory>{
        val query = "select _id, Name, Active, LastAccessed from Inventories where Active=0"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val inventoriesList = ArrayList<Inventory>()
        if(cursor.moveToFirst()) {
            inventoriesList.add(Inventory(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)))
        }
        while(cursor.moveToNext()){
            inventoriesList.add(Inventory(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)))
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }

        return inventoriesList
    }

    fun getActiveInventories(): ArrayList<Inventory>{
        val query = "select _id, Name, Active, LastAccessed from Inventories where Active=1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val inventoriesList = ArrayList<Inventory>()
        if(cursor.moveToFirst()) {
            inventoriesList.add(Inventory(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)))
        }
        while(cursor.moveToNext()){
            inventoriesList.add(Inventory(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)))
        }
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
        return inventoriesList
    }

    fun updateActiveInventories(inventories: ArrayList<Inventory>){
        inventories.forEach {
            val db = this.writableDatabase
            db.beginTransaction()
            val query = "update Inventories set Active=" + it.active + " where _id=" + it.id + ";"
            writableDatabase.execSQL(query)
            writableDatabase.setTransactionSuccessful()
            writableDatabase.endTransaction()
        }
    }

    fun removeInventory(inventory: Inventory){
        val db = this.writableDatabase
        db.beginTransaction()
        var query = "delete from Inventories where _id=" + inventory.id + ";"
        writableDatabase.execSQL(query)
        query = "delete from InventoriesParts where InventoryID=" + inventory.id + ";"
        writableDatabase.execSQL(query)
        writableDatabase.setTransactionSuccessful()
        writableDatabase.endTransaction()
    }

    fun getCurrentInventoryParts(inventoryId: Int): ArrayList<Item>{
        val query = "select _id, TypeID, ItemID, QuantityInSet, QuantityInStore, ColorID, extra from InventoriesParts where InventoryID = $inventoryId"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        val items = ArrayList<Item>()
        if(cursor.moveToFirst()) {
            items.add(Item(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6) == 1, false))
        }
        while(cursor.moveToNext()) {
            items.add(Item(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6) == 1, false))
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
            } else {
                it.itemId = -9
            }
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
        return items
    }

    fun getItemsColors(items: ArrayList<Item>): ArrayList<Item>{
        items.forEach {
            val query = "select Name from Colors where Code=\"${it.colorCode}\""
            val db = this.readableDatabase
            val cursor = db.rawQuery(query, null)
            if(cursor.moveToFirst()) {
                it.color = cursor.getString(0)
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
                item.image = BitmapFactory.decodeByteArray(blob, 0, blob.size)
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
            if (checkIfDesignIDExists(it.colorCode!!, it.itemId!!)) {
                val query = "select Code from Codes where ColorID=${it.colorCode} and ItemID=${it.itemId}"
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

    fun getItemsNames(items: ArrayList<Item>): ArrayList<Item>{
        items.forEach {
            val query = "select Name from Parts where _id=${it.itemId}"
            val db = this.readableDatabase
            val cursor = db.rawQuery(query, null)
            if(cursor.moveToFirst()) {
                it.name = cursor.getString(0)
            }
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
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
            val query = "update InventoriesParts set QuantityInStore=" + it.quantityInStore + " where InventoryID=" + inventoryId + " and _id=" + it.id+ ";"
            writableDatabase.execSQL(query)
            writableDatabase.setTransactionSuccessful()
            writableDatabase.endTransaction()
        }
    }
}