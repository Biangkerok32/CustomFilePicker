package com.wangsun.android.filepicker.api.callbacks;

import com.wangsun.android.filepicker.api.entity.ChosenFile;

import java.util.List;


public interface FilePickerCallback{
    void onFilesChosen(List<ChosenFile> files);
    void onError(String message);
}
