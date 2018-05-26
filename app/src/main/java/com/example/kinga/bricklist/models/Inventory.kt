package com.example.kinga.bricklist.models;

class Inventory {
    var id: Int = -1
    var name: String = ""
    var active: Int = -1

    constructor(id: Int, name: String, active: Int){
        this.id = id
        this.name = name
        this.active = active
    }

    constructor()
}
