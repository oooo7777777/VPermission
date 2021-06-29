package com.v.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.btAlone).setOnClickListener {
            PermissionsUtil.requestPermission(
                context = this,
                beanRefuse = PermissionHintBean("警告", "部分功能无法正常使用，请允许以下权限。", "取消", "去设置"),
                isShowRefuseDialog = false,
                listener = object : PermissionListener {
                    override fun onPass(list: ArrayList<PermissionBean>) {

                        Toast.makeText(this@MainActivity, "权限全部通过", Toast.LENGTH_LONG).show()
                        setContent(list)
                    }

                    override fun onDenied(list: ArrayList<PermissionBean>, isNotPrompt: Boolean) {

                        Toast.makeText(this@MainActivity, "权限未通过", Toast.LENGTH_LONG).show()
                        setContent(list)
                    }

                },
                permissions = *arrayOf(Manifest.permission.CALL_PHONE)
            )


        }

        findViewById<Button>(R.id.btGroup).setOnClickListener {
            PermissionsUtil.requestPermission(
                this,
                null,
                null,
                true,
                true,
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
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
            )
        }
    }

    private fun setContent(list: ArrayList<PermissionBean>) {
        val sb = StringBuffer()
        list.forEach {
            sb.append(it.title)
            sb.append("\n")
            sb.append(it.permission)
            sb.append("\n")

        }
        findViewById<TextView>(R.id.tvContent).text = sb.toString()

    }
}