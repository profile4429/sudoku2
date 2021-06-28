package com.example.sudoku.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profie.*
import kotlinx.android.synthetic.main.activity_sign_up2.*
import org.json.JSONException
import java.util.ArrayList
import java.util.regex.Pattern


class ProfieActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient

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
                    intent.putExtra("Id",tvID.text)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0)
                }
                R.id.Statistics->{
                    val intent = Intent(this, StatisticsActivity::class.java)
                    intent.putExtra("Id",tvID.text)
                    startActivity(intent)
                    finish()
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
        //Google
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//
//        // Build a GoogleSignInClient with the options specified by gso.
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val task: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (task != null) {
            var pesonName: String? = task.displayName
            var personEmail: String? = task.email
            var personID: String = task.id.toString()
            val user = User(pesonName.toString(), personEmail.toString(),"",0,"","","","")
            databaseArtists.child(personID).setValue(user)
            tvName.setText(pesonName)
            tvEmail.setText(personEmail)
            tvID.setText(personID)

        }
        //Logout
        val btnLogout = findViewById<Button>(R.id.btnSignOut)
        btnLogout.setOnClickListener(View.OnClickListener {

            mGoogleSignInClient.signOut()
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
        var Email: String? = null
            Email= intent.getStringExtra("email")
        val rootRef = FirebaseDatabase.getInstance().reference
        val hotelRef = rootRef.child("Users")
        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val name = ds.child("name").getValue(String::class.java)
                    val email = ds.child("email").getValue(String::class.java)

                    if(email == Email){
                        Log.d("TAG", "$name")
                        tvName.setText(name)
                        tvID.setText(ds.key.toString())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        hotelRef.addListenerForSingleValueEvent(eventListener)
        tvEmail.setText(Email)
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
                //var imageview: String =`object`.getString("profile_pic")

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
                            if(m.find() == true)
                            {
                                temp++
                            }
                            else{
                                temp=temp
                            }

                        }
                        if(temp == 0) {
                            val user = User(name, email, "", 0, "","","","")
                            databaseArtists.child(gender).setValue(user)
                        }
                        else{
                            Toast.makeText(this@ProfieActivity,"Bạn đang đăng nhập bằng Facebook",
                                Toast.LENGTH_SHORT).show()
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
        dialog.setIcon(R.drawable.new_game_icon)
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
    private fun showHowToPlayDialog_1(context: Activity){
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        val inflater: LayoutInflater = context.getLayoutInflater()
        val view: View = inflater.inflate(R.layout.layout_sudoku_title_2, null)
        dialog.setCustomTitle(view)
        dialog.setNegativeButton("Cancel") {
                dlg, _ ->  dlg.cancel()
        }
        dialog.show()
    }
    private fun showHowToPlayDialog(context: Activity){
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        val inflater: LayoutInflater = context.getLayoutInflater()
        val view: View = inflater.inflate(R.layout.layout_sudoku_title, null)
        dialog.setCustomTitle(view)
        dialog.setMessage("Câu đố Sudoku bắt đầu với một bảng kẻ ô, trong đó một số ô đã được điền số, tùy thuộc vào độ khó của trò chơi. Một câu đố được hoàn thành là câu đố trong đó mỗi số từ 1 đến 9 chỉ xuất hiện một lần trên mỗi một trong 9 hàng, cột và khối. Phân tích bảng để tìm ra số có thể phù hợp cho từng ô")
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
