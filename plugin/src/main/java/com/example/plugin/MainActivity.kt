package com.example.plugin

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView

class MainActivity :BaseActivity2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        Log.e("MainActivity","i11111111 onCreate 我是插件Activity")

//        findViewById<SeekBar>(R.id.seekbar).setOnSeekBarChangeListener(object :
//            SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                findViewById<TextView>(R.id.tv).text = "progress = $progress"
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar) {
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar) {
//            }
//
//        })
        val view = LayoutInflater.from(mContext2).inflate(R.layout.activity_main2, null)
        setContentView(view)

//        setContentView(R.layout.activity_main2)

//        setContentView(TextView(this).apply {
//            text  = "我是插件Activity"
//        })
    }
}