package com.example.sudoku.login

public class Item {
    var Level: String = ""
    var Time: String = ""
    var ID : String =""

    public fun User() {}

    constructor()
    constructor(Level: String, Time: String, id: String) {
        this.Level = Level
        this.Time = Time
        this.ID=id
    }
}