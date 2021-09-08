package com.v.permission

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:15
 */
class VPermissionsActivity : AppCompatActivity() {


    companion object {
        private const val PERMISSION_REQUEST_CODE = 64
    }

    private lateinit var permissionListBean: ArrayList<VPermissionsBean>

    private var isRequireCheck = false
    private var key: String? = null
    private var pkName: String? = ""


    private lateinit var config: VPermissionsConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent == null || !intent.hasExtra("permissionListBean") || !intent.hasExtra("config")) {
            finish()
            return
        }

        isRequireCheck = true

        intent?.run {

            permissionListBean =
                this.getSerializableExtra("permissionListBean") as ArrayList<VPermissionsBean>

            pkName = this.getStringExtra("packageName")
            key = this.getStringExtra("key")

            config = this.getSerializableExtra("config") as VPermissionsConfig

        }

    }

    override fun onResume() {
        super.onResume()
        if (isRequireCheck) {
            when {
                VPermissionsUtil.hasPermission(this, permissionListBean) -> {
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
        if (requestCode == PERMISSION_REQUEST_CODE && VPermissionsUtil.isGranted(*grantResults)) {
            permissionsPass()
        }
        //拒绝权限是否需要第二次提醒
        else if (config.isShowRefuseDialog) {
            showMissingPermissionDialog()
        }
        //权限申请失败
        else {
            permissionsDenied(true)

        }

    }

    // 显示缺失权限提示 点击跳转到设置页面
    private fun showMissingPermissionDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(config.beanRefuse.title)
        dialog.setMessage(
            config.beanRefuse.content +
                    VPermissionsUtil.getTipMsg(
                        VPermissionsUtil.getPermissionDenied(
                            this,
                            config.isTipDetail,
                            permissionListBean
                        )
                    )
        )

        dialog.setNegativeButton(config.beanRefuse.cancel) { dialog, which -> permissionsDenied() }
        dialog.setPositiveButton(config.beanRefuse.confirm) { dialog, which ->
            VPermissionsUtil.gotoSetting(this@VPermissionsActivity, pkName!!)
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
        dialog.setNegativeButton(config.beanFirst.cancel) { dialog, which -> permissionsDenied() }
        dialog.setPositiveButton(config.beanFirst.confirm) { _, _ ->
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

    private fun permissionsDenied(isApply: Boolean = false) {
        VPermissionsUtil.fetchCallbackListener(key)?.run {


            if (isApply) {
                var listNeverRemind = ArrayList<VPermissionsBean>()
                VPermissionsUtil.getPermissionDenied(
                    this@VPermissionsActivity,
                    true,
                    permissionListBean
                ).forEach {
                    //是否永不提醒 true没有点击   false点击了永不提醒
                    val isNeverRemind =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this@VPermissionsActivity, it.permission
                        )
                    if (!isNeverRemind) {
                        listNeverRemind.add(it)
                    }
                }
                if (listNeverRemind.size > 0) {
                    onNeverRemind(listNeverRemind)
                }
            }

            onDenied(
                VPermissionsUtil.getPermissionDenied(
                    this@VPermissionsActivity,
                    true,
                    permissionListBean
                )
            )

        }
        finish()
    }


    // 全部权限均已获取
    private fun permissionsPass() {
        VPermissionsUtil.fetchCallbackListener(key)?.run {
            onPass(
                VPermissionsUtil.getPermissionPass(
                    this@VPermissionsActivity,
                    true,
                    permissionListBean
                )
            )
        }
        finish()
    }

    override fun onDestroy() {
        VPermissionsUtil.fetchCallbackListener(key)
        super.onDestroy()
    }


}