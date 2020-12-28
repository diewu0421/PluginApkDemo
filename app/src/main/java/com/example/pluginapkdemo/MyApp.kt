package com.example.pluginapkdemo

import android.app.Application
import android.content.res.AssetManager
import android.content.res.Resources

/**
 * 浙江集商优选电子商务有限公司
 * @author zenglw
 * @date   12/25/20 4:51 PM
 */
class MyApp : Application(){
    private var mResource: Resources? = null

    override fun onCreate() {
        super.onCreate()
        // 加载插件apk
        LoadUtil.load(this)
        // hook startActivity
//        mResource = LoadUtil.loadResource(this)

    }

    override fun getResources(): Resources {
        return if (mResource != null ) mResource!! else super.getResources()

    }


}