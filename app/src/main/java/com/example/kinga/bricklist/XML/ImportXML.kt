package com.example.kinga.bricklist.XML

import android.os.AsyncTask
import android.os.Environment
import com.example.kinga.bricklist.models.Item
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
import javax.xml.parsers.DocumentBuilderFactory

class ImportXML constructor(private val urlPart: String, private val number: String) {

    fun downloadXml() {
        val downloadXMLAsyncTask = DownloadXMLAsyncTask()
        downloadXMLAsyncTask.execute()
        //var items = parseXml()
        //return items
    }


    private inner class DownloadXMLAsyncTask: AsyncTask<String, Int, String>() {
        override fun doInBackground(vararg p0: String?): String {
            try {
                var filesDir = Environment.getExternalStorageDirectory().absolutePath + "/XML"
                val url = URL(urlPart + number + ".xml")
                val connection = url.openConnection()
                connection.connect()
                val lengthOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File(filesDir)
                if (!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream("$testDirectory/" + number + ".xml")
                val data = ByteArray(1024)
                var count = 0
                var total: Long = 0
                var progress = 0
                count = isStream.read(data)
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
    }
}