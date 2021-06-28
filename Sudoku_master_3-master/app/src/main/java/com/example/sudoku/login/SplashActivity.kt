package com.example.sudoku.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sudoku.R
import android.os.Handler
import android.content.Intent

class SplashActivity : AppCompatActivity() {

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler = Handler()
        handler.postDelayed(Runnable {
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
        }, 3000)
    }
}