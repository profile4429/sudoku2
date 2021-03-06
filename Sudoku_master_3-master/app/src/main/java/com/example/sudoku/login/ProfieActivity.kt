package com.example.sudoku.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.R
import com.example.sudoku.view.HistoryActivity
import com.example.sudoku.view.MainActivity
import com.example.sudoku.view.RankingActivity
import com.example.sudoku.view.StatisticsActivity
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profie.*
import kotlinx.android.synthetic.main.activity_sign_up2.*
import org.json.JSONException
import java.util.*
import java.util.regex.Pattern


class ProfieActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profie)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setSelectedItemId(R.id.home)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {}
                R.id.ranking -> {
                    val intent = Intent(this, RankingActivity::class.java)
                    intent.putExtra("id1",tvID.text)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
                R.id.Statistics->{
                    val intent = Intent(this, StatisticsActivity::class.java)
                    intent.putExtra("id1",tvID.text)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }

            }
            false
        }

        val databaseArtists: DatabaseReference
        databaseArtists = FirebaseDatabase.getInstance().getReference("Users")
        val id: String? = databaseArtists.push().getKey()
        //Facebook
        result()
        //Logout
        val btnLogout = findViewById<Button>(R.id.btnSignOut)
        btnLogout.setOnClickListener(View.OnClickListener {
            // Logout
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/permissions/",
                    null,
                    HttpMethod.DELETE,
                    GraphRequest.Callback {
                        AccessToken.setCurrentAccessToken(null)
                        LoginManager.getInstance().logOut()
                        finish()
                    }
                ).executeAsync()
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })
        //Button play game
        btnPlayGame.setOnClickListener {
            PlayGameDialog()
        }
        //button cach choi
        btnHowToPlay.setOnClickListener { showHowToPlayDialog(this) }
        //button Lich su choi
        btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("id",tvID.text.toString())
            startActivity(intent)
        }
        //Xuat thong tin nguoi dung
        getProfie()
    }

    private fun getProfie(){
        var intent : Intent = getIntent()
        var Email: String = ""
        var id: String=""
        if(intent.getStringExtra("id2")==null)
        {
            if(intent.getStringExtra("id3") != null){
            id=intent.getStringExtra("id3")}
            else{
                id=" "
                if(intent.getStringExtra("email")==null){
                    Email = tvEmail.text.toString()
                }else {
                    Email = intent.getStringExtra("email")
                }
            }
        }
        else {
            id= intent.getStringExtra("id2")
        }
        val rootRef = FirebaseDatabase.getInstance().reference
        val hotelRef = rootRef.child("Users")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val name = ds.child("name").getValue(String::class.java)
                    val email = ds.child("email").getValue(String::class.java)
                    val ID = ds.key.toString()
                    if(email == Email || ID == id){
                        Log.d("TAG", "$name")
                        tvName.setText(name)
                        tvID.setText(ds.key.toString())
                        tvEmail.setText(email)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
    }
    private fun result() {
        val databaseArtists: DatabaseReference
        var arr = ArrayList<String>()
        var temp :Int=0
        databaseArtists = FirebaseDatabase.getInstance().getReference("Users")
        val id: String? = databaseArtists.push().getKey()
        val request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken()
        ) { `object`, response ->
            try {
                var email: String = `object`.getString("email")
                var name: String = `object`.getString("name")
                var gender: String = `object`.getString("id")

                tvName.setText(name)
                tvEmail.setText(email)
                tvID.setText(gender)
                //imageView.setImageURI(imageview)
                val rootRef = FirebaseDatabase.getInstance().reference
                val hotelRef = rootRef.child("Users")
                val eventListener: ValueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            val value = ds.key.toString()
                            arr.add(value)
                        }
                        for (i in 0 until (arr.size)) {
                            val p = Pattern.compile(gender)
                            val m = p.matcher(arr[i])
                            if(m.find() == true) {
                                temp++
                            }
                            else{
                                temp=temp
                            }
                        }
                        if(temp == 0) {
                            val user = User(name, email, "", 0,"", "","","","")
                            databaseArtists.child(gender).setValue(user)
                        }
                        else{
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                }
                hotelRef.addListenerForSingleValueEvent(eventListener)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        var bundle = Bundle()
        bundle.putString("fields", "email,name,id");
        request.setParameters(bundle)
        request.executeAsync()
    }

    private fun PlayGameDialog() {
        val diffNumber: Array<Int> = arrayOf(32, 40, 48)
        val diffName: Array<String> = arrayOf("Easy", "Normal", "Hard")
        var diffChoice = 32
        var level :String=""

        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        dialog.setIcon(R.drawable.ic_level)
        dialog.setTitle("Choose your game difficulty:")
        dialog.setSingleChoiceItems(diffName, 0) {
                _, i -> diffChoice = diffNumber[i]

        }
        dialog.setPositiveButton("OK") {
                _, _ ->
            run {
                if(diffChoice == 32){
                    level = "Easy"
                }
                else if(diffChoice == 40){
                    level = "Normal"
                }
                else if(diffChoice == 48){
                    level = "Hard"
                }
                Firebase.database.reference.child("Users").child(tvID.text.toString()).child("level").push().setValue(level)
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra("Key_1", diffChoice)
                intent.putExtra("Level",level)
                intent.putExtra("ID",tvID.text.toString())
                startActivity(intent)
            }
        }
        dialog.setNegativeButton("Cancel") {
                dlg, _ ->  dlg.cancel()
        }

        dialog.show()

    }
    private fun showHowToPlayDialog_3(context: Activity){
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        val inflater: LayoutInflater = context.getLayoutInflater()
        val view: View = inflater.inflate(R.layout.layout_sudoku_title_4, null)
        dialog.setCustomTitle(view)
        dialog.setMessage("Khi b???n ho??n th??nh c??u ?????, tr?? ch??i s??? th??ng b??o k???t qu???.")
        dialog.setNegativeButton("T??i ???? hi???u") {
                dlg, _ ->  dlg.cancel()
        }
        dialog.show()
    }
    private fun showHowToPlayDialog_2(context: Activity){
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        val inflater: LayoutInflater = context.getLayoutInflater()
        val view: View = inflater.inflate(R.layout.layout_sudoku_title_3, null)
        dialog.setCustomTitle(view)
        dialog.setPositiveButton("Next") {
                _, _ ->
            run {

                showHowToPlayDialog_3(this@ProfieActivity)
            }
        }
        dialog.show()
    }
    private fun showHowToPlayDialog_1(context: Activity){
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        val inflater: LayoutInflater = context.getLayoutInflater()
        val view: View = inflater.inflate(R.layout.layout_sudoku_title_2, null)
        dialog.setCustomTitle(view)
        dialog.setPositiveButton("Next") {
                _, _ ->
            run {

                showHowToPlayDialog_2(this@ProfieActivity)
            }
        }
        dialog.show()
    }
    private fun showHowToPlayDialog(context: Activity){
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        val inflater: LayoutInflater = context.getLayoutInflater()
        val view: View = inflater.inflate(R.layout.layout_sudoku_title, null)
        dialog.setCustomTitle(view)
        dialog.setMessage("C??u ????? Sudoku b???t ?????u v???i m???t b???ng k??? ??, trong ???? m???t s??? ?? ???? ???????c ??i???n s???, " +
                "t??y thu???c v??o ????? kh?? c???a tr?? ch??i. " +
                "M???t c??u ????? ???????c ho??n th??nh l?? c??u ????? trong ???? m???i s??? t??? 1 ?????n 9 ch??? xu???t hi???n m???t l???n tr??n m???i m???t trong 9 h??ng, c???t v?? kh???i. " +
                "Ph??n t??ch b???ng ????? t??m ra s??? c?? th??? ph?? h???p cho t???ng ??")
        dialog.setPositiveButton("Next") {
                _, _ ->
            run {

                showHowToPlayDialog_1(this@ProfieActivity)
            }
        }
        dialog.setNegativeButton("Cancel") {
                dlg, _ ->  dlg.cancel()
        }

        dialog.show()
    }
}
