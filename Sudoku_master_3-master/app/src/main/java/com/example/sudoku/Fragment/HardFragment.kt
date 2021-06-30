package com.example.sudoku.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sudoku.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_easy.*
import kotlinx.android.synthetic.main.fragment_hard.*
import java.util.regex.Pattern

class HardFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = arguments?.let {
            it.getString("id") ?: it.getString("iD")
        } ?: return
        GameStarted(id, "Hard")
        GameWon(id, "Hard")
        Winrate()
        BestTime(id)
        BestWinStreak(id)


    }

    private fun GameStarted(id: String, level: String) {
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
                gamestarted2.setText(ListResult.size.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }

    private fun GameWon(id: String, level: String) {
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
                gamewon2.setText(ListResult.size.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }

    private fun Winrate() {


    }

    private fun BestTime(id: String) {
        val rootRef = FirebaseDatabase.getInstance().reference
        val hotelRef = rootRef.child("Users")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val toptimeeasy = ds.child("top_time_hard").getValue(String::class.java)
                    val ID = ds.key.toString()
                    if (ID == id) {
                        besttime2.setText(
                            (toptimeeasy.toString().split("         ")[1]).split(" ")[0] + "s")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)

    }

    private fun BestWinStreak(id: String) {
        var ListResult = ArrayList<String>()
        val database = FirebaseDatabase.getInstance().reference
        val hotelRef = database.child("Users").child(id).child("result")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    var result = ds.getValue().toString()
                    ListResult.add(result)
                }
                val x: String = "Hard win"
                var b: Int = ListResult.size
                var a: Int = 0
                var max: Int = 0
                for (i in 0 until b) {
                    if (ListResult[i].toString().equals(x))
                        a++
                    else {
                        if (a > max)
                            max = a
                        a = 0
                    }
                }
                BestStreak2.setText(max.toString())
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hard, container, false)
    }

    companion object {
        fun newInstance(id: String?, iD: String?) = HardFragment().apply {
            arguments = Bundle().apply {
                putString("id", id)
                putString("iD", iD)
            }
        }
    }
}