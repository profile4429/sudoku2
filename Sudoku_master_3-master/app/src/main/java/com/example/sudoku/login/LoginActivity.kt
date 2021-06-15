package com.example.sudoku.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sudoku.R
import com.example.sudoku.Repass
import com.example.sudoku.view.MainActivity
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.FacebookException
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
     lateinit var callbackManager: CallbackManager

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
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
        var loginButton = findViewById<View>(R.id.login_button) as LoginButton

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
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener { signIn() }
    }
    private fun SignIn(){
        var email : String = editEmail.text.toString();
        var password : String = editPassword.text.toString();
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result?.user?.uid?.let {
                        Firebase.database.reference.child("Users").child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                USER_ID = it
                                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
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
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completeTask: Task<GoogleSignInAccount>) {
        try {
            Toast.makeText(this, "Đăng nhập thành công ! ;)", Toast.LENGTH_LONG).show()
            val account = completeTask.getResult(ApiException::class.java)
            startActivity(Intent(this@LoginActivity, ProfieActivity::class.java))
        } catch (e: ApiException) {
            Toast.makeText(this, "Đăng nhập thất bại !", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        var USER_ID: String? = null
    }
}