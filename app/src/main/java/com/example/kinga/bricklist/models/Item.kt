package com.example.kinga.bricklist.models;

class Item {
    var itemType: String? = null
    var itemId: String? = null
    var quantityInSet: Int? = null
    var color: Int? = null
    var extra: Boolean? = null
    var alternate: Boolean? = null

    constructor(itemType: String, itemId: String, quantityInSet: Int, color: Int, extra: Boolean, alternate: Boolean){
        this.itemType = itemType
        this.itemId = itemId
        this.quantityInSet = quantityInSet
        this.color = color
        this.extra = extra
        this.alternate = alternate
    }

    constructor()

}
