package com.bringmetheapp.worldmonuments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {

    private lateinit var nickname : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        nickname = sharedPreferences.getString("nickname", "").toString()

        //sharedPref = this.getSharedPreferences("com.bringmetheapp",Context.MODE_PRIVATE)
        //nickname = resources.getString(R.string.nickname)

        Log.d("nick", nickname)


        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.splash)
        val logo = logo()
        logo.start()
    }

    private inner class logo : Thread() {
        override fun run() {
            try {
                sleep(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if(nickname != ""){
                Log.d("nick", "ciao")
                val intent = Intent(this@Splash, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Log.d("nick", "ciao2")
                val intent = Intent(this@Splash, LoginActivity::class.java)
                startActivity(intent)
            }

            finish()
        }
    }
}