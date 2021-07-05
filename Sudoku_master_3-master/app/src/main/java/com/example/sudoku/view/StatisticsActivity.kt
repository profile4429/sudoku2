package com.example.sudoku.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.sudoku.Fragment.Adapter.VpAdapter
import com.example.sudoku.Fragment.EasyFragment
import com.example.sudoku.Fragment.HardFragment
import com.example.sudoku.Fragment.NormalFragment
import com.example.sudoku.R
import com.example.sudoku.login.Item
import com.example.sudoku.login.ProfieActivity
import com.example.sudoku.viewmodel.ShareViewModel
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
    private lateinit var viewEasy : ShareViewModel

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
        viewEasy = ViewModelProviders.of(this).get(ShareViewModel::class.java)

        viewEasy.inputString.postValue(id)
    }
    private fun setupTabs(){

        val adapter = VpAdapter(supportFragmentManager)
        adapter.addFragment(EasyFragment(),"Easy")
        adapter.addFragment(NormalFragment(),"Normal")
        adapter.addFragment(HardFragment(),"Hard")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_star_1)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_star_2)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_three_stars)

    }



}