package com.example.kinga.bricklist.activities

import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import com.example.kinga.bricklist.Database
import com.example.kinga.bricklist.R
import com.example.kinga.bricklist.models.Item
import kotlinx.android.synthetic.main.activity_new_project.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
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
        val database = Database(this)
        try {
            database.createDataBase()
        } catch (e: IOException) {
            throw Error("Error creating database")
        }
        try {
            database.openDataBase()
        } catch (e: SQLException) {
            throw e
        }

        addProjectButton.setOnClickListener {
            projectNumber = projectNumberValue.text.toString()
            val downloadXML = DownloadXML()
            downloadXML.execute()
            var items = parseXml()
            database.addNewInventory(projectNumber, items)
        }
    }

    private inner class DownloadXML: AsyncTask<String, Int, String>() {
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
                    val progress_temp = total.toInt() * 100 / lengthOfFile
                    if (progress_temp % 10 == 0 && progress != progress_temp) {
                        progress = progress_temp
                    }
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
            } catch (e: MalformedURLException) {
                return "Malformed URL"
            } catch (e: FileNotFoundException) {
                return "File not found"
            } catch (e: IOException) {
                return "IO Exception"
            }
            return "success"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result.equals("success")) {
                val toast = Toast.makeText(baseContext, "Project $projectNumber  has been successfully added.", Toast.LENGTH_LONG)
                toast.show()
            }
            else {
                val toast = Toast.makeText(baseContext, "An error occurred while adding the project.", Toast.LENGTH_LONG)
                toast.show()
            }
            finish()
        }
    }

    fun parseXml(): MutableList<Item> {
        var items = mutableListOf<Item>()
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
                        var item = Item()

                        for (j in 0 until children.length) {
                            val node=children.item(j)
                            if (node is Element) {
                                when (node.nodeName) {
                                    "ITEMTYPE" -> { item.itemType = node.textContent.toInt() }
                                    "ITEMID" -> { item.itemId = node.textContent.toInt() }
                                    "QTY" -> { item.quantityInSet = node.textContent.toInt() }
                                    "COLOR" -> { item.color = node.textContent.toInt() }
                                    "EXTRA" -> { item.extra = node.textContent == "Y" }
                                    "ALTERNATE" -> { item.alternate = node.textContent == "Y" }
                                }
                            }
                        }

                        if (item.itemType != null && item.itemId != null && item.quantityInSet != null
                                && item.color != null && item.extra != null && item.alternate == false)
                            items.add(item)
                    }
                }
            }
        }
        return items
    }

}
