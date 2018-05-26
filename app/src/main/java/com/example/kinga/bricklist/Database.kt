package com.example.kinga.bricklist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.kinga.bricklist.models.Inventory
import com.example.kinga.bricklist.models.Item
import java.io.FileOutputStream
import java.io.IOException

class Database: SQLiteOpenHelper {

    private var myDataBase: SQLiteDatabase? = null
    private var context2: Context
    private var DATABASE_NAME = "BrickList2.db"
    private var DATABASE_PATH = "/data/data/com.example.kinga.bricklist/databases/"

    constructor(context: Context):super(context, "BrickList2.db", null, 7){
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

    fun addNewInventory(inventoryNumber: String, items: MutableList<Item>) {
        /*var date = LocalDate.now()
        var dateString = date.toString().substring(0, 4) + date.toString().substring(6, 8) + date.toString().substring(10, 12)
        Log.i("stateChange", dateString)*/
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
            Log.i("StateChange", "last index: " + lastId + "from: " + inventoryName)
        }
        cursor.close()
        return lastId
    }

    fun getInventories(): ArrayList<Inventory>{
        val query = "select _id, Name, Active from Inventories"
        val db = this.readableDatabase
        val cursor = db.rawQuery(query, null)
        var inventoriesList = ArrayList<Inventory>()
        if(cursor.moveToFirst()) {
            inventoriesList.add(Inventory(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)))
        }
        while(cursor.moveToNext()){
            inventoriesList.add(Inventory(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)))
        }
        return inventoriesList
    }
}