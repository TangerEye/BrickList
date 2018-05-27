package com.example.kinga.bricklist.models

import android.util.Log

class Item {
    var itemType: String? = null
    var code: String? = null
    var quantityInSet: Int? = null
    var quantityInStore: Int? = 0
    var color: Int? = null
    var extra: Boolean? = null
    var alternate: Boolean? = null
    var itemId: Int? = 0


    constructor(itemType: String, code: String, quantityInSet: Int, quantityInStore: Int, color: Int, extra: Boolean, alternate: Boolean){
        this.itemType = itemType
        this.code = code
        this.quantityInSet = quantityInSet
        this.quantityInStore = quantityInStore
        this.color = color
        this.extra = extra
        this.alternate = alternate
    }

    constructor()

    fun showItem() {
        Log.i("StateChange", " itemType: " + itemType + " code: " + code + " qInSet: " + quantityInSet + " qInStore: " + quantityInStore + " itemId: " + itemId)
    }
}
