package com.example.sudoku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sudoku.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_repass.*


class Repass : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repass)
        auth = Firebase.auth
        btnReset.setOnClickListener {
            ResetPassword();
        }
        btnBack3.setOnClickListener {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    fun ResetPassword(){
        var email : String = editEmail3.text.toString();
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
                task ->
            if (task.isSuccessful) {
               Toast.makeText(this,"Khôi Phục Mật Khẩu Thành Công! Vui Lòng Kiểm Tra Email",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Vui Lòng Kiểm Tra Lại Email!",Toast.LENGTH_SHORT).show()
            }
        }

    }
}