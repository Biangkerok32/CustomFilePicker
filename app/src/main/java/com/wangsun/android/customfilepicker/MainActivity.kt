package com.wangsun.android.customfilepicker

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wangsun.custompicker.api.FilePicker
import com.wangsun.custompicker.api.Picker
import com.wangsun.custompicker.api.callbacks.FilePickerCallback
import com.wangsun.custompicker.api.entity.ChosenFile
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wangsun.custompicker.utils.MimeUtils
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Picker.PICK_FILE){
            filePicker.submit(data)
        }
        else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onFilesChosen(files: MutableList<ChosenFile>?) {
        files?.let {
            println("result: ${it}")

            id_file_path.text = "Path: ${it[0].originalPath}"
            id_mime_type.text = "MimeType: ${it[0].mimeType}"
            id_file_size.text = "File Size(in Bytes): ${it[0].size}"
        }
    }

    override fun onError(message: String?) {
        println("error: $message")
    }


    //********************Custom Function********************************

    private fun initButton() {
        id_pick_file.setOnClickListener { getPermission(1) }
        id_pick_pdf_file.setOnClickListener { getPermission(2) }
        id_pick_audio.setOnClickListener { getPermission(3) }
        id_pick_video.setOnClickListener { getPermission(4) }
        id_pick_only_mp4.setOnClickListener { getPermission(5) }
        id_pick_image.setOnClickListener {getPermission(6)  }
        id_pick_only_jpg.setOnClickListener { getPermission(7) }
    }


    /*By default:
    * filetype take all
    * mimeType take all
    * allowMimeType is true*/
    private fun startFilePicker() {
        filePicker.setFilePickerCallback(this)
            .pickFile()
    }

    private fun startOnlyPdfPicker() {
        filePicker.setFilePickerCallback(this)
            .setMimeTypes(arrayOf("application/pdf"))
            .pickFile()
    }

    /*No need to set mimeType as it will override fileType(you may see videos while recent file list)*/
    private fun startAudioPicker() {
        filePicker.setFilePickerCallback(this)
            .setFileType(MimeUtils.AUDIO_FILE_TYPE)
            .setMimeTypes(arrayOf(MimeUtils.AUDIO_FILE_TYPE))
            .pickFile()
    }

    private fun startVideoPicker() {
        filePicker.setFilePickerCallback(this)
            .setFileType(MimeUtils.VIDEO_FILE_TYPE)
            .setMimeTypes(arrayOf(MimeUtils.VIDEO_FILE_TYPE))
            .pickFile()
    }

    private fun startOnlyMp4Picker() {
        filePicker.setFilePickerCallback(this)
            .setMimeTypes(arrayOf("video/mp4"))
            .pickFile()
    }

    private fun startImagePicker() {
        filePicker.setFilePickerCallback(this)
            .setFileType(MimeUtils.IMAGE_FILE_TYPE)
            .setMimeTypes(arrayOf(MimeUtils.IMAGE_FILE_TYPE))
            .pickFile()
    }

    private fun startOnlyJpgPicker() {
        filePicker.setFilePickerCallback(this)
            .setMimeTypes(arrayOf("image/jpeg"))
            .pickFile()
    }




    @SuppressLint("CheckResult")
    private fun getPermission(value: Int) {
        val rxPermissions = RxPermissions(this)
        rxPermissions
            .request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .subscribe { granted ->
                if (granted) {
                    // All requested permissions are granted
                    when(value){
                        1-> startFilePicker()
                        2-> startOnlyPdfPicker()
                        3-> startAudioPicker()
                        4-> startVideoPicker()
                        5-> startOnlyMp4Picker()
                        6-> startImagePicker()
                        7-> startOnlyJpgPicker()
                    }
                } else {
                    // At least one permission is denied
                    getPermission(value)
                }
            }
    }
}
