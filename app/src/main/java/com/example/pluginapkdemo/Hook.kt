package com.example.pluginapkdemo

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import java.lang.reflect.Proxy

/**
 * 浙江集商优选电子商务有限公司
 * @author zenglw
 * @date   12/25/20 4:52 PM
 */
class Hook {

    companion object {
        @SuppressLint("PrivateApi")
        fun hook(context: Context) {
            val proxyClass = Class.forName("android.app.IActivityTaskManager")
            val singleton = Class.forName("android.app.ActivityTaskManager")
                .getDeclaredField("IActivityTaskManagerSingleton")
                .apply { this.isAccessible = true }
                .get(null)
            val mInstanceField = singleton?.findField("mInstance")
            val mInstance = mInstanceField?.get(singleton)

            val proxy = Proxy.newProxyInstance(context.classLoader, arrayOf(proxyClass))
            { _, method, args ->
                Log.e("Hook", "hook ${method.name}")
                if (method.name == "startActivity") {
                    var intent: Intent? = null
                    for (item in args) {
                        if (item is Intent) {
                            intent = item
                            break
                        }
                    }
                    intent?.component = ComponentName(
                        "com.example.pluginapkdemo",
                        "com.example.pluginapkdemo.ProxyActivity"
                    )
                }
                if (args != null && args.isNotEmpty()) {
                    method.invoke(mInstance, *args)
                } else {
                    method.invoke(mInstance)
                }
            }
            mInstanceField?.set(singleton, proxy)
        }


        private val TARGET_INTENT = "target_intent"
        fun hookHandler() {


            try {
                val clazz = Class.forName("android.app.ActivityThread")
                val sCurrentActivityThreadField = clazz.getDeclaredField("sCurrentActivityThread")
                sCurrentActivityThreadField.isAccessible = true
                val activityThread = sCurrentActivityThreadField[null]
                val mHField = clazz.getDeclaredField("mH")
                mHField.isAccessible = true
                val mH = mHField[activityThread]

                // new 一个 Callback 替换系统的 mCallback对象
                val handlerClass = Class.forName("android.os.Handler")
                val mCallbackField = handlerClass.getDeclaredField("mCallback")
                mCallbackField.isAccessible = true
                mCallbackField[mH] = Handler.Callback { msg -> // 将Intent换回来
                    Log.e("Hook", "hookHandler ${msg.obj} ${msg.what}")
                    when (msg.what) {
                        100 -> try {
                            // 获取ActivityClientRecord中的intent对象
                            val intentField = msg.obj.javaClass.getDeclaredField("intent")
                            Log.e("leo", "handleMessage: 1111111111111")
                            intentField.isAccessible = true
                            val proxyIntent = intentField[msg.obj] as Intent

                            // 拿到插件的Intent
                            val intent = proxyIntent.getParcelableExtra<Intent>(TARGET_INTENT)
                            Log.e("leo", "handleMessage: $intent")

                            // 替换回来
                            proxyIntent.component = intent.component
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        159 -> runCatching {
                            ReflectUtils.reflect(msg.obj)
                                .field("mActivityCallbacks")
                                .get<List<Any>>()
                                .forEach {
                                    if (it.javaClass.name == "android.app.servertransaction.LaunchActivityItem") {
//                                       it.javaClass.getDeclaredField("mIntent")
                                        ReflectUtils.reflect(it).field("mIntent").get<Intent>()?.let { targetIntent->
                                            targetIntent.setComponent(ComponentName("com.example.plugin", "com.example.plugin.MainActivity"))
                                        }
                                        Log.e("Hook","hookHandler 修改intent成功")

                                    }
                                }
                        }
                    }
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}