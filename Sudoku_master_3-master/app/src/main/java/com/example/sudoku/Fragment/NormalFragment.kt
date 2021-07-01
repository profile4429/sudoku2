package com.example.sudoku.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sudoku.R
import com.example.sudoku.viewmodel.ShareViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_normal.*
import java.util.regex.Pattern

class NormalFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_normal, container, false)
        val currentstreak = view.findViewById<TextView>(R.id.CurentStreakNormal)
        val beststreak = view.findViewById<TextView>(R.id.BestStreakNormal)
        val avrtime = view.findViewById<TextView>(R.id.AvrTimeNormal)
        val besttime = view.findViewById<TextView>(R.id.BestTimeNormal)
        val gamestarted = view.findViewById<TextView>(R.id.GameStartedNormal)
        val gamewon = view.findViewById<TextView>(R.id.GameWonNormal)
        val winrate = view.findViewById<TextView>(R.id.WinRateNormal)


        activity?.let {
            val sharedViewModel = ViewModelProviders.of(it).get(ShareViewModel::class.java)
            sharedViewModel.inputString.observe(this, Observer {
                Result(it,"Normal",beststreak,currentstreak,gamestarted,gamewon,winrate)
                BestTime(it,besttime)
                AverageTime(it,avrtime)
            }) }
        return view
    }
    private fun Result(id: String, level : String, tv1: TextView, tv2 : TextView,tv3 :TextView,tv4 :TextView,tv5:TextView){
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
                tv3.setText(ListResult.size.toString())
                TheBestCurrent(ListResult,tv1)
                CurrentWin(ListResult,tv2)
                GameWon(id,level,tv4,tv5,ListResult.size)
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }
    //Game da thang
    private fun GameWon(id: String, level: String, tv1: TextView,tv2: TextView,i:Int) {
        var ListResult = ArrayList<String>()
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users").child(id).child("level_Time")
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
                tv1.setText(ListResult.size.toString())
                if(i!=0){
                    var winrate = (ListResult.size * 100.00 / i )
                    var a = Math.round(winrate*100.0) / 100.0
                    tv2.setText(a.toString() + "%")
                }else
                {tv2.setText("0%")}

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }
    private fun BestTime(id: String,textView: TextView) {
        val rootRef = FirebaseDatabase.getInstance().reference
        val hotelRef = rootRef.child("Users")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val toptimeeasy = ds.child("top_time_normal").getValue(String::class.java)
                    val ID = ds.key.toString()
                    if (ID == id) {
                        if(toptimeeasy != ""){
                        textView.setText(
                            (toptimeeasy.toString().split("     ")[1]).split(" ")[0] + "s"
                        )
                        }else{
                            textView.setText("0")
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)

    }
    fun TheBestCurrent(arr: java.util.ArrayList<String>, textView1: TextView) {
        val best = 0
        val ar = java.util.ArrayList<Int>()
        for (i in 0 until arr.size) {
            var a = 0
            if (arr[i] == "Normal win") {
                a= 1
            } else {
                a= 0
            }
            ar.add(a)
        }
        var sum = 1
        if(ar.size != 0 ){
            var max = ar[0]
            for (i in 0 until (ar.size - 1)) {
                if (ar[i] == ar[i + 1]) {
                    sum += ar[i]
                }else {
                    sum = 1
                }
                if (sum > max) {
                    max = sum
                }
                if (sum < 0) {
                    sum = 1
                }
            }
            Log.d("result",max.toString())
            textView1.setText(max.toString())
        }else{
            textView1.setText("0")
        }
    }
    fun CurrentWin(arr: java.util.ArrayList<String>, textView : TextView) {
        var a: Int
        val ar = java.util.ArrayList<Int>()
        for (i in 0 until arr.size) {
            if (arr[i] == "Normal win") {
                a = 1
            } else {
                a = 0
            }
            ar.add(a)
        }
        var dem = 0
        for (i in (ar.size-1) downTo 0) {
            if (ar[i] == 1) {
                dem++
            } else {
                break
            }
        }
        textView.setText(dem.toString())
    }
    private fun AverageTime(id: String,textView: TextView)
    {
        var items = ArrayList<String>()
        var I = ArrayList<String>()
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users").child(id).child("level_Time")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    var level_time = ds.getValue().toString()
                    //Lọc ra danh sách theo từng cấp độ chơi vào mảng items
                    val f = Pattern.compile("Normal")//chuỗi cần tìm
                    val m = f.matcher(level_time)//chuỗi cho trước
                    while (m.find()) {
                        items.add(level_time)
                    }
                }
                if(items.size != 0 ){
                    val ar = java.util.ArrayList<Int>()
                    var s: List<String>
                    for (i in 0 until (items.size)) {
                        s = items.get(i).toString().split("     "," ")
                        val m = Integer.parseInt(s[1])
                        ar.add(m)
                    }
                    Average(ar,textView)
                }
                else{
                    textView.setText("0")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }
    fun Average(arr: java.util.ArrayList<Int>,textView: TextView){
        var avr =0
        var sum =0
        for(i in 0 until arr.size){
            sum= sum + arr[i]
        }
        avr=sum/arr.size
        textView.setText(avr.toString()+"s")
    }
}