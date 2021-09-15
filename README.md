# VPermission

[![](https://jitpack.io/v/oooo7777777/VPermission.svg)](https://jitpack.io/#oooo7777777/VPermission)


#### 介绍

- 权限申请库
- 自带申请提示弹窗(根据创建方式是否需要自带的提示弹窗)
- 自带每个权限的文案(根据你传进去的权限,会自带中文文案,也可以自定义)
- 自带拒绝后第二次弹窗(引导用户前往设置开启权限)
- 可针对权限，设置自定义的文案
- 可申请悬浮窗权限(已经做好了机型适配)

#### 集成

- **1. 在root build.gradle中加入Jitpack仓库**

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

- **2. 在app build.gradle中添加依赖**

```
dependencies {
  ...
  implementation 'com.github.oooo7777777:VPermission:latestVersion'
}
```

#### 使用

- **使用默认配置**

```

    VPermissions.Builder()
                .setPermission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                )
                .callback(object : VPermissionsListener {
                    override fun onPass(list: ArrayList<VPermissionsBean>) {
                        //"权限全部通过
                    }

                    override fun onDenied(list: ArrayList<VPermissionsBean>) {
                        //"权限未通过
                    }

                    override fun onNeverRemind(list: ArrayList<VPermissionsBean>) {
                        //"永不提醒的权限
                    }

                })
                .createDialog(this)

```

- **使用自定义配置**

```

   VPermissions.Builder()
                 .setConfig( VPermissionsConfig().apply {
                     //提示权限弹窗 如果不传会使用默认的
                     beanFirst = VPermissionsHintBean("提示", "部分功能无法正常使用，请允许以下权限。", "取消", "确定")
                     //权限拒绝后再次弹窗 如果不传会使用默认的
                     beanRefuse = VPermissionsHintBean("警告", "因为你拒绝了权限，导致部分功能无法正常使用，请允许以下权限。", "取消", "去授权")
                     //每个权限的文案 是使用详细的还是模糊的  详细的为每个权限的文案 模糊的为每一组文案
                     isTipDetail = true
                     //拒绝权限  是否弹出第二次弹窗
                     isShowRefuseDialog = true
                 })
                 .setPermission(
                     Manifest.permission.CAMERA,
                     Manifest.permission.RECORD_AUDIO,
                     Manifest.permission.READ_EXTERNAL_STORAGE,
                     Manifest.permission.WRITE_EXTERNAL_STORAGE
                 )
                 .callback(object : VPermissionsListener {
                     override fun onPass(list: ArrayList<VPermissionsBean>) {
                         //权限全部通过
                     }
 
                     override fun onDenied(list: ArrayList<VPermissionsBean>) {
                        //权限未通过
                     }
 
                     override fun onNeverRemind(list: ArrayList<VPermissionsBean>) {
                         //权限永不提醒
                     }
 
                 })
                 .createDialog(this)

```

- **为每个权限配置自定义的文案**

```
    VPermissions.Builder()
                .setPermission(ArrayList<VPermissionsBean>().apply {
                    add(VPermissionsBean("需要使用相机权限，以正常使用拍照、视频等功能。", Manifest.permission.CAMERA))
                    add(VPermissionsBean("需要使用麦克风权限，以正常使用语音等功能。", Manifest.permission.RECORD_AUDIO))
                    add(VPermissionsBean("需要存储权限，以帮您缓存照片，视频等内容，节省流量。", Manifest.permission.READ_EXTERNAL_STORAGE)
                    )
                })
                .callback(object : VPermissionsListener {
                    override fun onPass(list: ArrayList<VPermissionsBean>) {
                        //权限全部通过
                    }

                    override fun onDenied(list: ArrayList<VPermissionsBean>) {
                        //权限未通过
                    }

                    override fun onNeverRemind(list: ArrayList<VPermissionsBean>) {
                        //权限永不提醒
                    }

                })
                .createDialog(this)

```

#### 其他

- **自定义配置VPermissionsConfig**

```
VPermissionsConfig(
    //申请权限弹窗文案
    var beanFirst: VPermissionsHintBean = VPermissionsHintBean(
        "权限申请",
        "请允许以下权限，否则将影响应用的正常使用。",
        "取消",
        "确定"
    ),
    //第二次权限弹窗文案
    var beanRefuse: VPermissionsHintBean = VPermissionsHintBean(
        "部分功能无法使用",
        "请允许以下权限，否则将影响应用的正常使用。",
        "取消",
        "去授权"
    ),
    var isShowRefuseDialog: Boolean = true,//拒绝权限后  是否弹出第二次弹窗
    var isTipDetail: Boolean = true//每个权限的文案 是使用详细的还是模糊的  详细的为每个权限的对应文案 模糊的为每一组文案
) 
```

- **createDialog(this)**
这种创建方式会使用VPermissions里面写好的dialog

- **create(this)**
这种方式不会显示dialog,只会回调callback

