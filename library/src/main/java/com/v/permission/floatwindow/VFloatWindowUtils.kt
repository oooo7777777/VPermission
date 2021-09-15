package com.v.permission.floatwindow

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.v.permission.VPermissionsActivity

/**
 * @Author : ww
 * desc    : 申请悬浮窗
 * time    : 2021/9/15 17:18
 */
object VFloatWindowUtils {

    private const val TAG = "PermissionUtils--->"

    /**
     * 检测是否有悬浮窗权限
     * 6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
     */
    @JvmStatic
    fun checkPermission(context: Context): Boolean =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) when {
            RomUtils.checkIsHuaweiRom() -> huaweiPermissionCheck(context)
            RomUtils.checkIsMiuiRom() -> miuiPermissionCheck(context)
            RomUtils.checkIsOppoRom() -> oppoROMPermissionCheck(context)
            RomUtils.checkIsMeizuRom() -> meizuPermissionCheck(context)
            RomUtils.checkIs360Rom() -> qikuPermissionCheck(context)
            else -> true
        } else commonROMPermissionCheck(context)

    /**
     * 申请悬浮窗权限
     */

    fun requestPermission(activity: Activity) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) when {
            RomUtils.checkIsHuaweiRom() -> HuaweiUtils.applyPermission(activity)
            RomUtils.checkIsMiuiRom() -> MiuiUtils.applyMiuiPermission(activity)
            RomUtils.checkIsOppoRom() -> OppoUtils.applyOppoPermission(activity)
            RomUtils.checkIsMeizuRom() -> MeizuUtils.applyPermission(activity)
            RomUtils.checkIs360Rom() -> QikuUtils.applyPermission(activity)
            else -> Log.i(TAG, "原生 Android 6.0 以下无需权限申请")
        } else commonROMPermissionApply(activity)

    private fun huaweiPermissionCheck(context: Context) =
        HuaweiUtils.checkFloatWindowPermission(context)

    private fun miuiPermissionCheck(context: Context) =
        MiuiUtils.checkFloatWindowPermission(context)

    private fun meizuPermissionCheck(context: Context) =
        MeizuUtils.checkFloatWindowPermission(context)

    private fun qikuPermissionCheck(context: Context) =
        QikuUtils.checkFloatWindowPermission(context)

    private fun oppoROMPermissionCheck(context: Context) =
        OppoUtils.checkFloatWindowPermission(context)

    /**
     * 6.0以后，通用悬浮窗权限检测
     * 但是魅族6.0的系统这种方式不好用，需要单独适配一下
     */
    private fun commonROMPermissionCheck(context: Context): Boolean =
        if (RomUtils.checkIsMeizuRom()) meizuPermissionCheck(context) else {
            var result = true
            if (Build.VERSION.SDK_INT >= 23) try {
                val clazz = Settings::class.java
                val canDrawOverlays =
                    clazz.getDeclaredMethod("canDrawOverlays", Context::class.java)
                result = canDrawOverlays.invoke(null, context) as Boolean
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
            result
        }

    /**
     * 通用 rom 权限申请
     */
    private fun commonROMPermissionApply(activity: Activity) = when {
        // 这里也一样，魅族系统需要单独适配
        RomUtils.checkIsMeizuRom() -> MeizuUtils.applyPermission(activity)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> try {
            commonROMPermissionApplyInternal(activity)
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        // 需要做统计效果
        else -> Log.d(TAG, "user manually refuse OVERLAY_PERMISSION")
    }

    @JvmStatic
    fun commonROMPermissionApplyInternal(activity: Activity) = try {
        val clazz = Settings::class.java
        val field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION")
        val intent = Intent(field.get(null).toString())
        intent.data = Uri.parse("package:${activity.packageName}")
        activity.startActivityForResult(intent, VPermissionsActivity.PERMISSION_REQUEST_CODE)
    } catch (e: Exception) {
        Log.e(TAG, "$e")
    }

}

