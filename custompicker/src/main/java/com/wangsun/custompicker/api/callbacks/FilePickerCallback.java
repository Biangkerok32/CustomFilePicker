package com.wangsun.custompicker.api.callbacks;

import com.wangsun.custompicker.api.entity.ChosenFile;

import java.util.List;


public interface FilePickerCallback{
    void onFilesChosen(List<ChosenFile> files);
    void onError(String message);
}
