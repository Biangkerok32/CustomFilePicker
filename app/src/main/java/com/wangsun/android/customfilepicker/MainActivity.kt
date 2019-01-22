package com.wangsun.android.customfilepicker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kbeanie.multipicker.api.FilePicker
import com.kbeanie.multipicker.api.Picker
import com.kbeanie.multipicker.api.callbacks.FilePickerCallback
import com.kbeanie.multipicker.api.entity.ChosenFile
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FilePickerCallback {

    val mMimeTypes = arrayOf(
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
        "text/plain",
        "application/pdf",
        "application/zip"
    )


    lateinit var filePicker: FilePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filePicker = FilePicker(this)
        initButton()
    }

    override fun onFilesChosen(files: MutableList<ChosenFile>?) {
        files?.let {
            println("result: ${it}")
        }
    }

    override fun onError(message: String?) {
        println("error: $message")
    }


    //********************Custom Function********************************

    private fun initButton() {
        id_pick_file.setOnClickListener { getPermission() }
    }

    private fun startFilePicker() {
        filePicker.allowMultiple()
            .setFilePickerCallback(this)
            .setMimeTypes(mMimeTypes)
            .pickFile()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Picker.PICK_FILE){
            filePicker.submit(data)
        }
        else
            super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("CheckResult")
    private fun getPermission() {
        val rxPermissions = RxPermissions(this)
        rxPermissions
            .request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .subscribe { granted ->
                if (granted) {
                    // All requested permissions are granted
                    startFilePicker()
                } else {
                    // At least one permission is denied
                    getPermission()
                }
            }
    }
}
