package com.example.sudoku.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sudoku.Fragment.Adapter.VpAdapter
import com.example.sudoku.Fragment.EasyFragment
import com.example.sudoku.Fragment.HardFragment
import com.example.sudoku.Fragment.NormalFragment
import com.example.sudoku.R
import com.example.sudoku.login.ProfieActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_profie.*
import kotlinx.android.synthetic.main.activity_statistics.*

class StatisticsActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        setupTabs()

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setSelectedItemId(R.id.Statistics)
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
                    val intent = Intent(this, RankingActivity::class.java)
                    intent.putExtra("Id",id)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                R.id.Statistics->{

                }
            }
            false
        }
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

}