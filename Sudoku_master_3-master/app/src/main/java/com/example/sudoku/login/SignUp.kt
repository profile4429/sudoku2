package com.example.sudoku.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sudoku.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up2.*

class SignUp : AppCompatActivity() {
    private val database = Firebase.database.reference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)
        auth = Firebase.auth


        btnLogin2.setOnClickListener {
            SignUp()

        }
        btnBack.setOnClickListener {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }



    private fun SignUp(){

        val email : String = editEmail2.text.toString();
        val password : String = editPassword2.text.toString();
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result?.user?.uid != null) {
                    Toast.makeText(this, "Đăng Kí Thành Công", Toast.LENGTH_SHORT).show()
                    val name = editName.text.toString()
                    val user = User(name,email,"",0)
                    database.child("Users").child(task.result!!.user!!.uid).setValue(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("SignUp", task.exception?.message ?: "")
                    Toast.makeText(this, "Đăng Kí Thất Bại", Toast.LENGTH_SHORT).show()
                }
            }

    }
}
