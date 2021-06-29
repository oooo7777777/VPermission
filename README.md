# VPermission

[![](https://jitpack.io/v/oooo7777777/VPermission.svg)](https://jitpack.io/#oooo7777777/VPermission)


#### 介绍

- 权限申请库
- 自带每个权限的文案
- 自带申请提示弹窗
- 自带拒绝弹窗
- 可针对权限，设置自定义的文案

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

 PermissionsUtil.requestPermission(
                this,
                PermissionConfig(),
                object : PermissionListener {
                    override fun onPass(list: ArrayList<PermissionBean>) {
                        Toast.makeText(this@MainActivity, "权限全部通过", Toast.LENGTH_LONG).show()
                    }

                    override fun onDenied(list: ArrayList<PermissionBean>, isNotPrompt: Boolean) {
                        Toast.makeText(this@MainActivity, "权限未通过", Toast.LENGTH_LONG).show()
                    }

                },
                Manifest.permission.CAMERA
            )

```

- **使用自定义配置**

```

 private val permissionConfig by lazy {
        PermissionConfig().apply {
            //提示权限弹窗 如果不传会使用默认的
            beanFirst = PermissionHintBean("提示", "部分功能无法正常使用，请允许以下权限。", "取消", "确定")
            //权限拒绝后再次弹窗 如果不传会使用默认的
            beanRefuse = PermissionHintBean("警告", "因为你拒绝了权限，导致部分功能无法正常使用，请允许以下权限。", "取消", "去授权")
            //每个权限的文案 是使用详细的还是模糊的  详细的为每个权限的文案 模糊的为每一组文案  默认为true
            isTipDetail = true
            //权限拒绝后 是否弹出第二次弹窗 默认为true
            isTipDetail = true
        }
    }


 PermissionsUtil.requestPermission(
                this,
                permissionConfig,
                object : PermissionListener {
                    override fun onPass(list: ArrayList<PermissionBean>) {
                        Toast.makeText(this@MainActivity, "权限全部通过", Toast.LENGTH_LONG).show()
                    }

                    override fun onDenied(list: ArrayList<PermissionBean>, isNotPrompt: Boolean) {
                        Toast.makeText(this@MainActivity, "权限未通过", Toast.LENGTH_LONG).show()
                    }

                },
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

```

- **为每个权限配置自定义的文案**

```

 private val permissionConfig by lazy {
        PermissionConfig().apply {
            //提示权限弹窗 如果不传会使用默认的
            beanFirst = PermissionHintBean("提示", "部分功能无法正常使用，请允许以下权限。", "取消", "确定")
            //权限拒绝后再次弹窗 如果不传会使用默认的
            beanRefuse = PermissionHintBean("警告", "因为你拒绝了权限，导致部分功能无法正常使用，请允许以下权限。", "取消", "去授权")
            //每个权限的文案 是使用详细的还是模糊的  详细的为每个权限的文案 模糊的为每一组文案  默认为true
            isTipDetail = true
            //权限拒绝后 是否弹出第二次弹窗 默认为true
            isTipDetail = true
        }
    }


private val list by lazy {
        ArrayList<PermissionBean>().apply {
            add(PermissionBean("需要使用相机权限，以正常使用拍照、视频等功能。", Manifest.permission.CAMERA))
            add(PermissionBean("需要使用麦克风权限，以正常使用语音等功能。", Manifest.permission.RECORD_AUDIO))
            add(
                PermissionBean(
                    "需要存储权限，以帮您缓存照片，视频等内容，节省流量。",
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }
             
 PermissionsUtil.requestPermission(
                 this,
                 permissionConfig,
                 object : PermissionListener {
                     override fun onPass(list: ArrayList<PermissionBean>) {
                         Toast.makeText(this@MainActivity, "权限全部通过", Toast.LENGTH_LONG).show()
                     }
 
                     override fun onDenied(list: ArrayList<PermissionBean>, isNotPrompt: Boolean) {
                         Toast.makeText(this@MainActivity, "权限未通过", Toast.LENGTH_LONG).show()
                     }
                 },
                 list
             )
```