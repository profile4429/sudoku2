package com.example.sudoku.view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sudoku.R
import com.example.sudoku.login.Item
import com.example.sudoku.login.ProfieActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profie.*
import kotlinx.android.synthetic.main.activity_ranking.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class RankingActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var listview: ListView
    private lateinit var items: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setSelectedItemId(R.id.ranking)
        var intent: Intent = getIntent()
        var id: String = intent.getStringExtra("Id")

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, ProfieActivity::class.java)
                    intent.putExtra("iD",id)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                R.id.ranking -> {
                }
                R.id.Statistics->{
                    val intent = Intent(this, StatisticsActivity::class.java)
                    intent.putExtra("Id",id)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            false
        }
        //ListView

       setListeners()

    }
    private fun makeBackground(view: View){
        when (view.id) {
            R.id.btn_personal -> {
                view.setBackgroundResource(R.drawable.round_bg)
                btn_public.setBackgroundResource(R.drawable.round_border)
                val color1 = getString(java.lang.String.valueOf(R.color.White).toInt())
                val color2 = getString(java.lang.String.valueOf(R.color.Black).toInt())
                btn_personal.setTextColor(Color.parseColor(color1))
                btn_public.setTextColor(Color.parseColor(color2))
                showDialog("1")
            }
            R.id.btn_public -> {
                view.setBackgroundResource(R.drawable.round_bg)
                btn_personal.setBackgroundResource(R.drawable.round_border)
                val color1 = getString(java.lang.String.valueOf(R.color.White).toInt())
                val color2 = getString(java.lang.String.valueOf(R.color.Black).toInt())
                btn_personal.setTextColor(Color.parseColor(color2))
                btn_public.setTextColor(Color.parseColor(color1))
                showDialog("2")
            }
            else -> view.setBackgroundResource(R.color.White)
        }
    }
    private fun setListeners(){
        val btnPerson = findViewById<Button>(R.id.btn_personal)
        val btnPublic = findViewById<Button>(R.id.btn_public)
        val clickableViews: List<View> = listOf(btnPerson,btnPublic)
        for (item in clickableViews){
            item.setOnClickListener{makeBackground(it)}
        }
    }
    private fun showList(level:String){
        val flowersAdapter = FlowersAdapter { flower -> adapterOnClick(flower) }
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val arr = java.util.ArrayList<Item>()
        var intent: Intent = getIntent()
        var id: String = intent.getStringExtra("Id")
        var Tab:String=""
        items = ArrayList<String>()
        var I = ArrayList<String>()
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users").child(id).child("level_Time")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    var Id = ds.key.toString()
                    var level_time = ds.getValue().toString()
                    //if(level=="Easy"){
                    val f = Pattern.compile(level)
                    val m = f.matcher(level_time)
                    while (m.find()) {
                            items.add(level_time)
                    }
                }
                if(items.size != 0 ){
                    val ar = java.util.ArrayList<Int>()
                    var s: List<String>
                    for (i in 0 until (items.size)) {
                        if(level == "Easy" || level == "Hard"){
                            Tab = "         "
                        }
                        else if (level == "Normal"){
                            Tab = "   "
                        }
                        s = items.get(i).toString().split(Tab," ")
                        val m = Integer.parseInt(s[1])
                        ar.add(m)
                    }
                    sortASC(ar)
                    for (i in 0 until (items.size)) {
                        val p = Pattern.compile(ar[i].toString())
                        for (j in 0 until (items.size)) {
                            val m = p.matcher(items.get(j).toString())
                            while (m.find()) {
                                val user = Item("Top "+(i+1).toString(),items.get(j), 0.toString())
                                arr.add(user)
                                flowersAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    AddDataTopTime(arr.get(0).Time,id)
                    flowersAdapter.submitList(arr)
                    recyclerView.adapter = flowersAdapter
                }
                else{
                    recyclerView.adapter = flowersAdapter
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@RankingActivity,"Lấy data thất bại",
                    Toast.LENGTH_SHORT).show()
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)

    }
    fun sortASC(arr: java.util.ArrayList<Int>) {
        var temp = arr[0]
        for (i in 0 until arr.size - 1) {
            for (j in i + 1 until arr.size) {
                if (arr[i] > arr[j]) {
                    temp = arr[j]
                    arr[j] = arr[i]
                    arr[i] = temp
                }
            }
        }
    }
    //Them du lieu thoi gian nhanh nhat cua easy,normal,hard vao firebase
    private fun AddDataTopTime(string: String,id: String){
        val diffName: Array<String> = arrayOf("Easy", "Normal", "Hard")
        for(i in 0 until diffName.size){
            when(diffName.get(i)) {
                "Easy" -> {
                    val p = Pattern.compile("Easy")
                    val m = p.matcher(string)
                    while(m.find()){
                        FirebaseDatabase.getInstance().reference.child("Users").child(id).child("top_time_easy").setValue(string)
                    }
                }
                "Normal" -> {
                    val p = Pattern.compile("Normal")
                    val m = p.matcher(string)
                    while(m.find()){
                        FirebaseDatabase.getInstance().reference.child("Users").child(id).child("top_time_normal").setValue(string)
                    }
                }
                "Hard" -> {
                    val p = Pattern.compile("Hard")
                    val m = p.matcher(string)
                    while(m.find()){
                        FirebaseDatabase.getInstance().reference.child("Users").child(id).child("top_time_hard").setValue(string)
                    }
                }
            }

        }

    }
    private fun showListPublic(level: String){
        val Adapter = FlowersAdapter { flower -> adapterOnClick(flower) }
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        var intent: Intent = getIntent()
        var id: String = intent.getStringExtra("Id")
        var value : String =""
        var Tab:String=""
        var Items= java.util.ArrayList<String>()
        val arr = java.util.ArrayList<Item>()
        val arr1 = java.util.ArrayList<Item>()
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    when(level)
                    {
                        "Easy"-> value = "top_time_easy"
                        "Normal"-> value = "top_time_normal"
                        "Hard"-> value = "top_time_hard"
                    }
                    val top_time = ds.child(value).getValue(String::class.java)
                    if (top_time!=""){
                        Items.add(top_time.toString())
                    }


                }
                if(Items.size != 0 ){
                    val ar = java.util.ArrayList<Int>()
                    var s: List<String>
                    for (i in 0 until (Items.size)) {
                        if(level == "Easy" || level == "Hard"){
                            Tab = "         "
                        }
                        else if (level == "Normal"){
                            Tab = "   "
                        }
                        s = Items.get(i).toString().split(Tab," ")
                        val m = Integer.parseInt(s[1])
                        ar.add(m)
                    }
                    sortASC(ar)
                    for (i in 0 until (Items.size)) {
                        val p = Pattern.compile(ar[i].toString())
                        for (j in 0 until (Items.size)) {
                            val m = p.matcher(Items.get(j).toString())
                            while (m.find()) {
                                val user = Item("top "+(i+1).toString(),Items.get(j), 0.toString())
                                arr.add(user)
                                Adapter.notifyDataSetChanged()
                            }
                        }
                    }
                    for(i in 0 until arr.size)
                    {
                        if(i<3) {
                            var toptime = Item("top "+i.toString(), arr.get(i).Time, "")
                            arr1.add(toptime)
                        }
                    }
                    Adapter.submitList(arr1)
                    recyclerView.adapter = Adapter
                }
                else{recyclerView.adapter = Adapter}

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@RankingActivity,"Lấy data thất bại",
                    Toast.LENGTH_SHORT).show()
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)

    }

    private fun showDialog(string: String){
        val diffNumber: Array<Int> = arrayOf(32, 40, 48)
        val diffName: Array<String> = arrayOf("Easy", "Normal", "Hard")
        var diffChoice = 32
        var level : String =""
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
        dialog.setIcon(R.drawable.exit_game_icon)
        dialog.setTitle("Choose your game difficulty:")
        dialog.setSingleChoiceItems(diffName, 0) { _, i ->
            diffChoice = diffNumber[i]

        }
        dialog.setNegativeButton("Cancel") { dlg, _ ->
            dlg.dismiss()
        }
        dialog.setPositiveButton("OK") { _, _ ->
            run {
                if (diffChoice == 32) {
                    level = "Easy"
                } else if (diffChoice == 40) {
                    level = "Normal"
                } else if (diffChoice == 48) {
                    level = "Hard"
                }
                if(string == "1"){ showList(level)}
                else if(string == "2"){
                    showListPublic(level)}

            }
        }
        dialog.show()
    }
    private fun adapterOnClick(user: Item) {
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
        dialog.setIcon(R.drawable.exit_game_icon)
        dialog.setTitle("Choose your game difficulty:")
        dialog.setNegativeButton("Cancel") {
                dlg, _ -> dlg.dismiss()
        }
    }

    class FlowersAdapter(private val onClick: (Item) -> Unit) :
        ListAdapter<Item, FlowersAdapter.FlowerViewHolder>(FlowerDiffCallback) {

        /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
        class FlowerViewHolder(itemView: View, val onClick: (Item) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
            private val TV_time: TextView = itemView.findViewById(R.id.tv_time)
            private val TV_level: TextView = itemView.findViewById(R.id.tv_level)
            private var currentFlower: Item? = null

            init {
                itemView.setOnClickListener {
                    currentFlower?.let {
                        onClick(it)
                    }
                }
            }

            /* Bind flower name and image. */
            fun bind(user: Item) {
                currentFlower = user

                TV_level.text = user.Level
                TV_time.text = user.Time
                when (TV_level.text.toString()) {
                    "top 1"->{
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_trophy)
                    }
                    "Top 1" -> {
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_1st_place_medal)}
                    "Top 2" -> {
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_2nd_place_medal)}
                    "Top 3" -> {
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_3rd_place_medal)}
                    else -> {}
                }
            }
        }

        /* Creates and inflates view and return FlowerViewHolder. */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_item, parent, false)
            return FlowerViewHolder(view, onClick)
        }

        /* Gets current flower and uses it to bind view. */
        override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
            val flower = getItem(position)
            holder.bind(flower)

        }
    }

    object FlowerDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.ID == newItem.ID
        }
    }
}