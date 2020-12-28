package com.example.pluginapkdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class ProxyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("ProxyActivity","onCreate 我是代理Activity")
        setContentView(TextView(this).apply {
            text = "我是代理Activity"
        })
    }
}