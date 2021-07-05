package com.example.sudoku.view

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_profie.*


class HistoryActivity : AppCompatActivity() {

    private lateinit var listview: ListView
    private var mainAdapter: ArrayAdapter<String>? = null
    private lateinit var items: ArrayList<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        var intent : Intent = getIntent()
        var id :String =""
        if(intent.getStringExtra("id") == null){
            id = intent.getStringExtra("iD")
        }
        else{
            id = intent.getStringExtra("id")
        }

        listview = findViewById(R.id.listView)
        getDataHistory(id)
    }
    private  fun getDataHistory(id : String){

        items = ArrayList<String>()

        val rootRef = FirebaseDatabase.getInstance().reference
        val hotelRef = rootRef.child("Users").child(id).child("level_Time")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    items.add(ds.getValue().toString())
                    mainAdapter!!.notifyDataSetChanged()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HistoryActivity,"Lấy data thất bại",
                    Toast.LENGTH_SHORT).show()
            }
        }
        hotelRef.addValueEventListener(eventListener)
        mainAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.select_dialog_item, items!!
        )
        listview.setAdapter(mainAdapter)
    }
}

