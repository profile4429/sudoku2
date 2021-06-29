package com.example.sudoku.login

public class User {
    var Name : String? = ""
    var Email : String = ""
    var Level : String = ""
    var Time : Int = 0
    var Result : String=""
    var Level_Time : String = ""
    var Top_time_easy: String=""
    var Top_time_normal: String=""
    var Top_time_hard: String=""

    public fun User(){}
    constructor()
    constructor(Name: String, Email: String, Level: String, Time: Int,Result: String, Level_Time: String, top_time_easy:String, top_time_normal:String, top_time_hard:String) {
        this.Name = Name
        this.Email = Email
        this.Level = Level
        this.Time = Time
        this.Result = Result
        this.Level_Time = Level_Time
        this.Top_time_easy = top_time_easy
        this.Top_time_normal= top_time_normal
        this.Top_time_hard = top_time_hard
    }





}