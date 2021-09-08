package com.v.permission.listener

import com.v.permission.VPermissionsBean

/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:18
 */
interface VPermissionsListener {
    /**
     * 通过授权
     * @param list 通过的权限
     */
    fun onPass(list: ArrayList<VPermissionsBean>)

    /**
     * 拒绝授权
     * @param list 拒绝的权限
     */
    fun onDenied(list: ArrayList<VPermissionsBean>)

    /**
     * 永不提醒
     * @param list 永不提醒的权限
     */
    fun onNeverRemind(list: ArrayList<VPermissionsBean>)

}