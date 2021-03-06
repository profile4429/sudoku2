package com.example.sudoku.view

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sudoku.Fragment.Adapter.RankAdapter
import com.example.sudoku.R
import com.example.sudoku.login.Item
import com.example.sudoku.login.ProfieActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profie.*
import kotlinx.android.synthetic.main.activity_ranking.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class RankingActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var items: ArrayList<String>

    val list = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)


        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setSelectedItemId(R.id.ranking)
        var intent: Intent = getIntent()
        var id: String
        if (intent.getStringExtra("id1") == null) {
            if (intent.getStringExtra("id3") != null) {
                id = intent.getStringExtra("id3")
            } else {
                id = " "
            }
        } else {
            id = intent.getStringExtra("id1")
        }
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, ProfieActivity::class.java)
                    intent.putExtra("id2", id)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                R.id.ranking -> {
                }
                R.id.Statistics -> {
                    val intent = Intent(this, StatisticsActivity::class.java)
                    intent.putExtra("id2", id)
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

    private fun makeBackground(view: View) {
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

    private fun setListeners() {
        val btnPerson = findViewById<Button>(R.id.btn_personal)
        val btnPublic = findViewById<Button>(R.id.btn_public)
        val clickableViews: List<View> = listOf(btnPerson, btnPublic)
        for (item in clickableViews) {
            item.setOnClickListener { makeBackground(it) }
        }
    }

    private fun showList(level: String) {
        val adapter = Adapter { flower -> adapterOnClick(flower) }
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val arr = java.util.ArrayList<Item>()
        var intent: Intent = getIntent()
        var id: String
        if (intent.getStringExtra("id1") == null) {
            if (intent.getStringExtra("id3") != null) {
                id = intent.getStringExtra("id3")
            } else {
                id = " "
            }
        } else {
            id = intent.getStringExtra("id1")
        }
        var Tab: String = ""
        items = ArrayList<String>()
        var I = ArrayList<String>()
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users").child(id).child("level_Time")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    var level_time = ds.getValue().toString()
                    //L???c ra danh s??ch theo t???ng c???p ????? ch??i v??o m???ng items
                    val f = Pattern.compile(level)//chu???i c???n t??m
                    val m = f.matcher(level_time)//chu???i cho tr?????c
                    while (m.find()) {
                        items.add(level_time)
                    }
                }
                if (items.size != 0) {
                    val ar = java.util.ArrayList<Int>()
                    var s: List<String>
                    if (level == "Easy" || level == "Hard") {
                        Tab = "         "
                    } else if (level == "Normal") {
                        Tab = "     "
                    }
                    for (i in 0 until (items.size)) {
                        s = items.get(i).toString().split(Tab, " ")
                        val m = Integer.parseInt(s[1])
                        ar.add(m)
                    }
                    val set: Set<Int> =
                        LinkedHashSet<Int>(ar)//Constructing LiskedHashSet de loai bo phan tu trung lap
                    // Constructing listWithoutDuplicateElements using set
                    val listWithoutDuplicateElements: java.util.ArrayList<Int> =
                        java.util.ArrayList(set)
                    sortASC(listWithoutDuplicateElements)
                    for (i in 0 until (listWithoutDuplicateElements.size)) {
                        val p =
                            Pattern.compile(level + Tab + listWithoutDuplicateElements[i].toString())
                        for (j in 0 until (items.size)) {
                            val m = p.matcher(items.get(j).toString())
                            while (m.find()) {
                                val user = Item("","",(i + 1).toString(), items.get(j), 0.toString())
                                arr.add(user)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                    adapter.submitList(arr)
                    recyclerView.adapter = adapter
                } else {
                    recyclerView.adapter = adapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@RankingActivity, "L???y data th???t b???i",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)

    }

    //s???p x???p m???ng c??c s??? theo th??? t??? t??ng d???n
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
    private fun AddDataTopTime(string: String, id: String) {
        val diffName: Array<String> = arrayOf("Easy", "Normal", "Hard")
        for (i in 0 until diffName.size) {
            when (diffName.get(i)) {
                "Easy" -> {
                    val p = Pattern.compile("Easy")
                    val m = p.matcher(string)
                    while (m.find()) {
                        FirebaseDatabase.getInstance().reference.child("Users").child(id)
                            .child("top_time_easy").setValue(string)
                    }
                }
                "Normal" -> {
                    val p = Pattern.compile("Normal")
                    val m = p.matcher(string)
                    while (m.find()) {
                        FirebaseDatabase.getInstance().reference.child("Users").child(id)
                            .child("top_time_normal").setValue(string)
                    }
                }
                "Hard" -> {
                    val p = Pattern.compile("Hard")
                    val m = p.matcher(string)
                    while (m.find()) {
                        FirebaseDatabase.getInstance().reference.child("Users").child(id)
                            .child("top_time_hard").setValue(string)
                    }
                }
            }

        }

    }

    private fun showListPublic(level: String) {
        var value: String = ""
        var Tab: String = ""
        var Items = java.util.ArrayList<String>()
        val arr = java.util.ArrayList<Item>()
        val arr1 = java.util.ArrayList<Item>()
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    when (level) {
                        "Easy" -> value = "top_time_easy"
                        "Normal" -> value = "top_time_normal"
                        "Hard" -> value = "top_time_hard"
                    }
                    val top_time = ds.child(value).getValue(String::class.java)
                    if (top_time != "") {
                        Items.add(top_time.toString())
                    }
                }

                if (Items.size != 0) {
                    val ar = java.util.ArrayList<Int>()
                    var s: List<String>
                    for (i in 0 until (Items.size)) {
                        if (level == "Easy" || level == "Hard") {
                            Tab = "         "
                        } else if (level == "Normal") {
                            Tab = "     "
                        }
                        s = Items.get(i).toString().split(Tab, " ")
                        val m = Integer.parseInt(s[1])
                        ar.add(m)
                    }
                    val set: Set<Int> = LinkedHashSet<Int>(ar)
                    val listWithoutDuplicateElements: java.util.ArrayList<Int> =
                        java.util.ArrayList(set)
                    sortASC(listWithoutDuplicateElements)
                    for (i in 0 until (Items.size)) {
                        val p = Pattern.compile(level + Tab + listWithoutDuplicateElements[i].toString())
                        for (j in 0 until (Items.size)) {
                            val m = p.matcher(Items.get(j).toString())
                            while (m.find()) {
                                val user = Item("",""," "+(i + 1).toString(), Items.get(j), 0.toString())
                                if (user != null) {
                                    arr.add(user)
                                }
                            }
                        }
                    }
                    for(i in 0 until arr.size)
                    {
                        if(i<3) {
                            setDataUser(level,arr.get(i).Time,i,arr1)
                        }
                    }


                } else {
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@RankingActivity, "L???y data th???t b???i",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }
    private fun setDataUser(level: String,time: String,i :Int,arr1 : ArrayList<Item>){
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        var Items = java.util.ArrayList<String>()
        var adapter = RankAdapter(arr1)
        var value =""
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    when (level) {
                        "Easy" -> value = "top_time_easy"
                        "Normal" -> value = "top_time_normal"
                        "Hard" -> value = "top_time_hard"
                    }
                    val top_time = ds.child(value).getValue(String::class.java)
                    val name = ds.child("name").getValue(String::class.java)
                    val email = ds.child("email").getValue(String::class.java)
                    if (top_time == time) {
                        var toptime = Item(name.toString(),email.toString()," "+(i + 1).toString(), time, "")
                        arr1.add(toptime)
                    }
                }
                adapter= RankAdapter(arr1)
                recyclerView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@RankingActivity, "L???y data th???t b???i",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }
    private fun showDialog(string: String) {
        val diffNumber: Array<Int> = arrayOf(32, 40, 48)
        val diffName: Array<String> = arrayOf("Easy", "Normal", "Hard")
        var diffChoice = 32
        var level: String = ""
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
        dialog.setIcon(R.drawable.ic_level)
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
                if (string == "1") {
                    showList(level)
                } else if (string == "2") {
                    UpdateData(level)
                    showListPublic(level)

                }

            }
        }
        dialog.show()
    }

    //C???p nh???t d??? li???u th???i gian nhanh nh???t theo t???ng m???c ????? ch??i
    private fun UpdateData(level: String) {
        val arr = java.util.ArrayList<Item>()
        var intent: Intent = getIntent()
        var id: String
        if (intent.getStringExtra("id1") == null) {
            if (intent.getStringExtra("id3") != null) {
                id = intent.getStringExtra("id3")
            } else {
                id = " "
            }
        } else {
            id = intent.getStringExtra("id1")
        }
        var Tab: String = ""
        items = ArrayList<String>()
        var I = ArrayList<String>()
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users").child(id).child("level_Time")
        val eventListener: ValueEventListener = object : ValueEventListener {
            @SuppressLint("ResourceAsColor")
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
                if (items.size != 0) {
                    val ar = java.util.ArrayList<Int>()
                    var s: List<String>
                    for (i in 0 until (items.size)) {
                        if (level == "Easy" || level == "Hard") {
                            Tab = "         "
                        } else if (level == "Normal") {
                            Tab = "     "
                        }
                        s = items.get(i).toString().split(Tab, " ")
                        val m = Integer.parseInt(s[1])
                        ar.add(m)
                    }
                    //tv_rank.setText(ar.toString())
                    sortASC(ar)
                    for (i in 0 until (items.size)) {
                        val p = Pattern.compile(ar[i].toString())
                        for (j in 0 until (items.size)) {
                            val m = p.matcher(items.get(j).toString())
                            while (m.find()) {
                                val user =
                                    Item("","","Top " + (i + 1).toString(), items.get(j), 0.toString())
                                arr.add(user)
                            }
                        }
                    }
                    AddDataTopTime(arr.get(0).Time, id)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@RankingActivity, "L???y data th???t b???i",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)

    }

    private fun adapterOnClick(user: Item) {}
    class Adapter(private val onClick: (Item) -> Unit) :
        ListAdapter<Item, Adapter.ViewHolder>(DiffCallback) {

        /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
        class ViewHolder(itemView: View, val onClick: (Item) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
            private val TV_time: TextView = itemView.findViewById(R.id.tv_time)
            private val TV_level: TextView = itemView.findViewById(R.id.tv_level)
            private var current: Item? = null

            init {
                itemView.setOnClickListener {
                    current?.let {
                        onClick(it)

                    }
                }
            }

            /* Bind flower name and image. */
            fun bind(user: Item) {
                current = user

                TV_level.text = user.Level
                TV_time.text = user.Time
                when (TV_level.text.toString()) {
                    " 1" -> {
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_first)
                    }
                    " 2" -> {
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_second)
                    }
                    " 3" -> {
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_third)
                    }
                    "1" -> {
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_1st_place_medal)
                    }
                    "2" -> {
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_2nd_place_medal)
                    }
                    "3" -> {
                        TV_level.setText("")
                        TV_level.setBackgroundResource(R.drawable.ic_3rd_place_medal)
                    }
                    else -> {
                    }
                }
            }
        }

        /* Creates and inflates view and return FlowerViewHolder. */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_item, parent, false)
            return ViewHolder(view, onClick)
        }

        /* Gets current flower and uses it to bind view. */
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val flower = getItem(position)
            holder.bind(flower)

        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.ID == newItem.ID
        }
    }
}