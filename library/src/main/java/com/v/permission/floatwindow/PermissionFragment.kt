package com.v.permission.floatwindow

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.v.permission.VPermissionsActivity
import com.v.permission.listener.VPermissionsListener


/**
 * @Author : ww
 * desc    : 用于浮窗权限的申请，自动处理回调结果
 * time    : 2021/9/15 17:18
 */
internal class PermissionFragment : Fragment() {

    companion object {
        private var onPermissionResult: VPermissionsListener? = null

        fun requestPermission(activity: Activity,  listener: VPermissionsListener) {
            this.onPermissionResult = onPermissionResult
            activity.fragmentManager
                .beginTransaction()
                .add(PermissionFragment(), activity.localClassName)
                .commitAllowingStateLoss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 权限申请
//        VFloatWindowUtils.requestPermission(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == VPermissionsActivity.PERMISSION_REQUEST_CODE) {
            // 需要延迟执行，不然即使授权，仍有部分机型获取不到权限
            Handler(Looper.getMainLooper()).postDelayed({
                val activity = activity ?: return@postDelayed
                val check = VFloatWindowUtils.checkPermission(activity)
                // 回调权限结果
//                onPermissionResult?.permissionResult(check)
                onPermissionResult = null
                // 将Fragment移除
                fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            }, 500)
        }
    }

}
