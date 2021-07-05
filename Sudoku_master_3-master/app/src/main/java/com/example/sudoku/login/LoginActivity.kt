package com.example.sudoku.login

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.R
import com.example.sudoku.Repass
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity() {
    private lateinit var callbackManager: CallbackManager

    lateinit var auth: FirebaseAuth
    lateinit var mshare : SharedPreferences
    var isRemembered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Set email và matkhau
        mshare = getSharedPreferences("dataLogin", MODE_PRIVATE)
        editEmail.setText(mshare.getString("taikhoan",""))
        editPassword.setText(mshare.getString("matkhau",""))
        isRemembered = mshare.getBoolean("checked",false)

        //Check box hiện và ẩn password
        var etPassword = findViewById<View>(R.id.editPassword) as EditText
        etPassword.setTransformationMethod(PasswordTransformationMethod()) // Hide password initially

        var checkBoxShowPwd = findViewById<View>(R.id.checkBox) as CheckBox
        checkBoxShowPwd.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { arg0, isChecked ->
            if (isChecked) {
                etPassword.setTransformationMethod(null) // Show password when box checked
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod()) // Hide password when box not checked
            }
        })

        //Login Firebase
        auth = Firebase.auth
        btnSignUp.setOnClickListener {
            val intent:Intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        textView.setOnClickListener{
            val intent:Intent = Intent(this, Repass::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
           SignIn();
        }

        //Login Facebook
        var loginButton = findViewById<View>(R.id.login_button) as LoginButton
        facebookView.setOnClickListener { loginButton.performClick() }
        loginButton.setOnClickListener {
            callbackManager = CallbackManager.Factory.create()
            loginButton.setPermissions("public_profile", "user_gender", "email")
            // Callback registration
            loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // App code
                Log.d("Login Facebook", "Đăng nhập thành công !")
                startActivity(Intent(this@LoginActivity, ProfieActivity::class.java))
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
            })
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (::callbackManager.isInitialized){
            callbackManager.onActivityResult(requestCode, resultCode, data)}
        super.onActivityResult(requestCode, resultCode, data)

    }
    private fun SignIn(){
        var email : String = editEmail.text.toString();
        var password : String = editPassword.text.toString();
        val check : Boolean = checkB.isChecked()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if(checkB.isChecked()){
                        val edittor : SharedPreferences.Editor = mshare.edit()
                        edittor.putString("taikhoan",email)
                        edittor.putString("matkhau",password)
                        edittor.putBoolean("checked",check)
                        edittor.apply()
                    }else{
                        val edittor : SharedPreferences.Editor = mshare.edit()
                        edittor.remove("taikhoan")
                        edittor.remove("matkhau")
                        edittor.remove("checked")
                        edittor.apply()
                    }
                    task.result?.user?.uid?.let {
                        Firebase.database.reference.child("Users").child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                USER_ID = it
                                val intent = Intent(this@LoginActivity, ProfieActivity::class.java)
                                intent.putExtra("email",email)
                                startActivity(intent)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@LoginActivity,"Đăng Nhập Thất Bại! Vui Lòng Kiểm Tra Lại",Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {
                     Toast.makeText(this,"Đăng Nhập Thất Bại! Vui Lòng Kiểm Tra Lại",Toast.LENGTH_SHORT).show()
                }
            }
    }


    companion object {
        var USER_ID: String? = null
    }
}