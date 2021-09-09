package com.v.permission

import android.content.Context
import android.content.SharedPreferences

/**
 * SP相关工具类
 */
class VPermissionsSPUtil private constructor(context: Context)
{



    private var sp: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    val all: Map<String, *>
        get() = sp!!.all

    init
    {
        this.init(context)
    }

    /**
     * SPUtils构造函数
     *
     * 在Application中初始化
     *
     * @param context 上下文
     */
    fun init(context: Context)
    {
        if (this.sp == null || this.editor == null)
        {
            try
            {
                this.sp = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE)
                this.editor = this.sp!!.edit()
                editor!!.apply()
            }
            catch (var3: Exception)
            {
            }

        }
    }


    /**
     * SP中写入String类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putString(key: String, value: String)
    {
        editor!!.putString(key, value)
            .apply()
    }

    /**
     * SP中读取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getString(key: String, defaultValue: String? = null): String?
    {
        return sp!!.getString(key, defaultValue)
    }

    /**
     * SP中写入int类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putInt(key: String, value: Int)
    {
        editor!!.putInt(key, value)
            .apply()
    }

    /**
     * SP中读取int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getInt(key: String, defaultValue: Int = -1): Int
    {
        return sp!!.getInt(key, defaultValue)
    }

    /**
     * SP中写入long类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putLong(key: String, value: Long)
    {
        editor!!.putLong(key, value)
            .apply()
    }

    /**
     * SP中读取long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getLong(key: String, defaultValue: Long = -1L): Long
    {
        return sp!!.getLong(key, defaultValue)
    }

    /**
     * SP中写入float类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putFloat(key: String, value: Float)
    {
        editor!!.putFloat(key, value)
            .apply()
    }

    /**
     * SP中读取float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getFloat(key: String, defaultValue: Float = -1f): Float
    {
        return sp!!.getFloat(key, defaultValue)
    }

    /**
     * SP中写入boolean类型value
     *
     * @param key   键
     * @param value 值
     */
    fun putBoolean(key: String, value: Boolean)
    {
        editor!!.putBoolean(key, value)
            .apply()
    }

    /**
     * SP中读取boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值`defaultValue`
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    {
        return sp!!.getBoolean(key, defaultValue)
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    fun remove(key: String)
    {
        editor!!.remove(key)
            .apply()
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return `true`: 存在<br></br>`false`: 不存在
     */
    operator fun contains(key: String): Boolean
    {
        return sp!!.contains(key)
    }

    /**
     * SP中清除所有数据
     */
    fun clear()
    {
        editor!!.clear()
            .apply()
    }


    fun saveBean()
    {

    }

    companion object
    {
        private var instance: VPermissionsSPUtil? = null
        private val PREFERENCENAME = "yd_config_default"

        fun getInstance(context: Context): VPermissionsSPUtil
        {
            if (instance == null)
            {
                instance = VPermissionsSPUtil(context)
                instance!!.init(context)
            }
            return instance as VPermissionsSPUtil
        }
    }
}