package com.example.kinga.bricklist.utilities


import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.example.kinga.bricklist.models.Item
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class ExportXML(var filesDir: File){

    fun writeXML(items: ArrayList<Item>, inventoryName: String) {
        val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.newDocument()

        val rootElement: Element = doc.createElement("INVENTORY")

        items.forEach{
            if (it.quantityInStore!! < it.quantityInSet!!) {
                val item: Element = doc.createElement("ITEM")

                val itemType: Element = doc.createElement("ITEMTYPE")
                itemType.appendChild(doc.createTextNode(it.itemType))

                val itemId: Element = doc.createElement("ITEMID")
                itemId.appendChild(doc.createTextNode(it.itemId.toString()))

                val itemColor: Element = doc.createElement("COLOR")
                itemColor.appendChild(doc.createTextNode(it.colorCode.toString()))

                val itemQty: Element = doc.createElement("QTYFILLED")
                itemQty.appendChild(doc.createTextNode((it.quantityInSet!! - it.quantityInStore!!).toString()))

                item.appendChild(itemType)
                item.appendChild(itemId)
                item.appendChild(itemColor)
                item.appendChild(itemQty)

                rootElement.appendChild(item)
            }
        }
        doc.appendChild(rootElement)

        // val filesDir = Environment.getExternalStorageDirectory().absolutePath
        val outDir = File(filesDir, "WantedLists")
        outDir.mkdir()
        val file = File(outDir, inventoryName + ".xml")
        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        transformer.transform(DOMSource(doc), StreamResult(file))
    }
}