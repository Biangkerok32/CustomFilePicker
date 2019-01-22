package com.wangsun.custompicker.api.callbacks;

import com.wangsun.custompicker.api.entity.ChosenFile;

import java.util.List;


public interface FilePickerCallback extends PickerCallback {
    void onFilesChosen(List<ChosenFile> files);
}
