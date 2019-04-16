package com.wangsun.android.filepicker.utils;

/**
 * Created by WANGSUN on 14-Mar-19.
 */
public class MimeUtils {

    /************************************************************
     *       File types: to set type for intent
     **************************************************************/
    public static class FileType{
        public final static String DEFAULT = "*/*";
        public final static String VIDEO = "video/*";
        public final static String AUDIO = "audio/*";
        public final static String IMAGE = "image/*";
        public final static String DOC = "application/*";

    }

    /************************************************************
     *                  MIME types
     **************************************************************/
    public static class MimeType{
        public final static String[] DEFAULT = {"*/*"};
        public final static String[] VIDEO = {"video/*"};
        public final static String[] AUDIO = {"audio/*"};
        public final static String[] IMAGE = {"image/*"};

        /*Custom MimeTypes*/
        public final static String[] DOC = {
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

        /*******************************
         *     specific mime type
         *******************************/
        public final static String[] ONLY_JPEG = {"image/jpeg"};
        public final static String[] ONLY_MP3 = {"audio/mpeg"};
        public final static String[] ONLY_MP4 = {"video/mp4"};
        public final static String[] ONLY_PDF = {"application/pdf"};

        /*check this link for more mimeTypes
         * https://www.lifewire.com/file-extensions-and-mime-types-3469109*/
    }

}
