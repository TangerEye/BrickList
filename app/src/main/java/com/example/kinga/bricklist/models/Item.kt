package com.example.kinga.bricklist.models

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.util.Log
import android.graphics.BitmapFactory
import android.os.AsyncTask
import com.example.kinga.bricklist.Database
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL


class Item {
    var id: Int? = null
    var itemType: String? = null
    var code: String? = null
    var quantityInSet: Int? = null
    var quantityInStore: Int? = 0
    var color: Int? = null
    var extra: Boolean? = null
    var alternate: Boolean? = null
    var itemId: Int? = -9
    var designId: Int? = null
    var image: Bitmap? = null
    var imageSrc: String? = null
    var name: String = ""

    constructor(id: Int, itemType: String, itemId: Int, quantityInSet: Int, quantityInStore: Int, color: Int, extra: Boolean, alternate: Boolean){
        this.id = id
        this.itemType = itemType
        this.itemId = itemId
        this.quantityInSet = quantityInSet
        this.quantityInStore = quantityInStore
        this.color = color
        this.extra = extra
        this.alternate = alternate
    }

    constructor()

    fun showItem() {
        Log.i("StateChange", "itemId: " + itemId + " qInSet: " + quantityInSet + " qInStore: " + quantityInStore + " image: " + image + " designId: " + designId + " color: " + color)
    }


    @SuppressLint("StaticFieldLeak")
    inner class DownloadImage(private var database: Database, private var item: Item, private var url: String): AsyncTask<String, Int, String>() {
        override fun doInBackground(vararg params: String?): String {
            try {
                BufferedInputStream(URL(url).content as InputStream).use {
                    Log.i("StateChange", "trying: " + url)
                    val baf = ArrayList<Byte>()
                    var current: Int
                    while (true) {
                        current = it.read()
                        if (current == -1)
                            break
                        baf.add(current.toByte())
                    }
                    val blob = baf.toByteArray()
                    val blobValues = ContentValues()
                    blobValues.put("Image", blob)
                    database.saveImageToDatabase(item, blobValues)
                }
            } catch (e: IOException) {
                try {
                    url = "http://img.bricklink.com/P/" + item.color + "/" + item.code + ".gif"
                    Log.i("StateChange", "trying2: " + url)

                    BufferedInputStream(URL(url).content as InputStream).use {
                        val baf = ArrayList<Byte>()
                        var current: Int
                        while (true) {
                            current = it.read()
                            if (current == -1)
                                break
                            baf.add(current.toByte())
                        }
                        val blob = baf.toByteArray()
                        val blobValues = ContentValues()
                        blobValues.put("Image", blob)
                        database.saveImageToDatabase(item, blobValues)
                    }
                } catch (e: IOException) {
                    try {
                        item.imageSrc = "https://www.bricklink.com/PL/" + item.code + ".jpg"
                        Log.i("StateChange", "trying3: " + url)

                        BufferedInputStream(URL(url).content as InputStream).use {
                            val baf = ArrayList<Byte>()
                            var current: Int
                            while (true) {
                                current = it.read()
                                if (current == -1)
                                    break
                                baf.add(current.toByte())
                            }
                            val blob = baf.toByteArray()
                            val blobValues = ContentValues()
                            blobValues.put("Image", blob)
                            database.saveImageToDatabase(item, blobValues)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return "IOException"
                    }
                }
            }
            return "success"
        }
    }
}
