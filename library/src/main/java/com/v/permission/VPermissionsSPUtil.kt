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


    init
    {
        this.init(context)
    }


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


    fun putString(key: String, value: String)
    {
        editor!!.putString(key, value)
            .apply()
    }


    fun getString(key: String, defaultValue: String? = null): String?
    {
        return sp!!.getString(key, defaultValue)
    }


    fun remove(key: String)
    {
        editor!!.remove(key)
            .apply()
    }


    operator fun contains(key: String): Boolean
    {
        return sp!!.contains(key)
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