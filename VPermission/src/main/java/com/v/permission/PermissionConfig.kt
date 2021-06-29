package com.v.permission

import java.io.Serializable

/**
 * @Author : ww
 * desc    : 用于拒绝权限提示弹窗
 * time    : 2021/6/25 17:24
 */
data class PermissionConfig(
    var beanFirst: PermissionHintBean = PermissionHintBean(
        "权限申请",
        "请允许以下权限，否则将影响应用的正常使用。",
        "取消",
        "确定"
    ),
    var beanRefuse: PermissionHintBean = PermissionHintBean(
        "部分功能无法使用",
        "请允许以下权限，否则将影响应用的正常使用。",
        "取消",
        "去授权"
    ),
    var isShowRefuseDialog: Boolean = true,
    var isTipDetail: Boolean = true
) : Serializable
