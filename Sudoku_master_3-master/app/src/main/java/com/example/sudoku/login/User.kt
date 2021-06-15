package com.example.sudoku.login

import java.io.Serializable

class User {
    var Name : String = ""
    var Email : String = ""
    var Level : String = ""
    var Time : Int = 0

    constructor()
    constructor(Name: String, Email: String, Level: String, Time: Int) {
        this.Name = Name
        this.Email = Email
        this.Level = Level
        this.Time = Time
    }


}