package com.wangsun.android.filepicker.sample

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.wangsun.android.filepicker.api.FilePicker
import com.wangsun.android.filepicker.api.Picker
import com.wangsun.android.filepicker.api.callbacks.FilePickerCallback
import com.wangsun.android.filepicker.api.entity.ChosenFile
import com.wangsun.android.filepicker.utils.FileUtils
import com.wangsun.android.filepicker.utils.MimeUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FilePickerCallback {

    private lateinit var filePicker: FilePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filePicker = FilePicker(this)
        initButton()
    }

    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Picker.PICK_FILE){
            filePicker.submit(data)
            println("data: $data")
        }
        else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onFilesChosen(files: MutableList<ChosenFile>?) {
        files?.let {

            //println("result: ${it[0]}")
            //println("success: ${it[0].isSuccess}")

            println("Result: ${it[0].queryUri}")

            if(files.isNotEmpty()){
                id_file_path.text = "Path: ${it[0].originalPath}"
                id_mime_type.text = "MimeType from Uri: ${contentResolver.getType(Uri.parse(it[0].queryUri))}"
                id_file_size.text = "File Size(in Bytes): ${it[0].size}"

                //id_image.setImageURI(Uri.parse(it[0].queryUri))
            }
        }
    }

    override fun onError(message: String?) {
        println("error: $message")
    }


    private fun initButton() {
        id_pick_file.setOnClickListener { getPermission(1) }
        id_pick_pdf_file.setOnClickListener { getPermission(2) }
        id_pick_audio.setOnClickListener { getPermission(3) }
        id_pick_video.setOnClickListener { getPermission(4) }
        id_pick_only_mp4.setOnClickListener { getPermission(5) }
        id_pick_image.setOnClickListener {getPermission(6)  }
        id_pick_only_jpg.setOnClickListener { getPermission(7) }

        id_delete.setOnClickListener {
            FileUtils.deleteDirectory()
        }
    }


    /*By default:
    * filetype take all
    * mimeType take all
    * allowMimeType is true*/
    private fun startDocPicker() {
        filePicker.setFilePickerCallback(this)
            .setFileType(MimeUtils.FileType.DOC)
            .setMimeTypes(MimeUtils.MimeType.DOC)
            .pickFile()
    }

    private fun startOnlyPdfPicker() {
        filePicker.setFilePickerCallback(this)
            .setFileType(MimeUtils.FileType.DOC)
            .setMimeTypes(MimeUtils.MimeType.ONLY_PDF)
            .pickFile()
    }

    //AUDIO
    private fun startAudioPicker() {
        filePicker.setFilePickerCallback(this)
            .setFileType(MimeUtils.FileType.AUDIO)
            .setMimeTypes(MimeUtils.MimeType.AUDIO)
            .pickFile()
    }

    private fun startVideoPicker() {
        filePicker.setFilePickerCallback(this)
            .setFileType(MimeUtils.FileType.VIDEO)
            .setMimeTypes(MimeUtils.MimeType.VIDEO)
            .pickFile()
    }

    private fun startOnlyMp4Picker() {
        filePicker.setFilePickerCallback(this)
            .setFileType(MimeUtils.FileType.AUDIO)
            .setMimeTypes(MimeUtils.MimeType.ONLY_MP4)
            .pickFile()
    }

    private fun startImagePicker() {
        filePicker.setFilePickerCallback(this)
            .allowMultipleFiles(true)
            .setFileType(MimeUtils.FileType.IMAGE)
            .setMimeTypes(MimeUtils.MimeType.IMAGE)
            .pickFile()
    }

    private fun startOnlyJpgPicker() {
        filePicker.setFilePickerCallback(this)
            .setFileType(MimeUtils.FileType.IMAGE)
            .setMimeTypes(MimeUtils.MimeType.ONLY_JPEG)
            .pickFile()
    }


    @SuppressLint("CheckResult")
    private fun getPermission(value: Int) {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    when(value){
                        1-> startDocPicker()
                        2-> startOnlyPdfPicker()
                        3-> startAudioPicker()
                        4-> startVideoPicker()
                        5-> startOnlyMp4Picker()
                        6-> startImagePicker()
                        7-> startOnlyJpgPicker()
                    }
                }
                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(this@MainActivity, "Need permission to do this task.", Toast.LENGTH_SHORT).show()
                }
                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }
}
