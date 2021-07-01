package com.example.sudoku.login

import android.provider.ContactsContract

public class Item {
    var Name : String? = ""
    var Email : String = ""
    var Level: String = ""
    var Time: String = ""
    var ID : String =""
    private var expanded = false

    public fun User() {}

    constructor()
    constructor(name:String, email: String,Level: String, Time: String, id: String) {
        this.Name = name
        this.Email =email
        this.Level = Level
        this.Time = Time
        this.ID=id
        this.expanded = false
    }

    fun isExpanded(): Boolean {
        return expanded
    }

    fun setExpanded(expanded: Boolean) {
        this.expanded = expanded
    }
}