package com.v.permission

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:10
 */
class MainActivity : AppCompatActivity() {


    private val permissionConfig by lazy {
        PermissionConfig().apply {
            //提示权限弹窗 如果不传会使用默认的
            beanFirst = PermissionHintBean("提示", "部分功能无法正常使用，请允许以下权限。", "取消", "确定")
            //权限拒绝后再次弹窗 如果不传会使用默认的
            beanRefuse = PermissionHintBean("警告", "因为你拒绝了权限，导致部分功能无法正常使用，请允许以下权限。", "取消", "去授权")
            //每个权限的文案 是使用详细的还是模糊的  详细的为每个权限的文案 模糊的为每一组文案
            isTipDetail = true
            //权限拒绝后 是否弹出第二次弹窗
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt0).setOnClickListener {
            PermissionsUtil.requestPermission(
                this,
                PermissionConfig(),
                object : PermissionListener {
                    override fun onPass(list: ArrayList<PermissionBean>) {
                        Toast.makeText(this@MainActivity, "权限全部通过", Toast.LENGTH_LONG).show()
                        setContent(list)
                    }
                    override fun onDenied(list: ArrayList<PermissionBean>, isNotPrompt: Boolean) {
                        Toast.makeText(this@MainActivity, "权限未通过", Toast.LENGTH_LONG).show()
                        setContent(list)
                    }
                },
                Manifest.permission.CAMERA
            )

        }

        findViewById<Button>(R.id.bt1).setOnClickListener {
            PermissionsUtil.requestPermission(
                this,
                permissionConfig,
                object : PermissionListener {
                    override fun onPass(list: ArrayList<PermissionBean>) {
                        Toast.makeText(this@MainActivity, "权限全部通过", Toast.LENGTH_LONG).show()
                        setContent(list)
                    }
                    override fun onDenied(list: ArrayList<PermissionBean>, isNotPrompt: Boolean) {
                        Toast.makeText(this@MainActivity, "权限未通过", Toast.LENGTH_LONG).show()
                        setContent(list)
                    }
                },
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        }


        findViewById<Button>(R.id.bt2).setOnClickListener {

            PermissionsUtil.requestPermission(
                this,
                permissionConfig,
                object : PermissionListener {
                    override fun onPass(list: ArrayList<PermissionBean>) {
                        Toast.makeText(this@MainActivity, "权限全部通过", Toast.LENGTH_LONG).show()
                        setContent(list)
                    }
                    override fun onDenied(list: ArrayList<PermissionBean>, isNotPrompt: Boolean) {
                        Toast.makeText(this@MainActivity, "权限未通过", Toast.LENGTH_LONG).show()
                        setContent(list)
                    }
                },
                list
            )
        }
    }

    private fun setContent(list: ArrayList<PermissionBean>) {
        val sb = StringBuffer()
        list.forEach {
            sb.append(it.des)
            sb.append("\n")
            sb.append(it.permission)
            sb.append("\n")

        }
        findViewById<TextView>(R.id.tvContent).text = sb.toString()

    }
}