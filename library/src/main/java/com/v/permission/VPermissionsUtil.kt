package com.v.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.v.permission.floatwindow.VFloatWindowUtils
import com.v.permission.listener.VPermissionsListener
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:40
 */
object VPermissionsUtil {

    val callbackMap = HashMap<String, VPermissionsListener>()

    /**
     * 判断权限是否授权
     *
     * @param context
     * @param permissions
     * @return
     */
    fun hasPermission(context: Context, permissions: ArrayList<VPermissionsBean>): Boolean {
        if (permissions.isEmpty()) {
            return false
        }
        for (per in permissions) {
            //权限组里面是否有悬浮窗权限
            if (per.permission == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                if (!VFloatWindowUtils.checkPermission(context)) {
                    return false
                }
            } else {
                val result = PermissionChecker.checkSelfPermission(context, per.permission)
                if (result != PermissionChecker.PERMISSION_GRANTED) {
                    return false
                }
                VPermissionsSPUtil.getInstance(context)
                    .remove(per.permission)
            }
        }
        return true
    }

    /**
     * 权限组里面是否有申请悬浮窗权限
     */
    fun isFloatWindowPermission(permissions: ArrayList<VPermissionsBean>): Boolean {

        for (per in permissions) {
            if (per.permission == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                return true
            }
        }
        return false
    }

    /**
     * 判断权限是否授权
     *
     * @param context
     * @param permissions
     * @return
     */
    fun hasPermission(context: Context, permissions: String): Boolean {
        if (permissions.isNullOrEmpty()) {
            return false
        }

        //权限组里面是否有悬浮窗权限
        if (permissions == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            if (!VFloatWindowUtils.checkPermission(context)) {
                return false
            }
        } else {
            val result = PermissionChecker.checkSelfPermission(context, permissions)
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                return false
            }
            VPermissionsSPUtil.getInstance(context)
                .remove(permissions)
        }

        return true
    }

    /**
     * 判断该权限是否永不提醒
     * 必须是授权弹窗出现的时候 才能获取到准确的判断
     */
    fun isNeverRemind(act: Activity, permission: String): Boolean {
        //是否永不提醒 true没有点击   false点击了永不提醒
        return shouldShowRequestPermissionRationale(act, permission)
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
        permissions: ArrayList<VPermissionsBean>
    ): ArrayList<VPermissionsBean> {
        val list = ArrayList<VPermissionsBean>()
        if (permissions.isEmpty()) {
            return list
        }

        permissions.forEach {
            if (!hasPermission(context, it.permission)) {
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
     * 获取永不提醒权限
     *
     * @param context
     * @param isTipDetail 是否显示每个权限的文字
     * @param permissions
     * @return
     */
    fun getPermissionNeverRemind(
        @NonNull context: Context,
        isTipDetail: Boolean,
        permissions: ArrayList<VPermissionsBean>
    ): ArrayList<VPermissionsBean> {
        val list = ArrayList<VPermissionsBean>()
        if (permissions.isEmpty()) {
            return list
        }

        permissions.forEach {
            //通过  false 表示没有通过  true表示通过了
            var isGranted = ContextCompat.checkSelfPermission(
                context,
                it.permission
            ) == PackageManager.PERMISSION_GRANTED


            //是否没有授权  true 没有授权 false授权了
            var isDenied = ContextCompat.checkSelfPermission(
                context,
                it.permission
            ) == PackageManager.PERMISSION_DENIED


            //是否永不提醒 true没有点击   false点击了永不提醒
            val isNeverRemind =
                shouldShowRequestPermissionRationale(context as Activity, it.permission)


            if (isGranted) {
                //通过
            } else if (isNeverRemind) {
                //拒绝
            } else {
                //永不提醒
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
    fun fetchCallbackListener(key: String?): VPermissionsListener? {
        return callbackMap.remove(key)
    }

    /**
     * 获取权限提示文字
     */
    fun getTipMsg(list: ArrayList<VPermissionsBean>): String {

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
    fun getPermissionTip(it: String): VPermissionsBean? {

        when (it) {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                return VPermissionsBean("存储权限", it)
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR ->
                return VPermissionsBean("日历权限", it)
            Manifest.permission.CAMERA ->
                return VPermissionsBean("相机权限", it)
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS ->
                return VPermissionsBean("联系人权限", it)
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION ->
                return VPermissionsBean("位置权限", it)
            Manifest.permission.RECORD_AUDIO ->
                return VPermissionsBean("录音权限", it)
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS -> {
                return VPermissionsBean("手机权限", it)
            }
            Manifest.permission.BODY_SENSORS ->
                return VPermissionsBean("传感器权限", it)

            Manifest.permission.SYSTEM_ALERT_WINDOW ->
                return VPermissionsBean("悬浮窗权限", it)

            else -> {
                return null
            }
        }


    }

    /**
     * 获取每个权限文案
     */
    fun getPermissionTipDetail(it: String): VPermissionsBean? {

        //写入/删除联系人信息权限
        when (it) {
            //CONTACTS 通讯录权限组
            Manifest.permission.WRITE_CONTACTS ->
                return VPermissionsBean("写入联系人信息权限", it)
            Manifest.permission.GET_ACCOUNTS ->
                return VPermissionsBean("查找设备上的帐户权限", it)
            Manifest.permission.READ_CONTACTS ->
                return VPermissionsBean("读取联系人信息权限", it)
            //  PHONE 通讯权限组
            Manifest.permission.READ_CALL_LOG ->
                return VPermissionsBean("读取通话记录权限", it)
            Manifest.permission.READ_PHONE_STATE ->
                return VPermissionsBean("读取电话状态权限", it)
            Manifest.permission.CALL_PHONE ->
                return VPermissionsBean("拨打电话权限", it)
            Manifest.permission.WRITE_CALL_LOG ->
                return VPermissionsBean("修改通话记录权限", it)
            Manifest.permission.USE_SIP ->
                return VPermissionsBean("SIP视频服务权限", it)
            Manifest.permission.PROCESS_OUTGOING_CALLS ->
                return VPermissionsBean("修改或放弃拨出电话权限", it)
            Manifest.permission.ADD_VOICEMAIL ->
                return VPermissionsBean("加到系统的语音邮件权限", it)
            //CALENDAR 日历权限组
            Manifest.permission.READ_CALENDAR ->
                return VPermissionsBean("读取日历权限", it)
            Manifest.permission.WRITE_CALENDAR ->
                return VPermissionsBean("修改日历权限", it)
            // CAMERA 相机权限组
            Manifest.permission.CAMERA ->
                return VPermissionsBean("相机权限", it)
            // SENSORS 定位权限组
            Manifest.permission.ACCESS_FINE_LOCATION ->
                return VPermissionsBean("GPS定位权限", it)
            // LOCATION 位置权限组
            Manifest.permission.ACCESS_COARSE_LOCATION ->
                return VPermissionsBean("WIFI和移动基站获取定位权限", it)
            // STORAGE 储存权限组
            Manifest.permission.READ_EXTERNAL_STORAGE ->
                return VPermissionsBean("读取内存卡权限", it)
            Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                return VPermissionsBean("写入内存卡权限", it)
            // MICROPHONE 麦克风权限组
            Manifest.permission.RECORD_AUDIO ->
                return VPermissionsBean("录音权限", it)
            //SMS 通信服务权限组
            Manifest.permission.READ_SMS ->
                return VPermissionsBean("读取短信记录权限", it)
            Manifest.permission.RECEIVE_WAP_PUSH ->
                return VPermissionsBean("接收WAP PUSH信息权限", it)
            Manifest.permission.RECEIVE_MMS ->
                return VPermissionsBean("接收彩信权限", it)
            Manifest.permission.RECEIVE_SMS ->
                return VPermissionsBean("接收短信息权限", it)
            Manifest.permission.SEND_SMS ->
                return VPermissionsBean("发送短信权限", it)
            Manifest.permission.BODY_SENSORS ->
                return VPermissionsBean("传感器权限", it)
            Manifest.permission.SYSTEM_ALERT_WINDOW ->
                return VPermissionsBean("悬浮窗权限", it)
            else -> {
                return null
            }
        }

    }

}