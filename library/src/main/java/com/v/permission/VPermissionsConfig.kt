package com.v.permission

import java.io.Serializable

/**
 * @Author : ww
 * desc    : 用于拒绝权限提示弹窗
 * time    : 2021/6/25 17:24
 */
data class VPermissionsConfig(
    var beanFirst: VPermissionsHintBean = VPermissionsHintBean(
        "权限申请",
        "请允许以下权限，否则将影响应用的正常使用。",
        "取消",
        "确定"
    ), //提示权限弹窗 如果不传会使用默认的
    var beanRefuse: VPermissionsHintBean = VPermissionsHintBean(
        "部分功能无法使用",
        "请允许以下权限，否则将影响应用的正常使用。",
        "取消",
        "去授权"
    ),//权限拒绝后再次弹窗 如果不传会使用默认的
    var isShowRefuseDialog: Boolean = true,//权限拒绝后 是否弹出第二次弹窗
    var isTipDetail: Boolean = true//每个权限的文案 是使用详细的还是模糊的  详细的为每个权限的文案 模糊的为每一组文案
) : Serializable


/**
 * @Author : ww
 * desc    : 用于拒绝权限提示弹窗
 * time    : 2021/6/25 17:24
 */
data class VPermissionsHintBean(
    var title: String? = null,//弹窗标题
    var content: String? = null,//弹窗文案
    var cancel: String? = null,//取消按钮
    var confirm: String? = null//确定按钮
) : Serializable

/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:24
 */
data class VPermissionsBean(
    var des: String,
    var permission: String
) : Serializable
