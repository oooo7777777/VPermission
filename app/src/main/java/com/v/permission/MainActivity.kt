package com.v.permission

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.v.permission.listener.VPermissionsListener


/**
 * @Author : ww
 * desc    :
 * time    : 2021/6/25 17:10
 */
class MainActivity : AppCompatActivity() {


    private val permissionsConfig by lazy {
        VPermissionsConfig().apply {
            //提示权限弹窗 如果不传会使用默认的
            beanFirst = VPermissionsHintBean("提示", "部分功能无法正常使用，请允许以下权限。", "取消", "确定")
            //权限拒绝后再次弹窗 如果不传会使用默认的
            beanRefuse = VPermissionsHintBean("警告", "因为你拒绝了权限，导致部分功能无法正常使用，请允许以下权限。", "取消", "去授权")
            //每个权限的文案 是使用详细的还是模糊的  详细的为每个权限的文案 模糊的为每一组文案
            isTipDetail = true
            //拒绝权限点击了永不显示  是否弹出第二次弹窗
            isShowRefuseDialog = true
        }
    }

    private val list by lazy {
        ArrayList<VPermissionsBean>().apply {
            add(VPermissionsBean("需要使用相机权限，以正常使用拍照、视频等功能。", Manifest.permission.CAMERA))
            add(VPermissionsBean("需要使用麦克风权限，以正常使用语音等功能。", Manifest.permission.RECORD_AUDIO))
            add(
                VPermissionsBean(
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

            VPermissions.Builder()
                .setPermission(Manifest.permission.RECORD_AUDIO)
                .callback(object : VPermissionsListener {
                    override fun onPass(list: ArrayList<VPermissionsBean>) {
                        setContent("权限全部通过", list)
                    }

                    override fun onDenied(list: ArrayList<VPermissionsBean>) {
                        setContent("权限未通过", list)
                    }

                    override fun onNeverRemind(list: ArrayList<VPermissionsBean>) {
                        setContent("永不提醒的权限", list)
                    }

                })
                .create(this)

        }

        findViewById<Button>(R.id.bt1).setOnClickListener {

            VPermissions.Builder()
                .setConfig(permissionsConfig)
                .setPermission(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .callback(object : VPermissionsListener {
                    override fun onPass(list: ArrayList<VPermissionsBean>) {
                        setContent("权限全部通过", list)
                    }

                    override fun onDenied(list: ArrayList<VPermissionsBean>) {
                        setContent("权限未通过", list)
                    }

                    override fun onNeverRemind(list: ArrayList<VPermissionsBean>) {
                        setContent("权限永不提醒", list)
                    }

                })
                .create(this)


        }


        findViewById<Button>(R.id.bt2).setOnClickListener {


            VPermissions.Builder()
                .setConfig(permissionsConfig)
                .setPermission(list)
                .callback(object : VPermissionsListener {
                    override fun onPass(list: ArrayList<VPermissionsBean>) {
                        setContent("权限全部通过", list)
                    }

                    override fun onDenied(list: ArrayList<VPermissionsBean>) {
                        setContent("权限未通过", list)
                    }

                    override fun onNeverRemind(list: ArrayList<VPermissionsBean>) {
                        setContent("权限永不提醒", list)
                    }

                })
                .create(this)

        }


        findViewById<Button>(R.id.bt3).setOnClickListener {

            VPermissions.Builder()
                .setConfig(VPermissionsConfig().apply {
                    isShowRefuseDialog = false
                })
                .setPermission(Manifest.permission.CAMERA)
                .callback(object : VPermissionsListener {
                    override fun onPass(list: ArrayList<VPermissionsBean>) {
                        setContent("权限全部通过", list)
                    }

                    override fun onDenied(list: ArrayList<VPermissionsBean>) {
                        setContent("权限未通过", list)
                    }

                    override fun onNeverRemind(list: ArrayList<VPermissionsBean>) {
                        setContent("永不提醒的权限", list)
                    }

                })
                .create(this)
        }
    }

    private fun setContent(title: String, list: ArrayList<VPermissionsBean>) {
        val sb = StringBuffer()
        list.forEach {
            sb.append(title)
            sb.append("\t")
            sb.append(it.des)
            sb.append("\t")
            sb.append(it.permission)
            sb.append("\n")
        }
        Log.i("VPermissions", sb.toString())

    }


}