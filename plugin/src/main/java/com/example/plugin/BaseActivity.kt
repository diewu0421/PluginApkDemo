package com.example.plugin

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper

/**
 * 浙江集商优选电子商务有限公司
 * @author zenglw
 * @date   12/27/20 8:44 PM
 */

fun loadResource(context: Context): Resources {
    val newAssetManager = AssetManager::class.java.newInstance()
    AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java).run {
        isAccessible = true
        invoke(newAssetManager, "${context.externalCacheDir}/plugin-debug.apk")
    }
    return Resources(newAssetManager, context.resources.displayMetrics, context.resources.configuration)
}
/**
 * 浙江集商优选电子商务有限公司
 * @author zenglw
 * @date   12/27/20 8:44 PM
 */
open class BaseActivity2  : AppCompatActivity(){

    protected val mContext2: Context by lazy {

        val resource =  loadResource(application)
        ContextThemeWrapper(baseContext, 0).apply {
            javaClass.getDeclaredField("mResources").let {
                it.isAccessible  = true
                it.set(this, resource)
            }
        }

    }
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.e("BaseActivity2","onCreate ")

    }

//    override fun getResources(): Resources {
//        if (application != null && application.resources != null) {
//            return application.resources
//
//        }
//        return super.getResources()
//    }

}