package com.v.permission

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.v.permission.floatwindow.VFloatWindowUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:15
 */
class VPermissionsActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 964
    }

    private lateinit var permissionListBean: ArrayList<VPermissionsBean>

    private var key: String? = null
    private var pkName: String? = ""


    private lateinit var config: VPermissionsConfig

    private var isPause = false

    //是否展示dialog
    private var isDialog = false

    //权限组是否存在悬浮窗权限
    private var isFloatWindowPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent == null || !intent.hasExtra("permissionListBean") || !intent.hasExtra("config")) {
            finish()
            return
        }

        intent?.run {
            permissionListBean =
                this.getSerializableExtra("permissionListBean") as ArrayList<VPermissionsBean>
            pkName = this.getStringExtra("packageName")
            key = this.getStringExtra("key")

            config = this.getSerializableExtra("config") as VPermissionsConfig

            isDialog = this.getBooleanExtra("isDialog", false)

        }

        isFloatWindowPermission = VPermissionsUtil.isFloatWindowPermission(permissionListBean)


        if (isDialog) {
            showNeedPermissionDialog()
        } else {
            val ps = ArrayList<String>()
            permissionListBean.forEach {
                ps.add(it.permission)
            }
            requestPermissions(ps.toTypedArray())
        }

    }

    override fun onResume() {
        super.onResume()
        if (isPause) {
            callBackPermissions()
        }

    }


    // 请求权限,回调时会触发onResume
    private fun requestPermissions(permission: Array<String>) {
        //如果申请得权限只有一条 并且这个一条得权限是悬浮窗权限
        if (permission.size == 1 && permission[0] == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            VFloatWindowUtils.requestPermission(this)
        } else {
            ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        if (requestCode != PERMISSION_REQUEST_CODE) {
            return
        }
        isPause = false

        // 需要延迟执行，不然即使授权，仍有部分机型获取不到权限
        Handler(Looper.getMainLooper()).postDelayed({
            permissions.forEach {
                //是否永不提醒 true没有点击   false点击了永不提醒
                if (!VPermissionsUtil.hasPermission(
                        this@VPermissionsActivity,
                        it
                    ) && !VPermissionsUtil.isNeverRemind(this@VPermissionsActivity, it)
                ) {
                    VPermissionsSPUtil.getInstance(this@VPermissionsActivity)
                        .putString(it, it)
                }
            }

            if (isFloatWindowPermission && !VFloatWindowUtils.checkPermission(this)) {
                VFloatWindowUtils.requestPermission(this)
                isFloatWindowPermission = false
            } else {
                callBackPermissions()
            }

        }, 500)


    }

    // 显示缺失权限提示 点击跳转到设置页面
    private fun showMissingPermissionDialog() {

        //获取未授权权限
        var ls = VPermissionsUtil.getPermissionDenied(
            this,
            config.isTipDetail,
            permissionListBean
        )

        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(config.beanRefuse.title)
        dialog.setMessage(
            config.beanRefuse.content + VPermissionsUtil.getTipMsg(ls)
        )

        dialog.setNegativeButton(config.beanRefuse.cancel) { dialog, which ->
            dialog.dismiss()
            callBackPermissions()
        }
        dialog.setPositiveButton(config.beanRefuse.confirm) { dialog, which ->
            dialog.dismiss()

            //如果申请得权限只有一条 并且这个一条得权限是悬浮窗权限
            if (ls.size == 1 && ls[0].permission == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                VFloatWindowUtils.requestPermission(this)
            } else {
                VPermissionsUtil.gotoSetting(this@VPermissionsActivity, pkName!!)
            }

        }
        dialog.setCancelable(false)
        dialog.show()

    }

    // 显示需要权限提示
    private fun showNeedPermissionDialog() {

        var listOff =
            VPermissionsUtil.getPermissionDenied(this, config.isTipDetail, permissionListBean)
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(config.beanFirst.title)
        dialog.setMessage(config.beanFirst.content + VPermissionsUtil.getTipMsg(listOff))
        dialog.setNegativeButton(config.beanFirst.cancel) { dialog, which ->
            dialog.dismiss()
            callBackPermissions()
        }
        dialog.setPositiveButton(config.beanFirst.confirm) { _, _ ->
            val ps = ArrayList<String>()
            listOff.forEach {
                it?.run {
                    ps.add(this.permission)
                }
            }
            requestPermissions(ps.toTypedArray())
        }
        dialog.setCancelable(false)
        dialog.show()


    }


    override fun onPause() {
        isPause = true
        super.onPause()

    }

    override fun onDestroy() {
        VPermissionsUtil.fetchCallbackListener(key)
        super.onDestroy()
    }


    //判断所有权限返回相对应的回调
    private fun callBackPermissions() {

        //永不提醒
        var listNeverRemind = ArrayList<VPermissionsBean>()
        //申请通过
        var listPass = ArrayList<VPermissionsBean>()
        //拒绝的
        var listDenied = ArrayList<VPermissionsBean>()

        //权限全部授权了才会回调授权成功
        if (VPermissionsUtil.hasPermission(this, permissionListBean)) {
            listPass.addAll(permissionListBean)
            VPermissionsUtil.fetchCallbackListener(key)?.run {
                onPass(listPass)
            }

            finish()
        } else {

            //拒绝权限 是否需要第二次提醒
            if (config.isShowRefuseDialog && isDialog) {
                showMissingPermissionDialog()
                config.isShowRefuseDialog = false
            } else {
                permissionListBean.forEach {
                    //获取当前权限是否点击了永不保存 并且保存了起来
                    val sp = VPermissionsSPUtil.getInstance(this@VPermissionsActivity)
                        .getString(it.permission)
                    if (!sp.isNullOrEmpty() && it.permission != Manifest.permission.SYSTEM_ALERT_WINDOW) {
                        listNeverRemind.add(it)
                        VPermissionsSPUtil.getInstance(this@VPermissionsActivity)
                            .putString(it.permission, it.permission)
                    }
                    if (!VPermissionsUtil.hasPermission(
                            this@VPermissionsActivity,
                            it.permission
                        )
                    ) {
                        listDenied.add(it)
                    }
                }

                VPermissionsUtil.fetchCallbackListener(key)?.run {
                    onDenied(listDenied)
                    if (listNeverRemind.size > 0) {
                        onNeverRemind(listNeverRemind)
                    }
                }

                finish()
            }

        }


    }

}