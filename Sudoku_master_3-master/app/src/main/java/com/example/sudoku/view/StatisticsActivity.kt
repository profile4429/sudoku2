package com.example.sudoku.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.sudoku.Fragment.Adapter.VpAdapter
import com.example.sudoku.Fragment.EasyFragment
import com.example.sudoku.Fragment.HardFragment
import com.example.sudoku.Fragment.NormalFragment
import com.example.sudoku.R
import com.example.sudoku.login.Item
import com.example.sudoku.login.ProfieActivity
import com.example.sudoku.viewmodel.SudokuViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profie.*
import kotlinx.android.synthetic.main.activity_statistics.*
import kotlinx.android.synthetic.main.fragment_easy.*
import java.util.LinkedHashSet
import java.util.regex.Pattern

class StatisticsActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewEasy : EasyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        setupTabs()

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setSelectedItemId(R.id.Statistics)
        var intent: Intent = getIntent()
        var id: String
        if(intent.getStringExtra("id2")==null)
        {
            id=intent.getStringExtra("id1")
        }
        else{
            id= intent.getStringExtra("id2")
        }
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, ProfieActivity::class.java)
                    intent.putExtra("id3",id)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                R.id.ranking -> {
                    val intent = Intent(this, RankingActivity::class.java)
                    intent.putExtra("id3",id)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                R.id.Statistics->{

                }
            }
            false
        }
//        viewEasy = ViewModelProviders.of(this).get(EasyFragment::class.java)
//        viewEasy.Result(id,"Easy")
        Result(id,"Easy")
    }
    private fun setupTabs(){

        val adapter = VpAdapter(supportFragmentManager)
        adapter.addFragment(EasyFragment(),"Easy")
        adapter.addFragment(NormalFragment(),"Normal")
        adapter.addFragment(HardFragment(),"Hard")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.back_icon)
        tabs.getTabAt(1)!!.setIcon(R.drawable.back_icon)
        tabs.getTabAt(2)!!.setIcon(R.drawable.clear_icon)

    }
    private fun Result(id: String,level : String){
        var ListResult = ArrayList<String>()
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users").child(id).child("result")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    var result = ds.getValue().toString()
                    //Lọc ra danh sách theo từng cấp độ chơi vào mảng items
                    val f = Pattern.compile(level)//chuỗi cần tìm
                    val m = f.matcher(result)//chuỗi cho trước
                    while (m.find()) {
                        ListResult.add(result)
                    }
                }
                Log.d("result",ListResult.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }


}