package com.v.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:40
 */
object PermissionsUtil {

    private val listenerMap = HashMap<String, PermissionListener>()

    fun requestPermission(
        context: Context,
        config: PermissionConfig,
        listener: PermissionListener,
        vararg permissions: String
    ) {

        config?.run {

            var list = ArrayList<PermissionBean>()

            permissions.forEach {
                if (isTipDetail) {
                    getPermissionTipDetail(it)?.run {
                        list.add(this)
                    }
                } else {
                    getPermissionTip(it)?.run {
                        list.add(this)
                    }
                }
            }

            if (hasPermission(context, list)) {
                listener.onPass(list)
            } else {

                val key = System.currentTimeMillis().toString()
                listenerMap[key] = listener
                val intent = Intent(context, PermissionActivity::class.java)
                intent.putExtra("permissionListBean", list)
                intent.putExtra("packageName", context.packageName)
                intent.putExtra("key", key)
                intent.putExtra("beanFirst", beanFirst)
                intent.putExtra("beanRefuse", beanRefuse)
                intent.putExtra("isShowRefuseDialog", isShowRefuseDialog)
                intent.putExtra("isTipDetail", isTipDetail)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

        }

    }


    fun requestPermission(
        context: Context,
        config: PermissionConfig,
        listener: PermissionListener,
        list: ArrayList<PermissionBean>
    ) {

        config.run {

            if (hasPermission(context, list)) {
                listener.onPass(list)
            } else {

                val key = System.currentTimeMillis().toString()
                listenerMap[key] = listener
                val intent = Intent(context, PermissionActivity::class.java)
                intent.putExtra("permissionListBean", list)
                intent.putExtra("packageName", context.packageName)
                intent.putExtra("key", key)
                intent.putExtra("beanFirst", beanFirst)
                intent.putExtra("beanRefuse", beanRefuse)
                intent.putExtra("isShowRefuseDialog", isShowRefuseDialog)
                intent.putExtra("isTipDetail", isTipDetail)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

    }

    /**
     * 判断权限是否授权
     *
     * @param context
     * @param permissions
     * @return
     */
    fun hasPermission(context: Context, permissions: ArrayList<PermissionBean>): Boolean {
        if (permissions.isEmpty()) {
            return false
        }
        for (per in permissions) {
            val result = PermissionChecker.checkSelfPermission(context, per.permission)
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 判断一组授权结果是否为授权通过
     *
     * @param grantResult
     * @return
     */
    fun isGranted(vararg grantResult: Int): Boolean {
        if (grantResult.isEmpty()) {
            return false
        }
        for (result in grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 获取未授权的权限
     *
     * @param context
     * @param isTipDetail 是否显示每个权限的文字
     * @param permissions
     * @return
     */
    fun getPermissionDenied(
        @NonNull context: Context,
        isTipDetail: Boolean,
        permissions: ArrayList<PermissionBean>
    ): ArrayList<PermissionBean> {
        val list = ArrayList<PermissionBean>()
        if (permissions.isEmpty()) {
            return list
        }

        permissions.forEach {
            val result = PermissionChecker.checkSelfPermission(context, it.permission)
            if (result != PackageManager.PERMISSION_GRANTED) {

                //如果用户没有自定义权限的文案 就获取默认的
                if (it.des.isNullOrEmpty()) {
                    if (isTipDetail) {
                        getPermissionTipDetail(it.permission)?.run {
                            list.add(this)
                        }
                    } else {
                        getPermissionTip(it.permission)?.run {
                            list.add(this)
                        }
                    }
                } else {
                    list.add(it)
                }
            }
        }
        return list
    }

    /**
     * 获取已授权的权限
     *
     * @param context
     * @param isTipDetail 是否显示每个权限的文字
     * @param permissions
     * @return
     */
    fun getPermissionPass(
        @NonNull context: Context,
        isTipDetail: Boolean,
        permissions: ArrayList<PermissionBean>
    ): ArrayList<PermissionBean> {
        val list = ArrayList<PermissionBean>()
        if (permissions.isEmpty()) {
            return list
        }

        permissions.forEach {
            val result = PermissionChecker.checkSelfPermission(context, it.permission)
            if (result == PermissionChecker.PERMISSION_GRANTED) {
                if (it.des.isNullOrEmpty()) {
                    if (isTipDetail) {
                        getPermissionTipDetail(it.permission)?.run {
                            list.add(this)
                        }
                    } else {
                        getPermissionTip(it.permission)?.run {
                            list.add(this)
                        }
                    }
                } else {
                    list.add(it)
                }
            }
        }
        return list
    }

    /**
     * 跳转到当前应用对应的设置页面
     *
     * @param context
     */
    fun gotoSetting(context: Context, packageName: String) {

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        context.startActivity(intent)
    }


    /**
     * @param key
     * @return
     */
    fun fetchListener(key: String?): PermissionListener? {
        return listenerMap.remove(key)
    }

    /**
     * 获取权限提示文字
     */
    fun getTipMsg(list: ArrayList<PermissionBean>): String {

        var sb = StringBuffer()
        list.forEach {
            it?.run {
                sb.append(this.des)
                sb.append("\n")
            }
        }
        return "\n\n$sb"
    }


    /**
     * 获取权限组文案
     */
    private fun getPermissionTip(it: String): PermissionBean? {

        when (it) {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                return PermissionBean("存储权限", it)
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR ->
                return PermissionBean("日历权限", it)
            Manifest.permission.CAMERA ->
                return PermissionBean("相机权限", it)
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS ->
                return PermissionBean("联系人权限", it)
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION ->
                return PermissionBean("位置权限", it)
            Manifest.permission.RECORD_AUDIO ->
                return PermissionBean("录音权限", it)
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS -> {
                return PermissionBean("手机权限", it)
            }
            Manifest.permission.BODY_SENSORS ->
                return PermissionBean("传感器权限", it)
            else -> {
                return null
            }
        }


    }

    /**
     * 获取每个权限文案
     */
    private fun getPermissionTipDetail(it: String): PermissionBean? {

        //写入/删除联系人信息权限
        when (it) {
            //CONTACTS 通讯录权限组
            Manifest.permission.WRITE_CONTACTS ->
                return PermissionBean("写入联系人信息权限", it)
            Manifest.permission.GET_ACCOUNTS ->
                return PermissionBean("查找设备上的帐户权限", it)
            Manifest.permission.READ_CONTACTS ->
                return PermissionBean("读取联系人信息权限", it)
            //  PHONE 通讯权限组
            Manifest.permission.READ_CALL_LOG ->
                return PermissionBean("读取通话记录权限", it)
            Manifest.permission.READ_PHONE_STATE ->
                return PermissionBean("读取电话状态权限", it)
            Manifest.permission.CALL_PHONE ->
                return PermissionBean("拨打电话权限", it)
            Manifest.permission.WRITE_CALL_LOG ->
                return PermissionBean("修改通话记录权限", it)
            Manifest.permission.USE_SIP ->
                return PermissionBean("SIP视频服务权限", it)
            Manifest.permission.PROCESS_OUTGOING_CALLS ->
                return PermissionBean("修改或放弃拨出电话权限", it)
            Manifest.permission.ADD_VOICEMAIL ->
                return PermissionBean("加到系统的语音邮件权限", it)
            //CALENDAR 日历权限组
            Manifest.permission.READ_CALENDAR ->
                return PermissionBean("读取日历权限", it)
            Manifest.permission.WRITE_CALENDAR ->
                return PermissionBean("修改日历权限", it)
            // CAMERA 相机权限组
            Manifest.permission.CAMERA ->
                return PermissionBean("相机权限", it)
            // SENSORS 定位权限组
            Manifest.permission.ACCESS_FINE_LOCATION ->
                return PermissionBean("GPS定位权限", it)
            // LOCATION 位置权限组
            Manifest.permission.ACCESS_COARSE_LOCATION ->
                return PermissionBean("WIFI和移动基站获取定位权限", it)
            // STORAGE 储存权限组
            Manifest.permission.READ_EXTERNAL_STORAGE ->
                return PermissionBean("读取内存卡权限", it)
            Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                return PermissionBean("读取内存卡权限", it)
            // MICROPHONE 麦克风权限组
            Manifest.permission.RECORD_AUDIO ->
                return PermissionBean("录音权限", it)
            //SMS 通信服务权限组
            Manifest.permission.READ_SMS ->
                return PermissionBean("读取短信记录权限", it)
            Manifest.permission.RECEIVE_WAP_PUSH ->
                return PermissionBean("接收WAP PUSH信息权限", it)
            Manifest.permission.RECEIVE_MMS ->
                return PermissionBean("接收彩信权限", it)
            Manifest.permission.RECEIVE_SMS ->
                return PermissionBean("接收短信息权限", it)
            Manifest.permission.SEND_SMS ->
                return PermissionBean("发送短信权限", it)
            Manifest.permission.BODY_SENSORS ->
                return PermissionBean("传感器权限", it)
            else -> {
                return null
            }
        }

    }

}