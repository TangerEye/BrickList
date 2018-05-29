package com.example.kinga.bricklist.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import com.example.kinga.bricklist.utilities.Database
import com.example.kinga.bricklist.R
import com.example.kinga.bricklist.models.Item
import kotlinx.android.synthetic.main.activity_new_project.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import java.sql.SQLException
import javax.xml.parsers.DocumentBuilderFactory

class NewProjectActivity : AppCompatActivity() {

    private var urlPart = ""
    private var projectNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_project)
        urlPart = intent.getStringExtra("url")
        var database = Database(this)
        try {
            database.createDataBase()
        } catch (e: IOException) {
            throw Error("Error creating database")
        }
        try {
            database.openDataBase()
        } catch (e: SQLException) {
            throw Error("Error opening database")
        }

        addProjectButton.setOnClickListener {
            projectNumber = projectNumberValue.text.toString()
            if (database.checkIfInventoryExists(projectNumber)) {
                val toast = Toast.makeText(baseContext, "This project already exists.", Toast.LENGTH_LONG)
                toast.show()
            } else {
                val downloadXML = DownloadXML(database)
                downloadXML.execute()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DownloadXML(var database: Database): AsyncTask<String, Int, String>() {

        private var success: Boolean = false

        override fun doInBackground(vararg params: String?): String {
            try {
                val url = URL("$urlPart$projectNumber.xml")
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                if (!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream( "$testDirectory/$projectNumber.xml")
                val data = ByteArray(1024)
                var total: Long = 0
                var progress = 0
                var count = isStream.read(data)
                while (count != -1) {
                    total += count.toLong()
                    val progressTemp = total.toInt() * 100 / lengthOfFile
                    if (progressTemp % 10 == 0 && progress != progressTemp) {
                        progress = progressTemp
                    }
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
                this.success = true
            } catch (e: MalformedURLException) {
                return "Malformed URL"
            } catch (e: FileNotFoundException) {
                return "File not found"
            } catch (e: IOException) {
                return "IO Exception"
            }
            return "success"
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!result.equals("success")) {
                val toast = Toast.makeText(baseContext, "An error occurred while adding the project.", Toast.LENGTH_LONG)
                toast.show()
            }
            if (this.success) {
                add()
            }
            finish()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun add() {
            var items = parseXml() // read items from xml file
            items = database.getItemsIds(items) // read items ids from database
            items = removeItemsNotFromDatabase(items) // remove items that are not in database
            items = database.getItemsDesignIds(items) // read items design ids from database
            for (i: Int in 0 until items.size) { // read items images from database
                items[i] = database.getItemImage(items[i])
            }
            for (i: Int in 0 until items.size) {
                if (items[i].image == null) { //if theres no image in database
                    items[i].imageSrc = "https://www.lego.com/service/bricks/5/2/" + items[i].designId
                    items[i].DownloadImage(database, items[i], items[i].imageSrc!!).execute()
                }
            }
            database.addNewInventory(projectNumber, items)
            val toast = Toast.makeText(baseContext, "Project $projectNumber was added successfully.", Toast.LENGTH_LONG)
            toast.show()
        }

        private fun removeItemsNotFromDatabase(items: ArrayList<Item>): ArrayList<Item> {
            var toRemove: ArrayList<Item> = arrayListOf()
            for (i: Int in 0 until items.size) {
                if (items[i].itemId == -9) {
                    val toast = Toast.makeText(baseContext, "There is no item with ItemID: " + items[i].code + " and color: " + items[i].colorCode, Toast.LENGTH_LONG)
                    toast.show()
                    toRemove.add(items[i])
                }
            }
            toRemove.forEach{
                items.remove(it)
            }
            return items
        }

        private fun parseXml(): ArrayList<Item> {
            val items = ArrayList<Item>()
            val filename = "$projectNumber.xml"
            val path = filesDir
            val inDir = File(path, "XML")

            if (inDir.exists()) {

                val file = File(inDir, filename)
                if(file.exists()) {

                    val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
                    xmlDoc.documentElement.normalize()
                    val itemsXML: NodeList = xmlDoc.getElementsByTagName("ITEM")
                    for (i in 0 until itemsXML.length) {
                        val itemNode: Node = itemsXML.item(i)
                        if (itemNode.nodeType == Node.ELEMENT_NODE) {
                            val elem = itemNode as Element
                            val children = elem.childNodes
                            val item = Item()

                            for (j in 0 until children.length) {
                                val node=children.item(j)
                                if (node is Element) {
                                    when (node.nodeName) {
                                        "ITEMTYPE" -> { item.itemType = node.textContent }
                                        "ITEMID" -> { item.code = node.textContent }
                                        "QTY" -> { item.quantityInSet = node.textContent.toInt() }
                                        "COLOR" -> { item.colorCode = node.textContent.toInt() }
                                        "EXTRA" -> { item.extra = node.textContent == "Y" }
                                        "ALTERNATE" -> { item.alternate = node.textContent == "Y" }
                                    }
                                }
                            }

                            if (item.itemType != null && item.code != null && item.quantityInSet != null
                                    && item.colorCode != null && item.extra != null && item.alternate == false)
                                items.add(item)
                        }
                    }
                }
            }
            return items
        }
    }
}
