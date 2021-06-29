package com.v.permission

import java.io.Serializable

/**
 * @Author : ww
 * desc    : 用于拒绝权限提示弹窗
 * time    : 2021/6/25 17:24
 */
data class PermissionHintBean(var title: String? = null,
                              var content: String? = null,
                              var cancel: String? =null,
                              var confirm: String? = null): Serializable
