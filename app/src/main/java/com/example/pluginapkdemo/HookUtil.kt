package com.example.pluginapkdemo

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import java.lang.reflect.Proxy

object HookUtil {
    private const val TARGET_INTENT = "target_intent"
    fun hookAMS() {
        try {

            // 获取Singleton对象
            val clazz = Class.forName("android.app.ActivityTaskManager")
            val singletonField = clazz.getDeclaredField("IActivityTaskManagerSingleton")
            singletonField.isAccessible = true
            val singleton = singletonField[null]

            // 获取IActivityManager 对象
            val singletonClass = Class.forName("android.util.Singleton")
            val mInstanceField = singletonClass.getDeclaredField("mInstance")
            mInstanceField.isAccessible = true
            val mInstance = mInstanceField[singleton]
            val iActivityManagerClass = Class.forName("android.app.IActivityTaskManager")
            val proxyInstance = Proxy.newProxyInstance(
                Thread.currentThread().contextClassLoader,
                arrayOf(iActivityManagerClass)
            ) { proxy, method, args -> // IActivityManager 的方法执行的时候，都会先跑这儿
                Log.e("HookUtil", "invoke: " + method.name)
                if ("startActivity" == method.name) {
                    // 替换Intent
                    var index = 0
                    for (i in args.indices) {
                        if (args[i] is Intent) {
                            index = i
                            break
                        }
                    }
                    // 启动插件的intent
                    val intent = args[index] as Intent
                    val proxyIntent = Intent()
                    proxyIntent.setClassName(
                        "com.enjoy.leo_plugin",
                        "com.enjoy.leo_plugin.ProxyActivity"
                    )

                    // 保存原来的
                    proxyIntent.putExtra(TARGET_INTENT, intent)
                    args[index] = proxyIntent
                }
                method.invoke(mInstance, *args)
            }

            // 替换系统的 IActivityManager对象
            mInstanceField[singleton] = proxyInstance
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
                }
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadResource(context: Context) {
        context.resources.getColor(R.color.black)

    }
}