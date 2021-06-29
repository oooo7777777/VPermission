package com.v.permission

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:15
 */
class PermissionActivity : AppCompatActivity() {


    companion object {
        private const val PERMISSION_REQUEST_CODE = 64
    }

    private lateinit var permission: ArrayList<String>

    private lateinit var permissionBean: ArrayList<PermissionBean>

    private var isRequireCheck = false
    private var key: String? = null
    private lateinit var beanFirst: PermissionHintBean
    private lateinit var beanRefuse: PermissionHintBean

    private var pkName: String? = ""

    private var isShowRefuseDialog = true
    private var isTipDetail = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent == null || !intent.hasExtra("permission") || !intent.hasExtra("permissionBean")) {
            finish()
            return
        }

        isRequireCheck = true

        intent?.run {


            if (this.hasExtra("permission")) {
                permission = this.getStringArrayListExtra("permission") as ArrayList<String>
            } else if (this.hasExtra("permissionBean")) {
                permissionBean =
                    this.getSerializableExtra("permissionBean") as ArrayList<PermissionBean>
            }

            pkName = this.getStringExtra("packageName")
            key = this.getStringExtra("key")

            isShowRefuseDialog = this.getBooleanExtra("isShowRefuseDialog", true)
            isTipDetail = this.getBooleanExtra("isTipDetail", true)

            beanFirst = (this.getSerializableExtra("beanFirst") ?: PermissionHintBean(
                "权限申请",
                "请允许以下权限，否则将影响应用的正常使用。",
                "取消",
                "确定"
            )) as PermissionHintBean


            beanRefuse = (this.getSerializableExtra("beanRefuse") ?: PermissionHintBean(
                "部分功能无法使用",
                "请允许以下权限，否则将影响应用的正常使用。",
                "取消",
                "去授权"
            )) as PermissionHintBean


        }

    }

    override fun onResume() {
        super.onResume()
        if (isRequireCheck) {
            when {
                PermissionsUtil.hasPermission(this, permission) -> {
                    permissionsPass()
                }
                else -> {
                    showNeedPermissionDialog()
                    isRequireCheck = false
                }
            }
        } else {
            isRequireCheck = true
        }
    }

    // 请求权限兼容低版本
    private fun requestPermissions(permission: Array<String>) {
        ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE)
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

        //部分厂商手机系统返回授权成功时，厂商可以拒绝权限，所以要用PermissionChecker二次判断
        if (requestCode == PERMISSION_REQUEST_CODE && PermissionsUtil.isGranted(*grantResults)) {
            permissionsPass()
        } else if (isShowRefuseDialog) {
            showMissingPermissionDialog()
        } else { //不需要提示用户
            permissionsDenied()
        }
    }

    // 显示缺失权限提示 点击跳转到设置页面
    private fun showMissingPermissionDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(beanRefuse.title)
        dialog.setMessage(
            beanRefuse.content +
                    PermissionsUtil.getTipMsg(
                        PermissionsUtil.getPermissionDenied(
                            this,
                            isTipDetail,
                            permission
                        )
                    )
        )

        dialog.setNegativeButton(beanRefuse.cancel) { dialog, which -> permissionsDenied() }
        dialog.setPositiveButton(beanRefuse.confirm) { dialog, which ->
            PermissionsUtil.gotoSetting(this@PermissionActivity, pkName!!)
        }
        dialog.setCancelable(false)
        dialog.show()

    }

    // 显示需要权限提示
    private fun showNeedPermissionDialog() {

        var listOff = PermissionsUtil.getPermissionDenied(this, isTipDetail, permission)
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(beanFirst.title)
        dialog.setMessage(beanFirst.content + PermissionsUtil.getTipMsg(listOff))
        dialog.setNegativeButton(beanFirst.cancel) { dialog, which -> permissionsDenied() }
        dialog.setPositiveButton(beanFirst.confirm) { _, _ ->
            val ps = ArrayList<String>()
            listOff.forEach {
                it?.run {
                    ps.add(this.permission)
                }
            }
            requestPermissions(ps.toTypedArray()) // 请求权限,回调时会触发onResume
        }
        dialog.setCancelable(false)
        dialog.show()

    }

    private fun permissionsDenied() {
        PermissionsUtil.fetchListener(key)?.run {
            onDenied(
                PermissionsUtil.getPermissionDenied(this@PermissionActivity, true, permission),
                false
            )
        }
        finish()
    }

    // 全部权限均已获取
    private fun permissionsPass() {
        PermissionsUtil.fetchListener(key)?.run {
            onPass(PermissionsUtil.getPermissionPass(this@PermissionActivity, true, permission))
        }
        finish()
    }

    override fun onDestroy() {
        PermissionsUtil.fetchListener(key)
        super.onDestroy()
    }

}