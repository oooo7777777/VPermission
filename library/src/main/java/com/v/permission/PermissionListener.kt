package com.v.permission

/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:18
 */
interface PermissionListener {
    /**
     * 通过授权
     * @param list 通过的权限
     */
    fun onPass(list: ArrayList<PermissionBean>)

    /**
     * 拒绝授权
     * @param list 拒绝的权限
     * @param isNotPrompt 是否选中了不再提示
     */
    fun onDenied(list: ArrayList<PermissionBean>, isNotPrompt: Boolean)
}