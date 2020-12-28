package com.example.pluginapkdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import dalvik.system.DexClassLoader
import java.lang.reflect.Array.newInstance
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

/**
 * 浙江集商优选电子商务有限公司
 * @author zenglw
 * @date   12/25/20 3:47 PM
 */
class LoadUtil {

    companion object {
        const val APK_PATH = "/sdcard/plugin-debug.apk"

        fun load(context: Context) {

            val dexClassLoader =
                DexClassLoader("${context.externalCacheDir?.absolutePath}/plugin-debug.apk", context.cacheDir.absolutePath, null, context.classLoader)
            val pathClassLoader = context.classLoader
            // 获取宿主的dexElements
            val hostDexPathListField = pathClassLoader.findField("pathList")
            val hostDexPathList = hostDexPathListField.get(pathClassLoader)
            val hostDexElementsField = hostDexPathList?.findField("dexElements")
            val hostDexElements = hostDexElementsField?.get(hostDexPathList) as Array<*>

            // 获取插件的dexElements
            val pluginDexPathListField = dexClassLoader.findField("pathList")
            val pluginDexPathList = pluginDexPathListField.get(dexClassLoader)
            val pluginDexElementsField = pluginDexPathList?.findField("dexElements")
            val pluginDexElements = pluginDexElementsField?.get(pluginDexPathList) as Array<*>

            // 创建一个新的数组，size为 两个dexElements的size的和
            val newDexElements = newInstance(
                hostDexElements::class.java.componentType!!,
                hostDexElements.size + pluginDexElements.size
            )

            System.arraycopy(hostDexElements, 0, newDexElements, 0, hostDexElements.size)

            System.arraycopy(
                pluginDexElements,
                0,
                newDexElements,
                hostDexElements.size,
                pluginDexElements.size
            )
            hostDexElementsField.set(hostDexPathList, newDexElements)
        }


    }

}

class ShareHelper {


    companion object {

        fun findMethod(obj: Any, methodName: String, vararg params: Class<*>): Method {
            var cls = obj.javaClass
            while (true) {
                runCatching {
                    return cls.getDeclaredMethod(methodName, *params).apply { isAccessible = true }
                }.onFailure {
                    cls = cls.superclass as Class<Any>
                }
            }

        }

        fun findField(obj: Any, fieldName: String): Field {
            var cls = obj.javaClass
            while (true) {
                runCatching {
                    return cls.getDeclaredField(fieldName).apply { isAccessible = true }
                }.onFailure {
                    cls = cls.superclass as Class<Any>
                }
            }
        }
    }



}

fun Any.findMethod(methodName: String, vararg params: Class<*>) =
    ShareHelper.findMethod(this, methodName, *params)

fun Any.findField(fieldName: String) =
    ShareHelper.findField(this, fieldName)
