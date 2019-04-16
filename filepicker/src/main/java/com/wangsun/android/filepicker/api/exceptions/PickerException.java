package com.wangsun.android.filepicker.api.exceptions;

import java.io.IOException;


public class PickerException extends Exception {

    public PickerException(String msg) { super(msg); }

    public PickerException(IOException e) {
        super(e);
    }
}
