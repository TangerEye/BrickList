package com.example.kinga.bricklist.models;

class Item {
    var itemType: Int? = null
    var itemId: Int? = null
    var quantityInSet: Int? = null
    var quantityInStore: Int? = null
    var color: Int? = null
    var extra: Boolean? = null
    var alternate: Boolean? = null

    constructor(itemType: Int, itemId: Int, quantityInSet: Int, color: Int, extra: Boolean, alternate: Boolean){
        this.itemType = itemType
        this.itemId = itemId
        this.quantityInSet = quantityInSet
        this.quantityInStore = 0
        this.color = color
        this.extra = extra
        this.alternate = alternate
    }

    constructor()

}
