package com.example.fyp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    lateinit var topAni : Animation
    lateinit var btmAni : Animation
    lateinit var handler : Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        topAni = AnimationUtils.loadAnimation(this,R.anim.top_animation)
        btmAni = AnimationUtils.loadAnimation(this,R.anim.bottom_animation)

        val img = findViewById<ImageView>(R.id.img)
        val title = findViewById<TextView>(R.id.title)

        img.animation = topAni
        title.animation = btmAni

        val background = object : Thread(){
            override fun run(){
                try{
                    sleep(3000)
                    val intent = Intent(baseContext, Home::class.java)
                    startActivity(intent)
                } catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }
}