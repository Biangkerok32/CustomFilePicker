package com.wangsun.custompicker.utils;

public class MimeUtils {


    /*File types: to set type for intent*/
    public final static String DEFAULT_FILE_TYPE = "*/*";
    public final static String VIDEO_FILE_TYPE = "video/*";
    public final static String AUDIO_FILE_TYPE = "audio/*";
    public final static String IMAGE_FILE_TYPE = "image/*";
    public final static String DOC_FILE_TYPE = "application/*";



    /*Mime Type:*/
    public final static String DEFAULT_MIME_TYPE[] = {"*/*"};
    public final static String VIDEO_MIME_TYPE[] = {"video/*"};
    public final static String AUDIO_MIME_TYPE[] = {"audio/*"};
    public final static String IMAGE_MIME_TYPE[] = {"image/*"};

    /*Only GPG/JPEG File*/
    public final static String JPG_MIME_TYPE[] = {"image/jpeg"};
    public final static String MP3_MIME_TYPE[] = {"audio/mpeg"};
    public final static String MP4_MIME_TYPE[] = {"video/mp4"};

    /*check this lick for more mimeTypes
    * https://www.lifewire.com/file-extensions-and-mime-types-3469109*/



    /*Custom MimeTypes*/
    public final static String DOC_MIME_TYPE[] = {
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
            "text/plain",
            "application/pdf",
            "application/zip"
    };


}
