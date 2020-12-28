package com.example.pluginapkdemo

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import java.lang.reflect.Proxy

class MainActivity : AppCompatActivity() {
    private lateinit var mPerson: Person
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        Hook.hook(this)
        Hook.hookHandler()
        setContentView(R.layout.activity_main)
        mPerson = Person("nihao")


        findViewById<Button>(R.id.loadPlugin).setOnClickListener {
//            Class.forName("com.example.plugin.PluginTest").newInstance().let { it.findMethod("test").invoke(it)}
            // 启动插件activity
//            val field = resources.assets::class.java.declaredFields
//            val get = ReflectUtils.reflect(AssetManager::class.java).method("addAssetPath", "/sdcard/plugin-debug.apk").get<Int>()

//            resources.getString(R.string.zenglw)


//            Log.e("MainActivity","onCreate ${field.size} ${resources.getString(R.string.zenglw)}")
//            startActivity(Intent().setComponent(ComponentName("com.imaginer.yunji", "com.imaginer.yunji.activity.welcome.ACT_SplashScreen")))
            startActivity(Intent().setComponent(ComponentName("com.example.plugin", "com.example.plugin.MainActivity")))
        }

    }
}

data class Person(var name:String ) {
//    fun setName(name: String) {
//        Log.e("Person","setName $name")
//    }
}