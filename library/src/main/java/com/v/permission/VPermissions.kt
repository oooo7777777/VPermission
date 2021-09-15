package com.v.permission

import android.content.Context
import android.content.Intent
import com.v.permission.listener.VPermissionsListener
import java.lang.Exception


class VPermissions {

    private var config: VPermissionsConfig? = null
    private var permissionsListener: VPermissionsListener? = null
    private var list: ArrayList<VPermissionsBean>? = null


    class Builder {

        private var config: VPermissionsConfig? = null
        private var permissionsListener: VPermissionsListener? = null
        private var list: ArrayList<VPermissionsBean>? = null


        /**
         * 设置VPermissionsConfig
         */
        fun setConfig(config: VPermissionsConfig): Builder {
            this.config = config
            return this
        }

        /**
         * 设置默认提示文案 需要申请的权限
         */
        fun setPermission(vararg permissions: String): Builder {
            this.list?.clear()

            var list = ArrayList<VPermissionsBean>()
            permissions.forEach {
                if (config?.isTipDetail == true) {
                    VPermissionsUtil.getPermissionTipDetail(it)?.run {
                        list.add(this)
                    }
                } else {
                    VPermissionsUtil.getPermissionTip(it)?.run {
                        list.add(this)
                    }
                }
            }
            this.list = list
            return this
        }

        /**
         * 设置自定义提示文案 需要申请的权限
         */
        fun setPermission(list: ArrayList<VPermissionsBean>): Builder {
            this.list?.clear()

            this.list = list
            return this
        }


        /**
         * 设置默认dialog权限申请回调
         */
        fun callback(permissionsListener: VPermissionsListener): Builder {
            this.permissionsListener = permissionsListener
            return this
        }


        fun create(context: Context) {
            dispose(context, false)
        }

        fun createDialog(context: Context) {
            dispose(context, true)
        }

        private fun dispose(context: Context, isDialog: Boolean) {
            if (list.isNullOrEmpty()) {
                throw Exception("VPermissions 申请的权限不能为空")
            }

            if (permissionsListener == null) {
                throw Exception("VPermissions 请设置callback")
            }


            val permissions = VPermissions()
            permissions.config = config ?: VPermissionsConfig()
            permissions.list = list
            permissions.permissionsListener = permissionsListener

            val key = System.currentTimeMillis().toString()
            VPermissionsUtil.callbackMap[key] = permissions.permissionsListener!!

            if (VPermissionsUtil.hasPermission(context, list!!)) {
                VPermissionsUtil.fetchCallbackListener(key)?.run {
                    onPass(list!!)
                }
            } else {


                val intent = Intent(context, VPermissionsActivity::class.java)
                intent.putExtra("permissionListBean", list)
                intent.putExtra("packageName", context.packageName)
                intent.putExtra("key", key)
                intent.putExtra("config", permissions.config)
                intent.putExtra("isDialog", isDialog)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }

        }
    }
}