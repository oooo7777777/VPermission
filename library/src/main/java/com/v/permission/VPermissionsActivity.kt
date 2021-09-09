package com.v.permission

import android.os.Bundle
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
class VPermissionsActivity : AppCompatActivity() {


    companion object {
        private const val PERMISSION_REQUEST_CODE = 964
    }

    private lateinit var permissionListBean: ArrayList<VPermissionsBean>

    private var key: String? = null
    private var pkName: String? = ""


    private lateinit var config: VPermissionsConfig

    private var isPause = false

    //是否展示dialog
    private var isDialog = false

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


        //永不提醒的权限是否跟传进来的权限一一对应
        var equalCount = 0
        //永不提醒
        var listNeverRemind = ArrayList<VPermissionsBean>()
        permissionListBean.forEach {
            val sp = VPermissionsSPUtil.getInstance(this).getString(it.permission)
            if (!sp.isNullOrEmpty()) {
                listNeverRemind.add(it)

                if (it.permission == sp) {
                    equalCount++
                }
            }

        }

        //如果永不提醒的权限跟传进来的权限对应的上 就展示去设置的dialog

        if (isDialog) {
            if (equalCount == permissionListBean.size) {
                showMissingPermissionDialog()
            } else {
                showNeedPermissionDialog()
            }
        }
        else
        {
            val ps = ArrayList<String>()
            permissionListBean.forEach {
                ps.add(it.permission)
            }
            ActivityCompat.requestPermissions(this, ps.toTypedArray(), PERMISSION_REQUEST_CODE)

        }

    }

    override fun onResume() {
        super.onResume()
        if (isPause) {
            callBackPermissions()
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

        callBackPermissions()
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

        dialog.setNegativeButton(config.beanRefuse.cancel) { dialog, which ->
            dialog.dismiss()
            callBackPermissions()
        }
        dialog.setPositiveButton(config.beanRefuse.confirm) { dialog, which ->
            dialog.dismiss()
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
            // 请求权限,回调时会触发onResume
            ActivityCompat.requestPermissions(this, ps.toTypedArray(), PERMISSION_REQUEST_CODE)
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
                    if (!sp.isNullOrEmpty()) {
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